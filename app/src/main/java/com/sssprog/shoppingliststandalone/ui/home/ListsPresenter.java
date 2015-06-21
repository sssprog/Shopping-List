package com.sssprog.shoppingliststandalone.ui.home;

import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.parsemodels.ListModel;
import com.sssprog.shoppingliststandalone.api.services.ListService;
import com.sssprog.shoppingliststandalone.mvp.Presenter;

import java.util.List;

import rx.functions.Action0;

public class ListsPresenter extends Presenter<ListsFragment> {

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

    void saveList(ListModel item) {
        ListService.getInstance().saveItem(item)
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        loadLists();
                    }
                })
                .subscribe(new SimpleRxSubscriber<Void>());
    }

    void deleteList(ListModel item) {
        ListService.getInstance().deleteItem(item)
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        loadLists();
                    }
                })
                .subscribe(new SimpleRxSubscriber<Void>());
    }

}
