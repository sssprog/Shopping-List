package com.sssprog.shoppingliststandalone.ui

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.sssprog.shoppingliststandalone.R
import com.sssprog.shoppingliststandalone.api.parsemodels.ListModel
import com.sssprog.shoppingliststandalone.testbug.ViewTest
import com.sssprog.shoppingliststandalone.utils.LogHelper
import kotlinx.android.synthetic.activity_main.drawerLayout
import kotlinx.android.synthetic.toolbar.toolbar
import kotlin.properties.Delegates

public class MainActivity : BaseMvpActivity<MainPresenter>() {

    private var drawerToggle: ActionBarDrawerToggle by Delegates.notNull()

    override fun createPresenter(): MainPresenter {
        return MainPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupNavigationView()
        presenter.loadLists()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun setupNavigationView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        getSupportActionBar().setHomeButtonEnabled(true)
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_open, R.string.navigation_close)
        drawerLayout.setDrawerListener(drawerToggle)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun onListsLoaded(items: MutableList<ListModel>) {
        LogHelper.i("-tag-", "onListsLoaded $items")
    }

    fun test() {
        LogHelper.i("-tag-", "test")
    }
}
