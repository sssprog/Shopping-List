package com.sssprog.shoppingliststandalone

import android.os.Build
import com.parse.Parse
import com.parse.ParseACL
import com.parse.ParseObject
import com.parse.ParseUser
import com.sssprog.shoppingliststandalone.api.parsemodels.ListModel

public object Config {

    val DEBUG: Boolean = BuildConfig.DEBUG
    val LOGS_ENABLED: Boolean = DEBUG

    fun initParse() {
        ParseObject.registerSubclass(javaClass<ListModel>())

        Parse.enableLocalDatastore(App.appContext)
        Parse.initialize(App.appContext)
        ParseUser.enableAutomaticUser()

        val defaultAcl = ParseACL()
        ParseACL.setDefaultACL(defaultAcl, true)
    }

    fun hasLollipop(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    fun hasKitkat(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
}
