package com.sssprog.shoppingliststandalone.api.services;

import android.os.Handler;
import android.os.Looper;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.j256.ormlite.dao.Dao;
import com.sssprog.shoppingliststandalone.api.Api;
import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.CategoryModel;
import com.sssprog.shoppingliststandalone.utils.DatabaseUtils;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public abstract class BaseModelService<T> implements ModelService<T>, ModelQueryAllService<T> {

    private static final long DELETION_DELAY = 3500;

    private final Handler handler;
    private final LinkedList<DeletedItem<T>> deletedItems = new LinkedList<>();

    public BaseModelService() {
        handler = new Handler(Looper.getMainLooper());
    }

    protected abstract Dao<T, ?> getDao() throws SQLException;

    @Override
    public Observable<T> save(final T item) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                DatabaseUtils.executeWithRuntimeException(new DatabaseUtils.DatabaseTask() {
                    @Override
                    public void execute() throws Exception {
                        getDao().createOrUpdate(item);
                        subscriber.onNext(item);
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Void> delete(final T item) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                DatabaseUtils.executeWithRuntimeException(new DatabaseUtils.DatabaseTask() {
                    @Override
                    public void execute() throws Exception {
                        getDao().delete(item);
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void deleteWithDelay(T item) {
        deletedItems.add(new DeletedItem<T>(item, System.currentTimeMillis() + DELETION_DELAY));
        scheduleDeletion();
    }

    private Runnable deletionTask = new Runnable() {
        @Override
        public void run() {
            while (!deletedItems.isEmpty() && System.currentTimeMillis() > deletedItems.getFirst().time) {
                delete(deletedItems.getFirst().item).subscribe(new SimpleRxSubscriber<Void>());
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

    @Override
    public boolean cancelDeletion(final T item) {
        return Iterables.removeIf(deletedItems, new Predicate<DeletedItem<T>>() {
            @Override
            public boolean apply(DeletedItem<T> input) {
                return input.item.equals(item);
            }
        });
    }

    @Override
    public void finishDeletion() {
        for (DeletedItem<T> item : deletedItems) {
            delete(item.item).subscribe(new SimpleRxSubscriber<Void>());;
        }
        deletedItems.clear();
    }

    @Override
    public Observable<List<T>> getAll() {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override
            public void call(final Subscriber<? super List<T>> subscriber) {
                DatabaseUtils.executeWithRuntimeException(new DatabaseUtils.DatabaseTask() {
                    @Override
                    public void execute() throws Exception {
                        subscriber.onNext(getDao().queryForAll());
                        subscriber.onCompleted();
                    }
                });
            }
        }).subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
