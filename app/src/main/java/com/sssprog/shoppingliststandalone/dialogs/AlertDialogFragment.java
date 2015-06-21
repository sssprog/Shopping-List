package com.sssprog.shoppingliststandalone.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class AlertDialogFragment extends BaseDialogFragment<AlertDialogFragment.AlertDialogListener> {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return buildDialog().create();
    }

    @Override
    protected void onNegativeButtonClicked() {
        dismiss();
        getListener().onNegativeButtonClicked(getRequestCode());
    }

    protected void onPositiveButtonClicked() {
        dismiss();
        getListener().onPositiveButtonClicked(getRequestCode());
    }

    public interface AlertDialogListener {
        void onPositiveButtonClicked(int requestCode);
        void onNegativeButtonClicked(int requestCode);
    }

    public static class AlertDialogBuilder extends BaseDialogBuilder<AlertDialogBuilder> {

        public AlertDialogBuilder(Context context) {
            super(context);
        }

        public AlertDialogFragment build() {
//            AlertDialogFragment result = new AlertDialogFragment();
//            result.setArguments(args);
//            return result;
            return build(AlertDialogFragment.class);
        }

    }
}
