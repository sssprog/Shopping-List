package com.sssprog.shoppingliststandalone.api.parsemodels;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.UUID;

@ParseClassName("List")
public class ListModel extends ParseObject {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_LOCAL_ID = "local_id";

    public String getName() {
        return getString(FIELD_NAME);
    }

    public void setName(String value) {
        put(FIELD_NAME, value);
    }

    public String getLocalId() {
        return getString(FIELD_LOCAL_ID);
    }

    public static ParseQuery<ListModel> query() {
        return ParseQuery.getQuery(ListModel.class);
    }

    public static ListModel createObject() {
        ListModel object = new ListModel();
        object.put(FIELD_LOCAL_ID, UUID.randomUUID().toString());
        return object;
    }
}
