package com.sssprog.shoppingliststandalone.api

import com.sssprog.shoppingliststandalone.utils.LogHelper
import rx.Subscriber

public open class SimpleRxSubscriber<T> : Subscriber<T>() {
    override fun onNext(result: T) {

    }

    override fun onError(e: Throwable?) {
        LogHelper.printStackTrace(e)
    }

    override fun onCompleted() {

    }

}
