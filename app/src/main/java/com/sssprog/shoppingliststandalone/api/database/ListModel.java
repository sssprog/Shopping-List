package com.sssprog.shoppingliststandalone.api.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = ListModel.TABLE_NAME)
public class ListModel {

    public static final String TABLE_NAME = "list";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_NAME = "name";

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;

    @DatabaseField(columnName = FIELD_NAME)
    private String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
