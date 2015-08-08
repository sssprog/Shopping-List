package com.sssprog.shoppingliststandalone.ui.itemeditor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.collect.Iterables;
import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.database.CategoryModel;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.api.database.ModelWithId;
import com.sssprog.shoppingliststandalone.api.database.QuantityUnitModel;
import com.sssprog.shoppingliststandalone.mvp.PresenterClass;
import com.sssprog.shoppingliststandalone.ui.BaseMvpActivity;
import com.sssprog.shoppingliststandalone.ui.dictionary.DictionaryActivity;
import com.sssprog.shoppingliststandalone.utils.NumberUtils;
import com.sssprog.shoppingliststandalone.utils.ViewUtils;

import java.math.BigDecimal;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@PresenterClass(ItemEditorPresenter.class)
public class ItemEditorActivity extends BaseMvpActivity<ItemEditorPresenter> {

    private static final String PARAM_ITEM_ID = "PARAM_ITEM_ID";
    private static final String PARAM_HISTORY_ITEM = "PARAM_HISTORY_ITEM";

    private static final String PARAM_ITEM_LOADED_TO_VIEWS = "PARAM_ITEM_LOADED_TO_VIEWS";
    private static final String PARAM_CATEGORY = "PARAM_CATEGORY";
    private static final String PARAM_QUANTITY_UNIT = "PARAM_QUANTITY_UNIT";

    private static final int DELAY_BEFORE_STARTING_CONTINUES_QUANTITY_CHANGE = 600;
    private static final int DELAY_BETWEEN_CONTINUES_QUANTITY_CHANGES = 35;

    @InjectView(R.id.itemName)
    EditText itemNameView;
    @InjectView(R.id.quantityMinusButton)
    ImageButton quantityMinusButton;
    @InjectView(R.id.quantityPlusButton)
    ImageButton quantityPlusButton;
    @InjectView(R.id.quantity)
    EditText quantityView;
    @InjectView(R.id.quantitySpinner)
    Spinner quantitySpinner;
    @InjectView(R.id.price)
    EditText priceView;
    @InjectView(R.id.totalCost)
    TextView totalCost;
    @InjectView(R.id.categorySpinner)
    Spinner categorySpinner;
    @InjectView(R.id.note)
    EditText noteView;
    @InjectView(R.id.quantityContainer)
    View quantityContainer;
    @InjectView(R.id.noteContainer)
    View noteContainer;

    private ItemModel item;
    private MenuItem menuSave;
    private boolean isItemLoadedToViews;
    private long categoryId;
    private long quantityUnitId;
    private Handler handler;

    public static Intent createIntent(Context context, long itemId, boolean isHistoryItem) {
        return new Intent(context, ItemEditorActivity.class)
                .putExtra(PARAM_ITEM_ID, itemId)
                .putExtra(PARAM_HISTORY_ITEM, isHistoryItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_editor);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        if (savedInstanceState != null) {
            isItemLoadedToViews = savedInstanceState.getBoolean(PARAM_ITEM_LOADED_TO_VIEWS);
            categoryId = savedInstanceState.getLong(PARAM_CATEGORY);
            quantityUnitId = savedInstanceState.getLong(PARAM_QUANTITY_UNIT);
        }

        initViews();
    }

    private long getItemId() {
        return getIntent().getExtras().getLong(PARAM_ITEM_ID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PARAM_ITEM_LOADED_TO_VIEWS, isItemLoadedToViews);
        outState.putLong(PARAM_CATEGORY, categoryId);
        outState.putLong(PARAM_QUANTITY_UNIT, quantityUnitId);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Loading data here because categories and quantity units can be changed
        getPresenter().loadData(getItemId());
    }

    private void initViews() {
        if (getIntent().getExtras().getBoolean(PARAM_HISTORY_ITEM)) {
            quantityContainer.setVisibility(View.GONE);
            noteContainer.setVisibility(View.GONE);
            totalCost.setVisibility(View.GONE);
        }
        quantityPlusButton.setImageDrawable(ViewUtils.getGreyIconDrawable(this, R.drawable.ic_add_white_24dp));
        itemNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateMenuState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initTotalCostView();
    }

