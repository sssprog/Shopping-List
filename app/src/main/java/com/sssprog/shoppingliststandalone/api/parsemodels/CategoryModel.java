package com.sssprog.shoppingliststandalone.api.parsemodels;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Category")
public class CategoryModel extends ParseObject {

    private static final String FIELD_NAME = "name";

    public String getName() {
        return getString(FIELD_NAME);
    }

    public void setName(String value) {
        put(FIELD_NAME, value);
    }

    public static ParseQuery<CategoryModel> query() {
        return ParseQuery.getQuery(CategoryModel.class);
    }

    public static CategoryModel createObject() {
        return new CategoryModel();
    }
}
