package com.sssprog.shoppingliststandalone.ui.home;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.ListModel;
import com.sssprog.shoppingliststandalone.api.services.ListService;
import com.sssprog.shoppingliststandalone.mvp.Presenter;
import com.sssprog.shoppingliststandalone.utils.Prefs;

import java.util.List;

public class ListsPresenter extends Presenter<ListsFragment> {

    void loadLists() {
        ListService.getInstance().getAll().subscribe(new SimpleRxSubscriber<List<ListModel>>() {
            @Override
            public void onNext(final List<ListModel> result) {
                runViewAction(() -> getView().onListsLoaded(result));
            }
        });
    }

    void addItem(ListModel item) {
        ListService.getInstance().saveItem(item)
                .subscribe(new SimpleRxSubscriber<ListModel>() {
                    @Override
                    public void onNext(ListModel list) {
                        Prefs.putLong(R.string.pref_current_list_id, list.getId());
                    }

                    @Override
                    public void onCompleted() {
                        loadLists();
                    }
                });
    }

    void saveList(ListModel item) {
        ListService.getInstance().saveItem(item)
                .doOnCompleted(() -> loadLists())
                .subscribe(new SimpleRxSubscriber<ListModel>());
    }

    void deleteList(ListModel item) {
        ListService.getInstance().deleteItem(item)
                .doOnCompleted(() -> loadLists())
                .subscribe(new SimpleRxSubscriber<Void>());
    }

}
