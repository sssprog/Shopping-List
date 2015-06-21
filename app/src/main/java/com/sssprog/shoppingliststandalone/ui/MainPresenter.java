package com.sssprog.shoppingliststandalone.ui;

import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.parsemodels.ListModel;
import com.sssprog.shoppingliststandalone.api.services.ListService;
import com.sssprog.shoppingliststandalone.mvp.Presenter;

import java.util.List;

public class MainPresenter extends Presenter<MainActivity> {

    void loadLists() {
        ListService.getInstance().getAll().subscribe(new SimpleRxSubscriber<List<ListModel>>() {
            @Override
            public void onNext(final List<ListModel> result) {
                runViewAction(new Runnable() {
                    @Override
                    public void run() {
                        getView().onListsLoaded(result);
                    }
                });
            }
        });
    }

}
