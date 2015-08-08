package com.sssprog.shoppingliststandalone.ui.home;

import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.api.services.ItemService;
import com.sssprog.shoppingliststandalone.mvp.Presenter;

import java.util.Collection;
import java.util.List;

public class MainPresenter extends Presenter<MainActivity> {

    private long lastListId;

    public void loadItems(final long listId) {
        lastListId = listId;
        ItemService.getInstance().getListItems(listId).subscribe(new SimpleRxSubscriber<List<ItemModel>>() {
            @Override
            public void onNext(final List<ItemModel> items) {
                runViewAction(() -> {
                    if (lastListId == listId) {
                        getView().onItemsLoaded(items);
                    }
                });
            }
        });
    }

    public void deleteItem(ItemModel item) {
        ItemService.getInstance().deleteWithDelay(item);
    }

    public boolean cancelDeletion(ItemModel item) {
        return ItemService.getInstance().cancelDeletion(item);
    }

    public void finishDeletion() {
        ItemService.getInstance().finishDeletion();
    }

    public void deleteItems(Collection<ItemModel> items) {
        ItemService.getInstance().deleteItems(items).subscribe(new SimpleRxSubscriber<Void>());
    }

}
