package com.sssprog.shoppingliststandalone.api.services;

import com.j256.ormlite.dao.Dao;
import com.sssprog.shoppingliststandalone.api.database.CategoryModel;
import com.sssprog.shoppingliststandalone.api.database.DatabaseHelper;
import com.sssprog.shoppingliststandalone.events.CategoriesChangedEvent;

import java.sql.SQLException;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action0;

public class CategoryService extends BaseModelService<CategoryModel> {

    private static CategoryService instance;

    public static synchronized CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
        }
        return instance;
    }

    @Override
    protected Dao<CategoryModel, ?> getDao() throws SQLException {
        return DatabaseHelper.getInstance().getCategoryDao();
    }

    @Override
    public Observable<CategoryModel> save(CategoryModel item) {
        return super.save(item)
                .doOnCompleted(changeEvent);
    }

    @Override
    public Observable<Void> delete(CategoryModel item) {
        return super.delete(item)
                .doOnCompleted(changeEvent);
    }

    private Action0 changeEvent = new Action0() {
        @Override
        public void call() {
            EventBus.getDefault().post(new CategoriesChangedEvent());
        }
    };

}
