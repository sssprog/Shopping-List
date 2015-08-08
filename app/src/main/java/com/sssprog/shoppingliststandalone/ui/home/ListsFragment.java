package com.sssprog.shoppingliststandalone.ui.home;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;

import com.annimon.stream.Stream;
import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.api.database.ListModel;
import com.sssprog.shoppingliststandalone.dialogs.AlertDialogFragment;
import com.sssprog.shoppingliststandalone.dialogs.PromptDialogFragment;
import com.sssprog.shoppingliststandalone.events.ListChangedEvent;
import com.sssprog.shoppingliststandalone.mvp.PresenterClass;
import com.sssprog.shoppingliststandalone.ui.BaseMvpFragment;
import com.sssprog.shoppingliststandalone.utils.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

@PresenterClass(ListsPresenter.class)
public class ListsFragment extends BaseMvpFragment<ListsPresenter> implements
        NavigationView.OnNavigationItemSelectedListener,
        PromptDialogFragment.PromptDialogListener,
        AlertDialogFragment.AlertDialogListener {

    private static final int DIALOG_ADD_LIST = 0;
    private static final int DIALOG_RENAME_LIST = 1;
    private static final int DIALOG_DELETE_LIST = 2;

    private Menu navigationMenu;
    private List<ListModel> items = new ArrayList<>();
    private final Map<Integer, ListModel> menuItems = new HashMap<>();

    public ListsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPresenter().loadLists();
    }

    public void setData(Menu navigationMenu) {
        this.navigationMenu = navigationMenu;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_list:
                new PromptDialogFragment.PromptDialogBuilder(getActivity())
                        .setRequestCode(DIALOG_ADD_LIST)
                        .setTargetFragment(this)
                        .setTitle(R.string.create_list)
                        .setHint(R.string.list_name)
                        .setPositiveButtonText(R.string.save)
                        .setNegativeButtonText(R.string.cancel)
                        .build()
                        .show(getFragmentManager(), null);
                break;
        }
        ListModel list = menuItems.get(item.getItemId());
        if (list != null) {
            Prefs.putLong(R.string.pref_current_list_id, list.getId());
            updateCheckedMenuItem();
            updateActivityTitle();
            dispatchOnItemSelected();
        }
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_delete_list).setEnabled(items.size() > 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rename_list:
                new PromptDialogFragment.PromptDialogBuilder(getActivity())
                        .setRequestCode(DIALOG_RENAME_LIST)
                        .setTargetFragment(this)
                        .setTitle(R.string.rename_list)
                        .setHint(R.string.list_name)
                        .setInitialValue(getCurrentList().getName())
                        .setPositiveButtonText(R.string.rename)
                        .setNegativeButtonText(R.string.cancel)
                        .build()
                        .show(getFragmentManager(), null);
                return true;
            case R.id.action_delete_list:
                new AlertDialogFragment.AlertDialogBuilder(getActivity())
                        .setRequestCode(DIALOG_DELETE_LIST)
                        .setTargetFragment(this)
                        .setMessage(R.string.delete_list_confirmation)
                        .setPositiveButtonText(R.string.delete)
                        .setNegativeButtonText(R.string.cancel)
                        .build()
                        .show(getFragmentManager(), null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void onListsLoaded(List<ListModel> items) {
        this.items = items;
        removeListMenuItems();
        MenuIdFinder idFinder = new MenuIdFinder(navigationMenu);
        int order = 1;
        for (ListModel list : items) {
            int id = idFinder.nextId();
            menuItems.put(id, list);
            navigationMenu.add(R.id.lists_group, id, order, list.getName())
                    .setIcon(R.drawable.ic_shopping_basket_black_24dp)
                    .setCheckable(true);
            order++;
        }
        updateCheckedMenuItem();
        updateActivityTitle();
        dispatchOnItemSelected();
    }

    private void updateCheckedMenuItem() {
        ListModel currentItem = getCurrentList();
        for (int i = 0; i < navigationMenu.size(); i++) {
            MenuItem item = navigationMenu.getItem(i);
            if (menuItems.containsKey(item.getItemId())) {
                ListModel list = menuItems.get(item.getItemId());
                item.setChecked(list == currentItem);
            }
        }
    }

    private void updateActivityTitle() {
        ListModel currentItem = getCurrentList();
        getActivity().setTitle(currentItem != null ? currentItem.getName() : getString(R.string.app_name));
    }

    public ListModel getCurrentList() {
        return Stream.of(items)
                .filter(item -> item.getId() == Prefs.getLong(R.string.pref_current_list_id))
                .findFirst()
                .orElse(!items.isEmpty() ? items.get(0) : null);
    }

    private void removeListMenuItems() {
        for (int id : menuItems.keySet()) {
            navigationMenu.removeItem(id);
        }
        menuItems.clear();
    }

    @Override
    public void onPromptDialogPositive(int requestCode, String value, Bundle params) {
        switch (requestCode) {
            case DIALOG_ADD_LIST:
                ListModel list = new ListModel();
                list.setName(value);
                getPresenter().addItem(list);
                break;
            case DIALOG_RENAME_LIST:
                list = getCurrentList();
                if (list != null) {
                    list.setName(value);
                    getPresenter().saveList(list);
                }
                break;
        }
    }

    private void dispatchOnItemSelected() {
        EventBus.getDefault().post(new ListChangedEvent());
    }

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == DIALOG_DELETE_LIST) {
            ListModel list = getCurrentList();
            if (list != null) {
                getPresenter().deleteList(list);
            }
        }
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
    }
}
