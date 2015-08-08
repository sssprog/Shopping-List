package com.sssprog.shoppingliststandalone.api.services;

import com.sssprog.shoppingliststandalone.api.Api;
import com.sssprog.shoppingliststandalone.api.database.DatabaseHelper;
import com.sssprog.shoppingliststandalone.api.database.ListModel;
import com.sssprog.shoppingliststandalone.utils.DatabaseUtils;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class ListService {

    private static ListService instance;

    public static synchronized ListService getInstance() {
        if (instance == null) {
            instance = new ListService();
        }
        return instance;
    }

    public Observable<List<ListModel>> getAll() {
        return Observable
                .create(new Observable.OnSubscribe<List<ListModel>>() {
                    @Override
                    public void call(final Subscriber<? super List<ListModel>> subscriber) {
                        DatabaseUtils.executeWithRuntimeException(() -> {
                            subscriber.onNext(DatabaseHelper.getInstance().getListDao().queryForAll());
                            subscriber.onCompleted();
                        });
                    }
                })
                .subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ListModel> saveItem(final ListModel item) {
        return Observable
                .create(new Observable.OnSubscribe<ListModel>() {
                    @Override
                    public void call(final Subscriber<? super ListModel> subscriber) {
                        DatabaseUtils.executeWithRuntimeException(() -> {
                            DatabaseHelper.getInstance().getListDao().createOrUpdate(item);
                            subscriber.onNext(item);
                            subscriber.onCompleted();
                        });
                    }
                })
                .subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> deleteItem(final ListModel item) {
        return Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(final Subscriber<? super Void> subscriber) {
                        DatabaseUtils.executeWithRuntimeException(() -> {
                            DatabaseHelper.getInstance().getListDao().delete(item);
                            subscriber.onCompleted();
                        });
                    }
                })
                .subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
