package com.sssprog.shoppingliststandalone

import android.app.Application
import com.sssprog.shoppingliststandalone.testbug.ViewTest
import com.sssprog.shoppingliststandalone.utils.Prefs
import kotlin.properties.Delegates

public class App : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Config.initParse()
        Prefs.init(this)
    }

    companion object {
        private var instance: App by Delegates.notNull()
        val appContext: App
            get() = instance

        public fun getString(res: Int): String = appContext.getString(res)
    }
}
