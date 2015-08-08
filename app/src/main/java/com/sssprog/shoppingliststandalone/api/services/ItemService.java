package com.sssprog.shoppingliststandalone.api.services;

import com.j256.ormlite.dao.Dao;
import com.sssprog.shoppingliststandalone.api.Api;
import com.sssprog.shoppingliststandalone.api.database.DatabaseHelper;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.api.database.ListModel;
import com.sssprog.shoppingliststandalone.utils.DatabaseUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class ItemService extends BaseModelService<ItemModel> {

    private static ItemService instance;

    @Override
    protected Dao<ItemModel, Long> getDao() throws SQLException{
        return DatabaseHelper.getInstance().getItemDao();
    }

    public static synchronized ItemService getInstance() {
        if (instance == null) {
            instance = new ItemService();
        }
        return instance;
    }

    public Observable<List<ItemModel>> getHistory() {
        return Observable
                .create(new Observable.OnSubscribe<List<ItemModel>>() {
                    @Override
                    public void call(final Subscriber<? super List<ItemModel>> subscriber) {
                        DatabaseUtils.executeWithRuntimeException(() -> {
                            List<ItemModel> result = DatabaseHelper.getInstance().getItemDao().queryBuilder()
                                    .orderBy(ItemModel.FIELD_NAME, true)
                                    .where().isNull(ItemModel.FIELD_LIST)
                                    .query();
                            subscriber.onNext(result);
                            subscriber.onCompleted();
                        });
                    }
                })
                .subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<ItemModel>> getListItems(final long listId) {
        return Observable
                .create(new Observable.OnSubscribe<List<ItemModel>>() {
                    @Override
                    public void call(final Subscriber<? super List<ItemModel>> subscriber) {
                        DatabaseUtils.executeWithRuntimeException(() -> {
                            List<ItemModel> result = DatabaseHelper.getInstance().getItemDao().queryBuilder()
                                    .orderBy(ItemModel.FIELD_NAME, true)
                                    .where().eq(ItemModel.FIELD_LIST, listId)
                                    .query();
                            subscriber.onNext(result);
                            subscriber.onCompleted();
                        });
                    }
                })
                .subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Void> addItemsToList(final Collection<ItemModel> items, final long listId) {
        return Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(final Subscriber<? super Void> subscriber) {
                        DatabaseUtils.callInTransaction(() -> {
                            ListModel list = DatabaseHelper.getInstance().getListDao().queryForId(listId);
                            Dao<ItemModel, Long> dao = DatabaseHelper.getInstance().getItemDao();
                            for (ItemModel history : items) {
                                ItemModel item = new ItemModel();
                                copyHistoryRelatedFields(history, item);
                                item.setQuantity(BigDecimal.ONE);
                                item.setList(list);
                                dao.create(item);
                            }
                            return null;
                        });
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ItemModel> get(final long id) {
        return Observable
                .create(new Observable.OnSubscribe<ItemModel>() {
                    @Override
                    public void call(final Subscriber<? super ItemModel> subscriber) {
                        DatabaseUtils.executeWithRuntimeException(() -> {
                            ItemModel item = getDao().queryForId(id);
                            if (item == null) {
                                subscriber.onError(new Exception("Item is not in DB"));
                            } else {
                                subscriber.onNext(item);
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ItemModel> saveAndUpdateHistory(final ItemModel item) {
        return Observable
                .create(new Observable.OnSubscribe<ItemModel>() {
                    @Override
                    public void call(final Subscriber<? super ItemModel> subscriber) {
                        DatabaseUtils.callInTransaction(() -> {
                            getDao().createOrUpdate(item);
                            List<ItemModel> history = getDao().queryBuilder()
                                    .where().eq(ItemModel.FIELD_NAME, item.getName())
                                    .and().isNull(ItemModel.FIELD_LIST)
                                    .query();
                            for (ItemModel historyItem : history) {
                                copyHistoryRelatedFields(item, historyItem);
                                getDao().update(historyItem);
                            }
                            return null;
                        });
                        subscriber.onNext(item);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void copyHistoryRelatedFields(ItemModel source, ItemModel destination) {
        destination.setName(source.getName());
        destination.setCategory(source.getCategory());
        destination.setPrice(source.getPrice());
        destination.setQuantityUnit(source.getQuantityUnit());
    }

    public Observable<Void> deleteItems(final Collection<ItemModel> items) {
        return Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(final Subscriber<? super Void> subscriber) {
                        DatabaseUtils.callInTransaction(() -> {
                            getDao().delete(items);
                            return null;
                        });
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Api.scheduler())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
