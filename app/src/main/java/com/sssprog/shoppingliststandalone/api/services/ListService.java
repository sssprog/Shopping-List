package com.sssprog.shoppingliststandalone.api.services;

import com.parse.ParseException;
import com.sssprog.shoppingliststandalone.api.Api;
import com.sssprog.shoppingliststandalone.api.parsemodels.ListModel;

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
        return Observable.create(new Observable.OnSubscribe<List<ListModel>>() {
            @Override
            public void call(Subscriber<? super List<ListModel>> subscriber) {
                try {
                    List<ListModel> result = ListModel.query().fromLocalDatastore().find();
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (ParseException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
