package com.sssprog.shoppingliststandalone.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.sssprog.shoppingliststandalone.R;

public class PromptDialogFragment extends BaseDialogFragment<PromptDialogFragment.PromptDialogListener> {

    private static final String PARAM_HINT = "PARAM_HINT";
    private static final String PARAM_INITIAL_VALUE = "PARAM_INITIAL_VALUE";
    private static final String PARAM_CAP_SENTENCES = "PARAM_CAP_SENTENCES";

    private String initialValue;
    private EditText editText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_prompt, null);
        editText = (EditText) root.findViewById(R.id.editText);

        initialValue = getArguments().getString(PARAM_INITIAL_VALUE);
        editText.setText(initialValue);
        editText.setHint(getArguments().getString(PARAM_HINT));
        if (!getArguments().getBoolean(PARAM_CAP_SENTENCES)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        editText.setImeActionLabel(null, EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onPositiveButtonClicked();
                return true;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        });

        AlertDialog.Builder builder = buildDialog().setView(root);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                updateButtonState();
            }
        });
        return dialog;
    }

    private void updateButtonState() {
        String text = editText.getText().toString().trim();
        Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(!TextUtils.isEmpty(text));
    }

    @Override
    protected void onPositiveButtonClicked() {
        String value = editText.getText().toString().trim();
        if (!TextUtils.equals(initialValue, value)) {
            getListener().onPromptDialogPositive(getRequestCode(), value, getParams());
        }
        dismiss();
    }

    public static class PromptDialogBuilder extends BaseDialogBuilder<PromptDialogBuilder> {

        public PromptDialogBuilder(Context context) {
            super(context);
            setCapSentences(true);
        }

        public PromptDialogBuilder setInitialValue(String value) {
            args.putString(PARAM_INITIAL_VALUE, value);
            return this;
        }

        public PromptDialogBuilder setHint(String hint) {
            args.putString(PARAM_HINT, hint);
            return this;
        }

        public PromptDialogBuilder setHint(int hint) {
            return setHint(context.getString(hint));
        }

        public PromptDialogBuilder setCapSentences(boolean value) {
            args.putBoolean(PARAM_CAP_SENTENCES, value);
            return self();
        }

        public PromptDialogFragment build() {
            return build(PromptDialogFragment.class);
        }

    }

    public interface PromptDialogListener {
        void onPromptDialogPositive(int requestCode, String value, Bundle params);
    }

}
