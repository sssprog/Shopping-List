package com.sssprog.shoppingliststandalone.api.services

import com.sssprog.shoppingliststandalone.api.parsemodels.ListModel
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.lang.kotlin.observable
import rx.schedulers.Schedulers

public object ListService {

    public fun getAll(): Observable<MutableList<ListModel>> {
        return observable<MutableList<ListModel>> { subscriber ->
            val result = ListModel.query().fromLocalDatastore().find()
            subscriber.onNext(result)
            subscriber.onCompleted()
        }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
    }

}
