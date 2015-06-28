package com.sssprog.shoppingliststandalone.utils;

import com.sssprog.shoppingliststandalone.api.database.ModelWithName;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Utils {

    public static <T extends ModelWithName> void sortByName(List<T> list) {
        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T lhs, T rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
    }

}
