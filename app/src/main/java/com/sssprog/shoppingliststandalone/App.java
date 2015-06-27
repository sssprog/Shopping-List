package com.sssprog.shoppingliststandalone;

import android.app.Application;

import com.sssprog.shoppingliststandalone.api.Api;
import com.sssprog.shoppingliststandalone.utils.Prefs;

public class App extends Application {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Prefs.init(this);
        Api.getInstance().init();
    }

}
