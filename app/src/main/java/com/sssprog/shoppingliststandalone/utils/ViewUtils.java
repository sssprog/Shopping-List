package com.sssprog.shoppingliststandalone.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;

import com.sssprog.shoppingliststandalone.Config;
import com.sssprog.shoppingliststandalone.R;

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

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, int resId) {
        return Config.hasLollipop() ?
                context.getDrawable(resId) :
                context.getResources().getDrawable(resId);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable drawable) {
        if (Config.hasJellyBean()) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static Drawable getTintedDrawable(Context context, int drawableResId, int color) {
        Drawable drawable = DrawableCompat.wrap(ViewUtils.getDrawable(context, drawableResId).mutate());
        DrawableCompat.setTint(drawable, color);
        return drawable;
    }

    public static Drawable getGreyIconDrawable(Context context, int drawableResId) {
        return getTintedDrawable(context, drawableResId, context.getResources().getColor(R.color.light_54p));
    }

    public static Drawable getSelectableItemBackground(Context context) {
        int[] attrs = new int[] { R.attr.selectableItemBackground };
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        Drawable selectableBackground = typedArray.getDrawable(0);
        typedArray.recycle();
        return selectableBackground;
    }

    public static Drawable makeTouchFeedbackDrawable(Context context, int color) {
        return new LayerDrawable(new Drawable[] { new ColorDrawable(color), getSelectableItemBackground(context) });
    }

//    public static void tintButton(View button, int color) {
//        ColorStateList list = new ColorStateList()
////        ViewCompat.setBackgroundTintList(button, getResources().getColorStateList(R.color.accent_button));
//    }
}
