package com.sssprog.shoppingliststandalone.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class BlockingChildrenLinearLayout extends LinearLayout {

    public BlockingChildrenLinearLayout(Context context) {
        super(context);
    }

    public BlockingChildrenLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockingChildrenLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BlockingChildrenLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
