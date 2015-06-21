package com.sssprog.shoppingliststandalone.ui;

import android.os.Bundle;

import com.sssprog.shoppingliststandalone.mvp.Presenter;
import com.sssprog.shoppingliststandalone.mvp.PresenterCache;


public abstract class BaseMvpActivity<P extends Presenter> extends BaseActivity {

    private static final String STATE_PRESENTER = "presenter";
//    private static final String LOADING_DIALOG_TAG = "loading_dialog";

    private P mPresenter;

    public P getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mPresenter = PresenterCache.getInstance().get(getClass());
        }
        if (mPresenter == null) {
            mPresenter = createPresenter();
            PresenterCache.getInstance().put(getClass(), mPresenter);
            if (savedInstanceState != null) {
                mPresenter.restoreState(savedInstanceState.getBundle(STATE_PRESENTER));
            }
        }
        getPresenter().attach(this);
    }

    protected abstract P createPresenter();

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
        outState.putBundle(STATE_PRESENTER, mPresenter.saveState());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            PresenterCache.getInstance().remove(getClass());
        }
    }

//    public void showLoadingDialog() {
//        getSupportFragmentManager().beginTransaction()
//                .add(ProgressDialogFragment.newInstance(), LOADING_DIALOG_TAG)
//                .commit();
//    }
//
//    public void dismissLoadingDialog() {
//        ProgressDialogFragment dialog = (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(LOADING_DIALOG_TAG);
//        dialog.dismiss();
//    }
//
//    public void showSnackbar(int message) {
//        Snackbar.with(this)
//                .message(message)
//                .show();
//    }

}
