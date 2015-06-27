package com.sssprog.shoppingliststandalone.api.parsemodels;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("QuantityUnit")
public class QuantityUnitModel extends ParseObject {

    private static final String FIELD_NAME = "name";

    public String getName() {
        return getString(FIELD_NAME);
    }

    public void setName(String value) {
        put(FIELD_NAME, value);
    }

    public static ParseQuery<QuantityUnitModel> query() {
        return ParseQuery.getQuery(QuantityUnitModel.class);
    }

    public static QuantityUnitModel createObject() {
        return new QuantityUnitModel();
    }
}
