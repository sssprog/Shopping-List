package com.sssprog.shoppingliststandalone.api.database;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.sssprog.shoppingliststandalone.utils.NumberUtils;

import junit.framework.Assert;

import java.math.BigDecimal;

@DatabaseTable(tableName = ItemModel.TABLE_NAME)
public class ItemModel implements ModelWithName {

    public static final String TABLE_NAME = "item";
    public static final String FIELD_ID = "_id";
    public static final String FIELD_STRUCK_OUT = "striked_out";
    public static final String FIELD_QUANTITY = "quantity";
    public static final String FIELD_LIST = "list_id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_QUANTITY_UNIT = "quantity_unit_id";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_CATEGORY = "category_id";
    public static final String FIELD_NOTE = "note";

    @DatabaseField(generatedId = true, columnName = FIELD_ID)
    private long id;

    @DatabaseField(columnName = FIELD_NAME, index = true)
    private String name;

    @DatabaseField(columnName = FIELD_PRICE, useGetSet = true)
    private BigDecimal price;

    @DatabaseField(columnName = FIELD_QUANTITY_UNIT, foreign = true, foreignAutoRefresh = true)
    private QuantityUnitModel quantityUnit;

    @DatabaseField(columnName = FIELD_CATEGORY, foreign = true, foreignAutoRefresh = true)
    private CategoryModel category;

    @DatabaseField(columnName = FIELD_STRUCK_OUT)
    private boolean struckOut;

    @DatabaseField(columnName = FIELD_QUANTITY, useGetSet = true)
    private BigDecimal quantity;

    @DatabaseField(columnName = FIELD_LIST, foreign = true, foreignAutoRefresh = true, index = true)
    private ListModel list;

    @DatabaseField(columnName = FIELD_NOTE)
    private String note;

    public ItemModel() {
        price = BigDecimal.ZERO;
        quantity = BigDecimal.ZERO;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QuantityUnitModel getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(QuantityUnitModel quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }

    public boolean isStruckOut() {
        return struckOut;
    }

    public void setStruckOut(boolean struckOut) {
        this.struckOut = struckOut;
    }

    public ListModel getList() {
        return list;
    }

    public void setList(ListModel list) {
        this.list = list;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @NonNull
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        Assert.assertNotNull(quantity);
        this.quantity = NumberUtils.roundQuantity(quantity);
    }

    @NonNull
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        Assert.assertNotNull(price);
        this.price = NumberUtils.roundPrice(price);
    }

    public BigDecimal getTotalPrice() {
        return NumberUtils.roundPrice(price.multiply(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ItemModel)) {
            return false;
        }
        return id == ((ItemModel) o).id;
    }

}
