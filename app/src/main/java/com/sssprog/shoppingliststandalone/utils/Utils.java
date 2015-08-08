package com.sssprog.shoppingliststandalone.utils;

import com.sssprog.shoppingliststandalone.api.database.ModelWithName;

import java.util.Collections;
import java.util.List;

public class Utils {

    public static <T extends ModelWithName> void sortByName(List<T> list) {
        Collections.sort(list, (lhs, rhs) -> lhs.getName().compareToIgnoreCase(rhs.getName()));
    }

}
