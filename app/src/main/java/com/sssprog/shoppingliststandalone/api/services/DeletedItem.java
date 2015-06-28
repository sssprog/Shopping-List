package com.sssprog.shoppingliststandalone.api.services;

class DeletedItem<T> {
    T item;
    long time;

    public DeletedItem(T item, long time) {
        this.item = item;
        this.time = time;
    }
}
