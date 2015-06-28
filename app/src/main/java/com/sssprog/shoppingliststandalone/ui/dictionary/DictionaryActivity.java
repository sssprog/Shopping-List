package com.sssprog.shoppingliststandalone.ui.dictionary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.database.CategoryModel;
import com.sssprog.shoppingliststandalone.api.database.ModelWithId;
import com.sssprog.shoppingliststandalone.api.database.ModelWithName;
import com.sssprog.shoppingliststandalone.api.database.QuantityUnitModel;
import com.sssprog.shoppingliststandalone.api.services.BaseModelService;
import com.sssprog.shoppingliststandalone.api.services.CategoryService;
import com.sssprog.shoppingliststandalone.api.services.QuantityUnitService;
import com.sssprog.shoppingliststandalone.dialogs.PromptDialogFragment;
import com.sssprog.shoppingliststandalone.mvp.PresenterClass;
import com.sssprog.shoppingliststandalone.ui.BaseMvpActivity;
import com.sssprog.shoppingliststandalone.utils.ViewStateSwitcher;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@PresenterClass(DictionaryPresenter.class)
public class DictionaryActivity<Model extends ModelWithId & ModelWithName>
        extends BaseMvpActivity<DictionaryPresenter<Model>> implements PromptDialogFragment.PromptDialogListener {

    private static final String PARAM_TYPE = "PARAM_TYPE";

    private static final int DIALOG_ADD = 0;
    private static final int DIALOG_RENAME = 1;
    private static final String DIALOG_PARAM_ID = "DIALOG_PARAM_ID";

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    private DictionaryAdapter<Model> adapter;
    private ViewStateSwitcher stateSwitcher;
    private Model lastDeletedItem;
    private Class<Model> modelClass;

    public static Intent createIntent(Context context, DictionaryType type) {
        return new Intent(context, DictionaryActivity.class)
                .putExtra(PARAM_TYPE, type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        modelClass = (Class<Model>) (getType() == DictionaryType.CATEGORY ? CategoryModel.class : QuantityUnitModel.class);
        setTitle(getType() == DictionaryType.CATEGORY ? R.string.categories : R.string.quantity_units);

        getPresenter().setService((BaseModelService<Model>) (getType() == DictionaryType.CATEGORY ?
                CategoryService.getInstance() :
                QuantityUnitService.getInstance()));

        adapter = new DictionaryAdapter<>(this, itemListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        initSwipeToDelete();

        stateSwitcher = ViewStateSwitcher.createStandardSwitcher(this, recyclerView);
        ViewStateSwitcher.addTextState(stateSwitcher, ViewStateSwitcher.STATE_EMPTY, R.string.no_items);
        stateSwitcher.switchToLoading(false);

        stateSwitcher.switchToLoading(false);
        getPresenter().loadItems();
    }

    private DictionaryType getType() {
        return (DictionaryType) getIntent().getExtras().getSerializable(PARAM_TYPE);
    }

    private void initSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                DictionaryViewHolder holder = (DictionaryViewHolder) viewHolder;
                holder.move(dX);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                lastDeletedItem = adapter.removeItem(position);
                updateListState();
                getPresenter().deleteItem(lastDeletedItem);
                Snackbar.make(findViewById(android.R.id.content), R.string.item_was_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, undoDeletion)
                        .show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private View.OnClickListener undoDeletion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean canceled = getPresenter().cancelDeletion(lastDeletedItem);
            if (canceled) {
                adapter.addItem(lastDeletedItem);
                updateListState();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().finishDeletion();
    }

    void onItemsLoaded(List<Model> items) {
        adapter.setItems(items);
        updateListState();
    }

    private void updateListState() {
        stateSwitcher.switchToState(adapter.isEmpty() ? ViewStateSwitcher.STATE_EMPTY : ViewStateSwitcher.STATE_MAIN, true);
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        new PromptDialogFragment.PromptDialogBuilder(this)
                .setRequestCode(DIALOG_ADD)
                .setTitle(R.string.dictionary_create_dialog_title)
                .setCapSentences(capName())
                .setPositiveButtonText(R.string.save)
                .setNegativeButtonText(R.string.cancel)
                .build()
                .show(getSupportFragmentManager(), null);
    }

    private DictionaryAdapter.DictionaryAdapterListener<Model> itemListener =
            new DictionaryAdapter.DictionaryAdapterListener<Model>() {
                @Override
                public void onItemLongClick(Model item) {
                    new PromptDialogFragment.PromptDialogBuilder(DictionaryActivity.this)
                            .setRequestCode(DIALOG_RENAME)
                            .setParams(createDialogParams(item))
                            .setInitialValue(item.getName())
                            .setTitle(R.string.dictionary_rename_dialog_title)
                            .setCapSentences(capName())
                            .setPositiveButtonText(R.string.rename)
                            .setNegativeButtonText(R.string.cancel)
                            .build()
                            .show(getSupportFragmentManager(), null);
                }
            };

    private boolean capName() {
        return getType() == DictionaryType.CATEGORY;
    }

    @SuppressLint("NewApi")
    private Model createModel() {
        try {
            return modelClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPromptDialogPositive(int requestCode, String value, Bundle params) {
        switch (requestCode) {
            case DIALOG_ADD:
                Model item = createModel();
                item.setName(value);
                getPresenter().addItem(item);
                break;
            case DIALOG_RENAME:
                item = adapter.getItem(extractId(params));
                if (item != null) {
                    item.setName(value);
                    adapter.itemsUpdated();
                    getPresenter().saveItem(item);
                }
                break;
        }
    }

    private Bundle createDialogParams(Model item) {
        Bundle params = new Bundle();
        params.putLong(DIALOG_PARAM_ID, item.getId());
        return params;
    }

    private long extractId(Bundle params) {
        return params.getLong(DIALOG_PARAM_ID);
    }

    public enum DictionaryType {
        CATEGORY, QUANTITY_UNIT
    }

}
