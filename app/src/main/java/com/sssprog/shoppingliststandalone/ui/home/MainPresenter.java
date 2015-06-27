package com.sssprog.shoppingliststandalone.ui.home;

import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.api.services.ItemService;
import com.sssprog.shoppingliststandalone.mvp.Presenter;

import java.util.List;

public class MainPresenter extends Presenter<MainActivity> {

    private long lastListId;

    public void loadItems(final long listId) {
        lastListId = listId;
        ItemService.getInstance().getListItems(listId).subscribe(new SimpleRxSubscriber<List<ItemModel>>() {
            @Override
            public void onNext(final List<ItemModel> items) {
                runViewAction(new Runnable() {
                    @Override
                    public void run() {
                        if (lastListId == listId) {
                            getView().onItemsLoaded(items);
                        }
                    }
                });
            }
        });
    }

}
