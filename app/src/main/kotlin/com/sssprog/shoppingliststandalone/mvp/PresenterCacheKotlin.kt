package com.sssprog.shoppingliststandalone.mvp

import java.util.*

public object PresenterCacheKotlin {
    private val presenters = HashMap<Class<*>, PresenterKotlin<*>>()

    public fun put(key: Class<*>, presenter: PresenterKotlin<*>) {
        remove(key)
        presenters[key] = presenter
    }

    public fun <P : PresenterKotlin<*>> get(key: Class<*>): P? {
        return presenters[key] as? P
    }

    public fun remove(key: Class<*>) {
        val presenter = presenters[key]
        presenter?.destroy()
    }
}
