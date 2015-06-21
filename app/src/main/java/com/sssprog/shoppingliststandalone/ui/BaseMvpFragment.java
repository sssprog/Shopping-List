package com.sssprog.shoppingliststandalone.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.sssprog.shoppingliststandalone.mvp.Presenter;
import com.sssprog.shoppingliststandalone.mvp.PresenterHolder;

import butterknife.ButterKnife;

public class BaseMvpFragment<P extends Presenter> extends Fragment {

    private PresenterHolder<P> presenterHolder;

    public P getPresenter() {
        return presenterHolder.getPresenter();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenterHolder = PresenterHolder.createHolder(this);
        presenterHolder.init(savedInstanceState);
        getPresenter().attach(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
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

}
