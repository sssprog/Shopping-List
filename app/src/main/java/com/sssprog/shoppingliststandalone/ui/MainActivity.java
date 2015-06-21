package com.sssprog.shoppingliststandalone.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.parsemodels.ListModel;
import com.sssprog.shoppingliststandalone.utils.LogHelper;

import java.util.List;

public class MainActivity extends BaseMvpActivity<MainPresenter> {

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        setSupportActionBar(toolbar);
//        setupNavigationView();
        getPresenter().loadLists();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    private void setupNavigationView() {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close);
//        drawerLayout.setDrawerListener(drawerToggle);
//    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        drawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        drawerToggle.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    void onListsLoaded(List<ListModel> items) {
        LogHelper.i("-tag-", "onListsLoaded $items");
    }
}
