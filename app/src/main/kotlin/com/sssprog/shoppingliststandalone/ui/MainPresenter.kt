package com.sssprog.shoppingliststandalone.ui

import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber
import com.sssprog.shoppingliststandalone.api.parsemodels.ListModel
import com.sssprog.shoppingliststandalone.api.services.ListService
import com.sssprog.shoppingliststandalone.mvp.PresenterKotlin

// kotlin compiler has bug, when instantiating this class happens java.lang.VerifyError
// if to take a look at byte code it seems broken.
// While trying work around this issue, I stumbled upon IncompatibleClassChangeError
// If in the future versions of kotlin this is fixes maybe it's time to give it another try
class MainPresenter : PresenterKotlin<MainActivity?>() {

    fun loadLists() {
        ListService.getAll().subscribe(object : SimpleRxSubscriber<MutableList<ListModel>>() {
            override fun onNext(result: MutableList<ListModel>) {
                runViewAction{ view?.onListsLoaded(result) }
            }
        })
    }

}
