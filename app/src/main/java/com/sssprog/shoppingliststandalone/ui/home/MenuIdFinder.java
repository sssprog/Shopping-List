package com.sssprog.shoppingliststandalone.ui.home;

import android.view.Menu;

import java.util.HashSet;
import java.util.Set;

public class MenuIdFinder {

    private final Menu menu;
    private final Set<Integer> ids = new HashSet<>();
    private int currentId;

    public MenuIdFinder(Menu menu) {
        this.menu = menu;
        for (int i = 0; i < menu.size(); i++) {
            ids.add(menu.getItem(i).getItemId());
        }
    }

    public int nextId() {
        while (ids.contains(currentId)) {
            currentId = (currentId + 1) % Integer.MAX_VALUE;
        }
        ids.add(currentId);
        return currentId;
    }
}
