package com.sssprog.shoppingliststandalone.api.services;

import java.util.List;

import rx.Observable;

public interface ModelQueryAllService<T> {
    Observable<List<T>> getAll();
}
