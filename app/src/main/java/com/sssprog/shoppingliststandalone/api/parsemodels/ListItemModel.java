package com.sssprog.shoppingliststandalone.api.parsemodels;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sssprog.shoppingliststandalone.utils.NumberUtils;

import java.math.BigDecimal;

@ParseClassName("ListItem")
public class ListItemModel extends ParseObject {

    public static final String FIELD_HISTORY_ITEM = "history_item";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_STRIKED_OUT = "striked_out";
    public static final String FIELD_QUANTITY = "quantity";
    public static final String FIELD_LIST = "list_id";
    public static final String FIELD_NOTE = "note";

    public HistoryItemModel getHistoryItem() {
        return (HistoryItemModel) get(FIELD_HISTORY_ITEM);
    }

    public void setHistoryItem(HistoryItemModel historyItem) {
        put(FIELD_HISTORY_ITEM, historyItem);
    }

    public ListModel getList() {
        return (ListModel) get(FIELD_LIST);
    }

    public void setList(ListModel list) {
        put(FIELD_LIST, list);
    }

    public BigDecimal getPrice() {
        return NumberUtils.stringToPrice(getString(FIELD_PRICE));
    }

    public void setPrice(BigDecimal value) {
        put(FIELD_PRICE, NumberUtils.roundPrice(value).toString());
    }

    public BigDecimal getQuantity() {
        return NumberUtils.stringToQuantity(getString(FIELD_QUANTITY));
    }

    public void setQuantity(BigDecimal value) {
        put(FIELD_QUANTITY, NumberUtils.roundQuantity(value).toString());
    }

    public boolean isStrikedOut() {
        return getBoolean(FIELD_STRIKED_OUT);
    }

    public void setStrikedOut(boolean value) {
        put(FIELD_STRIKED_OUT, value);
    }

    public String getNote() {
        return getString(FIELD_NOTE);
    }

    public void setNote(String value) {
        put(FIELD_NOTE, value);
    }

    public static ParseQuery<ListItemModel> query() {
        return ParseQuery.getQuery(ListItemModel.class);
    }

    public static ListItemModel createObject() {
        ListItemModel object = new ListItemModel();
        return object;
    }
}
