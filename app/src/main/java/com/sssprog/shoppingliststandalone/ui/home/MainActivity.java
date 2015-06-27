package com.sssprog.shoppingliststandalone.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.events.ListChangedEvent;
import com.sssprog.shoppingliststandalone.mvp.PresenterClass;
import com.sssprog.shoppingliststandalone.ui.BaseMvpActivity;
import com.sssprog.shoppingliststandalone.ui.history.HistoryActivity;
import com.sssprog.shoppingliststandalone.utils.ViewStateSwitcher;
import com.sssprog.shoppingliststandalone.utils.ViewUtils;

import java.util.List;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

@PresenterClass(MainPresenter.class)
public class MainActivity extends BaseMvpActivity<MainPresenter> {

    private static final int REQUEST_ADD_ITEMS = 0;

    @InjectView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.navigationView)
    NavigationView navigationView;
    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    private ActionBarDrawerToggle drawerToggle;
    private ListsFragment listsFragment;
    private ViewStateSwitcher stateSwitcher;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ListAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
                        break;
                    default:
                        listsFragment.onNavigationItemSelected(item);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
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
        stateSwitcher.switchToState(adapter.isEmpty() ? ViewStateSwitcher.STATE_EMPTY : ViewStateSwitcher.STATE_MAIN, true);
    }

    public void onEventMainThread(ListChangedEvent event) {
        loadList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ITEMS && resultCode == RESULT_OK) {
            loadList();
        }
    }
}
