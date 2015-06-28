package com.sssprog.shoppingliststandalone.ui.dictionary;

import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.ModelWithId;
import com.sssprog.shoppingliststandalone.api.database.ModelWithName;
import com.sssprog.shoppingliststandalone.api.services.BaseModelService;
import com.sssprog.shoppingliststandalone.mvp.Presenter;

import java.util.List;

public class DictionaryPresenter<Model extends ModelWithId & ModelWithName> extends Presenter<DictionaryActivity<Model>> {

    private BaseModelService<Model> service;

    public void setService(BaseModelService<Model> service) {
        this.service = service;
    }

    public void loadItems() {
        service.getAll().subscribe(new SimpleRxSubscriber<List<Model>>() {
            @Override
            public void onNext(final List<Model> items) {
                runViewAction(new Runnable() {
                    @Override
                    public void run() {
                        getView().onItemsLoaded(items);
                    }
                });
            }
        });
    }

    public void addItem(Model item) {
        service.save(item).subscribe(new SimpleRxSubscriber<Model>() {
            @Override
            public void onCompleted() {
                loadItems();
            }
        });
    }

    public void deleteItem(Model item) {
        service.deleteWithDelay(item);
    }

    public boolean cancelDeletion(Model item) {
        return service.cancelDeletion(item);
    }

    public void finishDeletion() {
        service.finishDeletion();
    }

    public void saveItem(Model item) {
        service.save(item).subscribe(new SimpleRxSubscriber<Model>());
    }

}
