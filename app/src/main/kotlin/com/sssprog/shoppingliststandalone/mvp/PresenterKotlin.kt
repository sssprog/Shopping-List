package com.sssprog.shoppingliststandalone.mvp

import java.util.*

public open class PresenterKotlin<V> {
    protected var view: V = null
    private val actions: MutableList<() -> Unit> = LinkedList()

    public fun attach(view: V) {
        this.view = view
        onViewAttached()
        executeViewActions()
    }

    public fun detach() {
        view?.let { onBeforeViewDetached() }
        view = null
        onViewDetached()
    }

    protected fun isViewAttached(): Boolean = view != null

    public fun destroy() {
    }

    protected fun onViewAttached() {
    }

    protected fun onViewDetached() {
    }

    protected fun onBeforeViewDetached() {
    }

    private fun executeViewActions() {
        actions.forEach { it() }
        actions.clear()
    }

    protected fun runViewAction(action: () -> Unit) {
        if (isViewAttached()) {
            action()
        } else {
            actions.add(action)
        }
    }

}