    private void initTotalCostView() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTotalCost();
            }
        };
        quantityView.addTextChangedListener(textWatcher);
        priceView.addTextChangedListener(textWatcher);
        updateTotalCost();
        initPlusMinusButtons();
    }

    private void updateTotalCost() {
        BigDecimal quantity = NumberUtils.stringToQuantity(quantityView.getText().toString());
        BigDecimal price = NumberUtils.stringToPrice(priceView.getText().toString());
        BigDecimal total = NumberUtils.roundPrice(price.multiply(quantity));
        totalCost.setText(getString(R.string.total_cost, NumberUtils.priceWithCurrency(total)));
    }

    private void initPlusMinusButtons() {
        handler = new Handler();
        View.OnClickListener plusMinusClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onQuantityChanged((BigDecimal) v.getTag());
            }
        };
        quantityMinusButton.setTag(new BigDecimal(-1));
        quantityPlusButton.setTag(BigDecimal.ONE);
        quantityMinusButton.setOnClickListener(plusMinusClickListener);
        quantityPlusButton.setOnClickListener(plusMinusClickListener);

        View.OnTouchListener touchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    quantityRunnable.quantityDelta = (BigDecimal) v.getTag();
                    handler.postDelayed(quantityRunnable, DELAY_BEFORE_STARTING_CONTINUES_QUANTITY_CHANGE);
                    break;
                case MotionEvent.ACTION_UP:
                    handler.removeCallbacks(quantityRunnable);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(quantityRunnable);
                    break;
            }
            return false;
        };
        quantityMinusButton.setOnTouchListener(touchListener);
        quantityPlusButton.setOnTouchListener(touchListener);
    }

    private class QuantityRunnable implements Runnable {

        public BigDecimal quantityDelta;

        @Override
        public void run() {
            onQuantityChanged(quantityDelta);
            handler.postDelayed(this, DELAY_BETWEEN_CONTINUES_QUANTITY_CHANGES);
        }
    }

    private final QuantityRunnable quantityRunnable = new QuantityRunnable();

    private void onQuantityChanged(BigDecimal quantityDelta) {
        BigDecimal quantity = NumberUtils.stringToQuantity(quantityView.getText().toString());
        quantity = quantity.add(quantityDelta);
        if (NumberUtils.numberGreater(quantity, 0)) {
            quantityView.setText(NumberUtils.quantityToString(quantity));
        }
    }

    private void updateMenuState() {
        if (menuSave != null) {
            menuSave.setEnabled(item != null && !itemNameView.getText().toString().trim().isEmpty());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_editor_activity, menu);
        menuSave = menu.findItem(R.id.save);
        updateMenuState();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save() {
        item.setName(itemNameView.getText().toString().trim());
        item.setQuantity(NumberUtils.stringToQuantity(quantityView.getText().toString()));
        item.setPrice(NumberUtils.stringToPrice(priceView.getText().toString()));
        item.setNote(noteView.getText().toString());
        QuantityUnitModel unit = (QuantityUnitModel) quantitySpinner.getSelectedItem();
        item.setQuantityUnit(unit.getId() > 0 ? unit : null);
        CategoryModel category = (CategoryModel) categorySpinner.getSelectedItem();
        item.setCategory(category.getId() > 0 ? category : null);
        getPresenter().save(item);
    }

    void onItemSaved() {
        setResult(RESULT_OK);
        finish();
    }

    void onDataLoaded(ItemModel item, List<CategoryModel> categories, List<QuantityUnitModel> units) {
        this.item = item;
        if (!isItemLoadedToViews) {
            isItemLoadedToViews = true;
            itemNameView.setText(item.getName());
            quantityView.setText(NumberUtils.quantityToString(item.getQuantity()));
            priceView.setText(NumberUtils.priceToString(item.getPrice()));
            noteView.setText(item.getNote());

            categoryId = item.getCategory() != null ? item.getCategory().getId() : 0;
            quantityUnitId = item.getQuantityUnit() != null ? item.getQuantityUnit().getId() : 0;
        }
        setUnits(units);
        setCategories(categories);
    }

    private void setUnits(List<QuantityUnitModel> units) {
        quantitySpinner.setAdapter(ViewUtils.getSpinnerAdapter(this, units));
        int index = findPosition(units, quantityUnitId);
        if (index < 0) { // previous quantity unit was removed
            quantityUnitId = 0;
            index = 0;
        }
        quantitySpinner.setSelection(index);
        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                quantityUnitId = ((QuantityUnitModel) quantitySpinner.getSelectedItem()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setCategories(List<CategoryModel> categories) {
        categorySpinner.setAdapter(ViewUtils.getSpinnerAdapter(this, categories));
        int index = findPosition(categories, categoryId);
        if (index < 0) { // previous category was removed
            categoryId = 0;
            index = 0;
        }
        categorySpinner.setSelection(index);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = ((CategoryModel) categorySpinner.getSelectedItem()).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private <T extends ModelWithId> int findPosition(List<T> list, final long id) {
        return Iterables.indexOf(list, input -> input.getId() == id);
    }

    @OnClick(R.id.editQuantityUnitsButton)
    public void onEditQuantityUnitsClicked() {
        startActivity(DictionaryActivity.createIntent(this,
                DictionaryActivity.DictionaryType.QUANTITY_UNIT));
    }

    @OnClick(R.id.editCategoriesButton)
    public void onEditCategoryClicked() {
        startActivity(DictionaryActivity.createIntent(this,
                DictionaryActivity.DictionaryType.CATEGORY));
    }

}
