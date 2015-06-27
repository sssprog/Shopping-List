package com.sssprog.shoppingliststandalone.ui.history;

import android.util.Pair;

import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.api.services.ItemService;
import com.sssprog.shoppingliststandalone.mvp.Presenter;

import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.functions.Func2;

public class HistoryPresenter extends Presenter<HistoryActivity> {

    private long listId;

    public void setListId(long listId) {
        this.listId = listId;
    }

    public void loadItems() {
        Observable.zip(ItemService.getInstance().getHistory(),
                ItemService.getInstance().getListItems(listId),
                new Func2<List<ItemModel>, List<ItemModel>, Pair<List<ItemModel>, List<ItemModel>>>() {
                    @Override
                    public Pair<List<ItemModel>, List<ItemModel>> call(List<ItemModel> history, List<ItemModel> listItems) {
                        return new Pair<>(history, listItems);
                    }
                })
                .subscribe(new SimpleRxSubscriber<Pair<List<ItemModel>, List<ItemModel>>>() {
                    @Override
                    public void onNext(final Pair<List<ItemModel>, List<ItemModel>> result) {
                        runViewAction(new Runnable() {
                            @Override
                            public void run() {
                                getView().onItemsLoaded(result.first, result.second);
                            }
                        });
                    }
                });
    }

    public void addItem(ItemModel item) {
        ItemService.getInstance().saveItem(item).subscribe(new SimpleRxSubscriber<ItemModel>() {
            @Override
            public void onNext(final ItemModel result) {
                runViewAction(new Runnable() {
                    @Override
                    public void run() {
                        getView().onItemAdded(result);
                    }
                });
            }
        });
    }

    public void deleteItem(ItemModel item) {
        ItemService.getInstance().deleteItemWithDelay(item);
    }

    public boolean cancelDeletion(ItemModel item) {
        return ItemService.getInstance().cancelDeletion(item);
    }

    public void finalizeDeletion() {
        ItemService.getInstance().finalizeDeletion();
    }

    public void addItemsToList(Collection<ItemModel> items) {
        ItemService.getInstance().addItemsToList(items, listId).subscribe(new SimpleRxSubscriber<Void>() {
            @Override
            public void onCompleted() {
                runViewAction(new Runnable() {
                    @Override
                    public void run() {
                        getView().onItemsAdded();
                    }
                });
            }
        });
    }

}
