package com.sssprog.shoppingliststandalone.api.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = QuantityUnitModel.TABLE_NAME)
public class QuantityUnitModel implements ModelWithName, ModelWithId {

    public static final String TABLE_NAME = "quantity_unit";
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
        if (o == null || !(o instanceof QuantityUnitModel)) {
            return false;
        }
        return id == ((QuantityUnitModel) o).id;
    }

    @Override
    public String toString() {
        return getName();
    }
}
