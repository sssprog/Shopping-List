package com.sssprog.shoppingliststandalone.api;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sssprog.shoppingliststandalone.App;
import com.sssprog.shoppingliststandalone.api.parsemodels.ListModel;

import java.util.concurrent.Executors;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class Api {

    private static Api instance;
    private static final Scheduler SCHEDULER = Schedulers.from(Executors.newFixedThreadPool(4));

    public static Api getInstance() {
        if (instance == null) {
            instance = new Api();
        }
        return instance;
    }

    public void init() {
        ParseObject.registerSubclass(ListModel.class);

        Parse.enableLocalDatastore(App.getAppContext());
        Parse.initialize(App.getAppContext());
        ParseUser.enableAutomaticUser();

        ParseACL defaultAcl = new ParseACL();
        ParseACL.setDefaultACL(defaultAcl, true);
    }

    public static Scheduler scheduler() {
        return SCHEDULER;
    }
}
