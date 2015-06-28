package com.sssprog.shoppingliststandalone.api.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = CategoryModel.TABLE_NAME)
public class CategoryModel implements ModelWithName, ModelWithId {

    public static final String TABLE_NAME = "category";
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

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CategoryModel)) {
            return false;
        }
        return id == ((CategoryModel) o).id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
