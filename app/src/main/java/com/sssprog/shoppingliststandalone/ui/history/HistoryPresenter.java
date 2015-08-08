package com.sssprog.shoppingliststandalone.ui.history;

import android.util.Pair;

import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.api.services.ItemService;
import com.sssprog.shoppingliststandalone.mvp.Presenter;

import java.util.Collection;
import java.util.List;

import rx.Observable;

public class HistoryPresenter extends Presenter<HistoryActivity> {

    private long listId;

    public void setListId(long listId) {
        this.listId = listId;
    }

    public void loadItems() {
        Observable.zip(ItemService.getInstance().getHistory(),
                ItemService.getInstance().getListItems(listId),
                (history, listItems) -> new Pair<>(history, listItems))
                .subscribe(new SimpleRxSubscriber<Pair<List<ItemModel>, List<ItemModel>>>() {
                    @Override
                    public void onNext(final Pair<List<ItemModel>, List<ItemModel>> result) {
                        runViewAction(() -> getView().onItemsLoaded(result.first, result.second));
                    }
                });
    }

    public void addItem(ItemModel item) {
        ItemService.getInstance().save(item).subscribe(new SimpleRxSubscriber<ItemModel>() {
            @Override
            public void onNext(final ItemModel result) {
                runViewAction(() -> getView().onItemAdded(result));
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

    public void addItemsToList(Collection<ItemModel> items) {
        ItemService.getInstance().addItemsToList(items, listId).subscribe(new SimpleRxSubscriber<Void>() {
            @Override
            public void onCompleted() {
                runViewAction(() -> getView().onItemsAdded());
            }
        });
    }

}
