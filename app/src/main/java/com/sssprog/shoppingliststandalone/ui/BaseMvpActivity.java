package com.sssprog.shoppingliststandalone.ui;

import android.os.Bundle;

import com.sssprog.shoppingliststandalone.mvp.Presenter;
import com.sssprog.shoppingliststandalone.mvp.PresenterHolder;


public abstract class BaseMvpActivity<P extends Presenter> extends BaseActivity {

    private PresenterHolder<P> presenterHolder;

    public P getPresenter() {
        return presenterHolder.getPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterHolder = PresenterHolder.createHolder(this);
        presenterHolder.init(savedInstanceState);
        getPresenter().attach(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().attach(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().detach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenterHolder.saveState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            presenterHolder.onDestroy();
        }
    }

}
