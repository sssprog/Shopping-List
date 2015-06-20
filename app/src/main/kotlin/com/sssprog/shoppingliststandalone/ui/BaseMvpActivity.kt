package com.sssprog.shoppingliststandalone.ui

import android.os.Bundle
import com.sssprog.mvp.Presenter
import com.sssprog.shoppingliststandalone.mvp.PresenterKotlin
import kotlin.properties.Delegates

public abstract class BaseMvpActivity<P: PresenterKotlin<*>>: BaseActivity() {

    protected var presenter: P by Delegates.notNull()
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var p: P? = null
//        savedInstanceState.let {
//            p = PresenterCache.getInstance().get(javaClass)
//        }
//        if (p == null) {
//            p = createPresenter()
//            PresenterCache.getInstance().put(javaClass, p!!)
//        }
//        presenter = p!!
//        (presenter as Presenter<in BaseActivity>).attach(this)


        presenter = createPresenter()
        (presenter as Presenter<in BaseActivity>).attach(this)
    }

    abstract fun createPresenter(): P
}
