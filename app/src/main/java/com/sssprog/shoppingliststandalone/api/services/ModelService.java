package com.sssprog.shoppingliststandalone.api.services;

import rx.Observable;

public interface ModelService<T> {

    Observable<T> save(T item);

    Observable<Void> delete(T item);

    void deleteWithDelay(T item);

    boolean cancelDeletion(T item);

    void finishDeletion();

}
