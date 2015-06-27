package com.sssprog.shoppingliststandalone.api.services;

import android.os.Handler;
import android.os.Looper;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.j256.ormlite.dao.Dao;
import com.sssprog.shoppingliststandalone.api.Api;
import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.DatabaseHelper;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.api.database.ListModel;
import com.sssprog.shoppingliststandalone.utils.DatabaseUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class ItemService {

    private static final long DELETION_DELAY = 3500;

    private static ItemService instance;

    private final Handler handler;
    private final LinkedList<DeletedItem> deletedItems = new LinkedList<>();

    public ItemService() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static synchronized ItemService getInstance() {
        if (instance == null) {
            instance = new ItemService();
        }
        return instance;
    }

    public Observable<List<ItemModel>> getHistory() {
        return Observable.create(new Observable.OnSubscribe<List<ItemModel>>() {
            @Override
            public void call(final Subscriber<? super List<ItemModel>> subscriber) {
                DatabaseUtils.executeWithRuntimeException(new DatabaseUtils.DatabaseTask() {
                    @Override
                    public void execute() throws Exception {
                        List<ItemModel> result = DatabaseHelper.getInstance().getItemDao().queryBuilder()
                                .orderBy(ItemModel.FIELD_NAME, true)
                                .where().isNull(ItemModel.FIELD_LIST)
                                .query();
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ItemModel> saveItem(final ItemModel item) {
        return Observable.create(new Observable.OnSubscribe<ItemModel>() {
            @Override
            public void call(final Subscriber<? super ItemModel> subscriber) {
                DatabaseUtils.executeWithRuntimeException(new DatabaseUtils.DatabaseTask() {
                    @Override
                    public void execute() throws Exception {
                        DatabaseHelper.getInstance().getItemDao().createOrUpdate(item);
                        subscriber.onNext(item);
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void deleteItemWithDelay(ItemModel item) {
        deletedItems.add(new DeletedItem(item, System.currentTimeMillis() + DELETION_DELAY));
        scheduleDeletion();
    }

    public boolean cancelDeletion(final ItemModel item) {
        return Iterables.removeIf(deletedItems, new Predicate<DeletedItem>() {
            @Override
            public boolean apply(DeletedItem input) {
                return input.item.getId() == item.getId();
            }
        });
    }

    public void finalizeDeletion() {
        for (DeletedItem item : deletedItems) {
            deleteItem(item.item).subscribe(new SimpleRxSubscriber<Void>());;
        }
        deletedItems.clear();
    }

    private Runnable deletionTask = new Runnable() {
        @Override
        public void run() {
            while (!deletedItems.isEmpty() && System.currentTimeMillis() > deletedItems.getFirst().time) {
                deleteItem(deletedItems.getFirst().item).subscribe(new SimpleRxSubscriber<Void>());
                deletedItems.removeFirst();
            }
            if (!deletedItems.isEmpty()) {
                scheduleDeletion();
            }
        }
    };

    private void scheduleDeletion() {
        handler.postDelayed(deletionTask, DELETION_DELAY);
    }

    public Observable<Void> deleteItem(final ItemModel item) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                DatabaseUtils.executeWithRuntimeException(new DatabaseUtils.DatabaseTask() {
                    @Override
                    public void execute() throws Exception {
                        DatabaseHelper.getInstance().getItemDao().delete(item);
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<ItemModel>> getListItems(final long listId) {
        return Observable.create(new Observable.OnSubscribe<List<ItemModel>>() {
            @Override
            public void call(final Subscriber<? super List<ItemModel>> subscriber) {
                DatabaseUtils.executeWithRuntimeException(new DatabaseUtils.DatabaseTask() {
                    @Override
                    public void execute() throws Exception {
                        List<ItemModel> result = DatabaseHelper.getInstance().getItemDao().queryBuilder()
                                .orderBy(ItemModel.FIELD_NAME, true)
                                .where().eq(ItemModel.FIELD_LIST, listId)
                                .query();
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> addItemsToList(final Collection<ItemModel> items, final long listId) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                DatabaseUtils.callInTransaction(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ListModel list = DatabaseHelper.getInstance().getListDao().queryForId(listId);
                        Dao<ItemModel, Long> dao = DatabaseHelper.getInstance().getItemDao();
                        for (ItemModel history : items) {
                            ItemModel item = new ItemModel();
                            ItemModel.copyFields(history, item);
                            item.setList(list);
                            dao.create(item);
                        }
                        return null;
                    }
                });
                subscriber.onCompleted();
            }
        }).subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static class DeletedItem {
        ItemModel item;
        long time;

        public DeletedItem(ItemModel item, long time) {
            this.item = item;
            this.time = time;
        }
    }

}
