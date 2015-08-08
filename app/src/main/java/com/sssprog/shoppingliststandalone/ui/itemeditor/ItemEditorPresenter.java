package com.sssprog.shoppingliststandalone.ui.itemeditor;

import com.sssprog.shoppingliststandalone.App;
import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.SimpleRxSubscriber;
import com.sssprog.shoppingliststandalone.api.database.CategoryModel;
import com.sssprog.shoppingliststandalone.api.database.ItemModel;
import com.sssprog.shoppingliststandalone.api.database.QuantityUnitModel;
import com.sssprog.shoppingliststandalone.api.services.CategoryService;
import com.sssprog.shoppingliststandalone.api.services.ItemService;
import com.sssprog.shoppingliststandalone.api.services.QuantityUnitService;
import com.sssprog.shoppingliststandalone.mvp.Presenter;
import com.sssprog.shoppingliststandalone.utils.Utils;

import java.util.List;

import rx.Observable;

public class ItemEditorPresenter extends Presenter<ItemEditorActivity> {

    public void loadData(long itemId) {
        Observable.zip(ItemService.getInstance().get(itemId),
                CategoryService.getInstance().getAll(),
                QuantityUnitService.getInstance().getAll(),
                (item, categories, units) -> {
                    Utils.sortByName(categories);
                    Utils.sortByName(units);
                    return new Data(item, addDefaultCategory(categories), addDefaultQuantityUnit(units));
                })
                .subscribe(new SimpleRxSubscriber<Data>() {
                    @Override
                    public void onNext(final Data data) {
                        runViewAction(() -> getView().onDataLoaded(data.item, data.categories, data.units));
                    }
                });
    }

    private List<CategoryModel> addDefaultCategory(List<CategoryModel> categories) {
        CategoryModel empty = new CategoryModel();
        empty.setName(App.getInstance().getString(R.string.default_category));
        categories.add(0, empty);
        return categories;
    }

    private List<QuantityUnitModel> addDefaultQuantityUnit(List<QuantityUnitModel> units) {
        QuantityUnitModel empty = new QuantityUnitModel();
        empty.setName(App.getInstance().getString(R.string.default_quantity_unit));
        units.add(0, empty);
        return units;
    }

    public void save(ItemModel item) {
        ItemService.getInstance().saveAndUpdateHistory(item).subscribe(new SimpleRxSubscriber<ItemModel>() {
            @Override
            public void onCompleted() {
                runViewAction(() -> getView().onItemSaved());
            }
        });
    }

    private static class Data {
        ItemModel item;
        List<CategoryModel> categories;
        List<QuantityUnitModel> units;

        public Data(ItemModel item, List<CategoryModel> categories, List<QuantityUnitModel> units) {
            this.item = item;
            this.categories = categories;
            this.units = units;
        }
    }

}
