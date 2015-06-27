package com.sssprog.shoppingliststandalone.api.parsemodels;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sssprog.shoppingliststandalone.utils.NumberUtils;

import java.math.BigDecimal;
import java.util.UUID;

@ParseClassName("HistoryItem")
public class HistoryItemModel extends ParseObject {

    private static final String FIELD_NAME = "name";
    private static final String FIELD_LOCAL_ID = "local_id";
    public static final String FIELD_QUANTITY_UNIT = "quantity_unit_id";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_CATEGORY = "category_id";

    public String getName() {
        return getString(FIELD_NAME);
    }

    public void setName(String value) {
        put(FIELD_NAME, value);
    }

    public String getLocalId() {
        return getString(FIELD_LOCAL_ID);
    }

    public QuantityUnitModel getQuantityUnit() {
        return (QuantityUnitModel) get(FIELD_QUANTITY_UNIT);
    }

    public void setQuantityUnit(QuantityUnitModel quantityUnit) {
        put(FIELD_QUANTITY_UNIT, quantityUnit);
    }

    public CategoryModel getCategory() {
        return (CategoryModel) get(FIELD_CATEGORY);
    }

    public void setCategory(CategoryModel category) {
        put(FIELD_CATEGORY, category);
    }

    public BigDecimal getPrice() {
        return NumberUtils.stringToPrice(getString(FIELD_PRICE));
    }

    public void setPrice(BigDecimal value) {
        put(FIELD_PRICE, NumberUtils.roundPrice(value).toString());
    }

    public static ParseQuery<HistoryItemModel> query() {
        return ParseQuery.getQuery(HistoryItemModel.class);
    }

    public static HistoryItemModel createObject() {
        HistoryItemModel object = new HistoryItemModel();
        object.put(FIELD_LOCAL_ID, UUID.randomUUID().toString());
        object.setPrice(BigDecimal.ZERO);
        return object;
    }
}
