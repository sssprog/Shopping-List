package com.sssprog.shoppingliststandalone.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import kotlin.properties.Delegates

public object Prefs99 {

    private var context: Context by Delegates.notNull()
    private var settings: SharedPreferences by Delegates.notNull()
    private var editor: SharedPreferences.Editor by Delegates.notNull()

    public fun init(context: Context) {
        this.context = context
        settings = PreferenceManager.getDefaultSharedPreferences(context)
        editor = settings.edit()
    }

}
