package com.sssprog.shoppingliststandalone.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.events.ListChangedEvent;
import com.sssprog.shoppingliststandalone.mvp.PresenterClass;
import com.sssprog.shoppingliststandalone.ui.BaseMvpActivity;
import com.sssprog.shoppingliststandalone.ui.history.HistoryActivity;
import com.sssprog.shoppingliststandalone.ui.itemeditor.ItemEditorActivity;
import com.sssprog.shoppingliststandalone.ui.settings.SettingsActivity;
import com.sssprog.shoppingliststandalone.utils.NumberUtils;
import com.sssprog.shoppingliststandalone.utils.Prefs;
import com.sssprog.shoppingliststandalone.utils.ViewStateSwitcher;
import com.sssprog.shoppingliststandalone.utils.ViewUtils;

import java.math.BigDecimal;
import java.util.List;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

@PresenterClass(MainPresenter.class)
public class MainActivity extends BaseMvpActivity<MainPresenter> {

    private static final int REQUEST_ADD_ITEMS = 0;
    private static final int REQUEST_EDIT_ITEM = 1;

    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.navigationView)
    NavigationView navigationView;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    @InjectView(R.id.totalCostContainer)
    View totalCostContainer;
    @InjectView(R.id.totalCost)
    TextView totalCost;
    @InjectView(R.id.inCartCost)
    TextView inCartCost;

    private ActionBarDrawerToggle drawerToggle;
    private ListsFragment listsFragment;
    private ViewStateSwitcher stateSwitcher;
    private ListAdapter adapter;
    private ItemModel lastDeletedItem;
    private Snackbar deletionSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ListAdapter(this, new AdapterListener());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        initSwipeToDelete();

        initStateSwitcher();
        stateSwitcher.switchToLoading(false);

        listsFragment = ViewUtils.findOrAddFragment(this, ListsFragment.class);
        listsFragment.setData(navigationView.getMenu());
        setupNavigationView();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Settings could have been updated and adapter depends on settings
        adapter.update();
    }

    @Override
    protected int getToolbarShadowContainerId() {
        return R.id.contentContainer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_items:
                addItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addItems() {
        startActivityForResult(HistoryActivity.createIntent(this, listsFragment.getCurrentList().getId()),
                REQUEST_ADD_ITEMS);
    }

    private void initStateSwitcher() {
        stateSwitcher = ViewStateSwitcher.createStandardSwitcher(this, recyclerView);
        View view = stateSwitcher.inflateStateView(R.layout.list_empty_state);
        Button button = (Button) view.findViewById(R.id.addItemsButton);
        ViewCompat.setBackgroundTintList(button, getResources().getColorStateList(R.color.accent_button));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItems();
            }
        });
        stateSwitcher.addViewState(ViewStateSwitcher.STATE_EMPTY, view);
    }

    private void setupNavigationView() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawerLayout.setDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    default:
                        listsFragment.onNavigationItemSelected(item);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void initSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CategoryViewHolder) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                ListViewHolder holder = (ListViewHolder) viewHolder;
                holder.move(dX);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                lastDeletedItem = adapter.removeItem(position);
                updateListState();
                getPresenter().deleteItem(lastDeletedItem);
                deletionSnackbar = Snackbar.make(findViewById(android.R.id.content), R.string.item_was_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, undoDeletion);
                deletionSnackbar.show();
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void loadList() {
        stateSwitcher.switchToLoading(false);
        getPresenter().loadItems(listsFragment.getCurrentList().getId());
    }

    void onItemsLoaded(List<ItemModel> items) {
        adapter.setItems(items);
        updateListState();
    }

    private void updateListState() {
        stateSwitcher.switchToState(adapter.isEmpty() ? ViewStateSwitcher.STATE_EMPTY : ViewStateSwitcher.STATE_MAIN, true);
    }

    public void onEventMainThread(ListChangedEvent event) {
        loadList();
        if (deletionSnackbar != null) {
            deletionSnackbar.dismiss();
            deletionSnackbar = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_ADD_ITEMS:
            case REQUEST_EDIT_ITEM:
                if (listsFragment.getCurrentList() != null) {
                    loadList();
                }
                break;
        }
    }

    private class AdapterListener implements ListAdapter.ListAdapterListener {

        @Override
        public boolean onItemLongClick(ItemModel item) {
            startActivityForResult(ItemEditorActivity.createIntent(MainActivity.this, item.getId(), false),
                    REQUEST_EDIT_ITEM);
            return true;
        }

        @Override
        public void onUpdateTotalCost() {
            updateTotalCost();
        }
    }

    private void updateTotalCost() {
        if (!Prefs.getBoolean(R.string.prefs_display_total_cost)) {
            totalCostContainer.setVisibility(View.GONE);
        } else {
            BigDecimal total = adapter.getTotalCost();
            if (NumberUtils.numberLessOrEquals(total, 0)) {
                totalCostContainer.setVisibility(View.GONE);
            } else {
                totalCostContainer.setVisibility(View.VISIBLE);
                BigDecimal inCart = adapter.getInCartTotalCost();
                BigDecimal remaining = total.subtract(inCart);
                if (NumberUtils.numberEquals(Prefs.getTaxPercent(), 0)) {
                    String text = NumberUtils.priceWithCurrency(inCart) + " / " + NumberUtils.priceWithCurrency(total);
                    totalCost.setText(getString(R.string.total_cost, text));
                    inCartCost.setText(getString(R.string.remaining_cost, NumberUtils.priceWithCurrency(remaining)));
                } else {
                    totalCost.setText(getString(R.string.total_cost, NumberUtils.priceWithTax(total)));
                    inCartCost.setText(getString(R.string.in_cart_cost, NumberUtils.priceWithTax(inCart)));
                }
            }
        }
    }
}
