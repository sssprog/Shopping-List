package com.sssprog.shoppingliststandalone.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class ViewUtils {

    public static <T extends Fragment> T findOrAddFragment(FragmentActivity activity, Class<T> cls) {
        String tag = cls.getSimpleName();
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = Fragment.instantiate(activity, cls.getName());
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragment, tag)
                    .commit();
        }
        return (T) fragment;
    }

}
