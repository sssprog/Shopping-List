package com.sssprog.shoppingliststandalone.ui.history;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.mvp.PresenterClass;
import com.sssprog.shoppingliststandalone.ui.BaseMvpActivity;
import com.sssprog.shoppingliststandalone.utils.ViewStateSwitcher;
import com.sssprog.shoppingliststandalone.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.InjectView;

@PresenterClass(HistoryPresenter.class)
public class HistoryActivity extends BaseMvpActivity<HistoryPresenter> {

    private static final String PARAM_LIST_ID = "PARAM_LIST_ID";
    private static final String PARAM_SELECTED_ITEMS = "PARAM_SELECTED_ITEMS";

    private static final String STATE_ITEM_IS_IN_LIST = "item_is_in_list";
    private static final String STATE_ADD_ITEM = "add_item";

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;
    @InjectView(R.id.newItemView)
    EditText newItemView;
    @InjectView(R.id.clearTextButton)
    ImageButton clearTextButton;
    @InjectView(R.id.micButton)
    ImageButton micButton;

    private TextView itemInListText;
    private TextView addItemText;

    private HistoryAdapter adapter;
    private ViewStateSwitcher stateSwitcher;

    private List<ItemModel> history = new ArrayList<>();
    private final Set<String> listItems = new HashSet<>();
    private ItemModel lastDeletedItem;

    public static Intent createIntent(Context context, long listId) {
        return new Intent(context, HistoryActivity.class)
                .putExtra(PARAM_LIST_ID, listId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        HashSet<Long> selectedItems = savedInstanceState != null ?
                (HashSet<Long>) savedInstanceState.getSerializable(PARAM_SELECTED_ITEMS) :
                new HashSet<Long>();
        adapter = new HistoryAdapter(this, selectedItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        initSwipeToDelete();

        initStateSwitcher();
        initNewItemView();
        stateSwitcher.switchToLoading(false);
        getPresenter().setListId(getIntent().getExtras().getLong(PARAM_LIST_ID));
        getPresenter().loadItems();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PARAM_SELECTED_ITEMS, adapter.getSelectedItems());
    }

    private void initStateSwitcher() {
        stateSwitcher = ViewStateSwitcher.createStandardSwitcher(this, recyclerView);
        ViewStateSwitcher.addTextState(stateSwitcher, ViewStateSwitcher.STATE_EMPTY, R.string.no_items);
        itemInListText = ViewStateSwitcher.addTextState(stateSwitcher, STATE_ITEM_IS_IN_LIST, "");

        View view = stateSwitcher.inflateStateView(R.layout.add_item_state);
        addItemText = (TextView) view.findViewById(R.id.label);
        Button button = (Button) view.findViewById(R.id.addItemButton);
        ViewCompat.setBackgroundTintList(button, getResources().getColorStateList(R.color.accent_button));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToList();
            }
        });
        stateSwitcher.addViewState(STATE_ADD_ITEM, view);
    }

    private void initSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                HistoryViewHolder holder = (HistoryViewHolder) viewHolder;
                holder.move(dX);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                lastDeletedItem = adapter.removeItem(position);
                history.remove(lastDeletedItem);
                updateListAndState();
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
                history.add(lastDeletedItem);
                sortHistory();
                updateListAndState();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().finalizeDeletion();
    }

    private void initNewItemView() {
        newItemView.setImeActionLabel(null, EditorInfo.IME_ACTION_DONE);
        newItemView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addItemToList();
                return true;
            }
        });
        newItemView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateClearTextButton();
                updateListAndState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearTextButton.setImageDrawable(ViewUtils.getGreyIconDrawable(this, R.drawable.ic_close_white_24dp));
        updateClearTextButton();
        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newItemView.setText("");
            }
        });
    }

    private void updateClearTextButton() {
        clearTextButton.setVisibility(newItemView.getText().length() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private boolean isNewItem() {
        final String text = newItemView.getText().toString().trim().toLowerCase();
        boolean inHistory = Iterables.any(history, new Predicate<ItemModel>() {
            @Override
            public boolean apply(ItemModel input) {
                return text.equals(input.getName().toLowerCase());
            }
        });
        return !inHistory && !listItems.contains(text);
    }

    private void addItemToList() {
        if (!isNewItem()) {
            return;
        }
        ItemModel item = new ItemModel();
        item.setName(newItemView.getText().toString().trim());
        getPresenter().addItem(item);
        newItemView.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(newItemView.getWindowToken(), 0);
    }

    void onItemsAdded() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addItems();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addItems() {
        Collection<ItemModel> items = Collections2.filter(history, new Predicate<ItemModel>() {
            @Override
            public boolean apply(ItemModel input) {
                return adapter.getSelectedItems().contains(input.getId());
            }
        });
        getPresenter().addItemsToList(items);
    }

    void onItemsLoaded(List<ItemModel> history, List<ItemModel> listItems) {
        this.history = history;
        sortHistory();
        this.listItems.clear();
        this.listItems.addAll(Collections2.transform(listItems, new Function<ItemModel, String>() {
            @Override
            public String apply(ItemModel input) {
                return input.getName().toLowerCase();
            }
        }));
        updateListAndState();
    }

    void onItemAdded(ItemModel item) {
        adapter.selectItem(item);
        history.add(item);
        sortHistory();
        updateListAndState();
    }

    private void sortHistory() {
        Collections.sort(history, new Comparator<ItemModel>() {
            @Override
            public int compare(ItemModel lhs, ItemModel rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
    }

    private void updateListAndState() {
        String text = newItemView.getText().toString();
        itemInListText.setText(Html.fromHtml(getString(R.string.item_is_in_list_already, text)));
        addItemText.setText(Html.fromHtml(getString(R.string.add_item_to_list, text)));
        updateList();
        if (adapter.getItemCount() == 0) {
            if (text.isEmpty()) {
                stateSwitcher.switchToEmpty(true);
            } else {
                stateSwitcher.switchToState(listItems.contains(text.toLowerCase()) ? STATE_ITEM_IS_IN_LIST :
                        STATE_ADD_ITEM, true);
            }
        } else {
            stateSwitcher.switchToMain(true);
        }
    }

    private void updateList() {
        final String query = newItemView.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) {
            adapter.setItems(history, "");
            return;
        }
        Iterable<ItemModel> items = Iterables.filter(history, new Predicate<ItemModel>() {
            @Override
            public boolean apply(ItemModel item) {
                String[] words = item.getName().toLowerCase().split("\\W");
                for (String word : words) {
                    if (word.startsWith(query)) {
                        return true;
                    }
                }
                return false;
            }
        });
        adapter.setItems(Lists.newArrayList(items), query);
    }

}
