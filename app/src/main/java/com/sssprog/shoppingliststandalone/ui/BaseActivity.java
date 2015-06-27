package com.sssprog.shoppingliststandalone.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.sssprog.shoppingliststandalone.Config;
import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.utils.LogHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    public Toolbar toolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        initToolbarShadow();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    protected int getToolbarShadowContainerId() {
        return android.R.id.content;
    }

    private void initToolbarShadow() {
        if (Config.hasLollipop()) {
            return;
        }
        final ViewGroup shadowContainer = (ViewGroup) findViewById(getToolbarShadowContainerId());
        final View shadow = getLayoutInflater().inflate(R.layout.toolbar_shadow, shadowContainer, false);
        shadowContainer.addView(shadow);
        final View aboveView = toolbar;
        aboveView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            int bottom = 0;
            int[] location = new int[2];

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int b, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                aboveView.getLocationOnScreen(location);
                int newBottom = location[1] + aboveView.getHeight() - getContentViewTop();
                LogHelper.i("-tag-", "view above shadow changed position " + (newBottom != bottom));
                if (newBottom != bottom) {
                    bottom = newBottom;
                    ((ViewGroup.MarginLayoutParams) shadow.getLayoutParams()).topMargin = bottom;
                    shadow.requestLayout();
                }
            }

            private int getContentViewTop() {
                shadowContainer.getLocationOnScreen(location);
                return location[1];
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
