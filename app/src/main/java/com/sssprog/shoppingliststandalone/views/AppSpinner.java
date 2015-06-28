package com.sssprog.shoppingliststandalone.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Spinner;

import com.sssprog.shoppingliststandalone.utils.ViewUtils;


public class AppSpinner extends Spinner {

    private Drawable mBackgroundNormal;
    private Drawable mBackgroundPressed;

    public AppSpinner(Context context) {
        super(context);
        init(null);
    }

    public AppSpinner(Context context, int mode) {
        super(context, mode);
        init(null);
    }

    public AppSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AppSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public AppSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        init(attrs);
    }

    @SuppressLint("NewApi")
    public AppSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setBackgroundResource(0);
        createBackgroundDrawable();
    }

    /**
     * StateListDrawable doesn't work correctly with DrawableCompat on versions prior lollipop,
     * so we draw drawables ourselves
     */
    private void createBackgroundDrawable() {
        mBackgroundNormal = ViewUtils.getSpinnerBackgroundNormal(getContext());
        mBackgroundPressed = ViewUtils.getSpinnerBackgroundPressed(getContext());
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
        super.onSizeChanged(width, height, oldwidth, oldheight);
        mBackgroundNormal.setBounds(0, 0, width, height);
        mBackgroundPressed.setBounds(0, 0, width, height);
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isPressed()) {
            mBackgroundPressed.draw(canvas);
        } else {
            mBackgroundNormal.draw(canvas);
        }
    }

}
