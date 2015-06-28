package com.sssprog.shoppingliststandalone.api.services;

import com.j256.ormlite.dao.Dao;
import com.sssprog.shoppingliststandalone.api.database.DatabaseHelper;
import com.sssprog.shoppingliststandalone.api.database.QuantityUnitModel;
import com.sssprog.shoppingliststandalone.events.QuantityUnitsChangedEvent;

import java.sql.SQLException;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action0;

public class QuantityUnitService extends BaseModelService<QuantityUnitModel> {

    private static QuantityUnitService instance;

    public static synchronized QuantityUnitService getInstance() {
        if (instance == null) {
            instance = new QuantityUnitService();
        }
        return instance;
    }

    @Override
    protected Dao<QuantityUnitModel, ?> getDao() throws SQLException {
        return DatabaseHelper.getInstance().getQuantityUnitDao();
    }

    @Override
    public Observable<QuantityUnitModel> save(QuantityUnitModel item) {
        return super.save(item)
                .doOnCompleted(changeEvent);
    }

    @Override
    public Observable<Void> delete(QuantityUnitModel item) {
        return super.delete(item)
                .doOnCompleted(changeEvent);
    }

    private Action0 changeEvent = new Action0() {
        @Override
        public void call() {
            EventBus.getDefault().post(new QuantityUnitsChangedEvent());
        }
    };
}
