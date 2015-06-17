package com.sssprog.shoppingliststandalone.ui

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

public open class BaseActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
            else ->
                return super.onOptionsItemSelected(item)
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}
