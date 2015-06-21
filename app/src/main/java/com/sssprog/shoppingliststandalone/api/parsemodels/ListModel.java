package com.sssprog.shoppingliststandalone.api.parsemodels;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("List")
public class ListModel extends ParseObject {

    private static final String FIELD_NAME = "name";


    public String getName() {
        return getString(FIELD_NAME);
    }

    public void setName(String value) {
        put(FIELD_NAME, value);
    }

    public static ParseQuery<ListModel> query() {
        return ParseQuery.getQuery(ListModel.class);
    }
}
