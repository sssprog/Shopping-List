package com.sssprog.shoppingliststandalone.ui.settings;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.utils.CurrencyHelper;
import com.sssprog.shoppingliststandalone.utils.NumberUtils;
import com.sssprog.shoppingliststandalone.utils.ViewUtils;

import java.math.BigDecimal;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class CurrencyPreference extends DialogPreference {

    private static final BigDecimal PREVIEW_PRICE = new BigDecimal("1.20");

    @InjectView(R.id.symbol)
    EditText symbolView;
    @InjectView(R.id.positionSpinner)
    Spinner positionSpinner;
    @InjectView(R.id.preview)
    TextView previewView;

    private CurrencyHelper.Currency value;
    private CurrencyHelper.Currency newValue;

    public CurrencyPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public CurrencyPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setDialogLayoutResource(R.layout.currency_preference);
        value = CurrencyHelper.parseCurrency(null);
        newValue = value.copy();
        setPositiveButtonText(R.string.save);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ButterKnife.inject(this, view);
        symbolView.setText(newValue.symbol);
        String[] items = getContext().getResources().getStringArray(R.array.currency_position);
        positionSpinner.setAdapter(ViewUtils.getSpinnerAdapter(getContext(), Arrays.asList(items)));
        positionSpinner.setSelection(newValue.position == CurrencyHelper.CurrencyPosition.LEFT ? 0 : 1);
        updatePreview();
        symbolView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                newValue.symbol = s.toString();
                updatePreview();
            }
        });
        positionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newValue.position = position == 0 ? CurrencyHelper.CurrencyPosition.LEFT :
                        CurrencyHelper.CurrencyPosition.RIGHT;
                updatePreview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updatePreview() {
        previewView.setText(NumberUtils.priceWithCurrency(PREVIEW_PRICE, newValue));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            value = newValue.copy();
            persistString(CurrencyHelper.toString(value));
            notifyChanged();
        } else {
            newValue = value.copy();
        }
        super.onDialogClosed(positiveResult);
    }

    @Override
    public CharSequence getSummary() {
        return getContext().getString(R.string.currency_summary, NumberUtils.priceWithCurrency(PREVIEW_PRICE, value));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        value = CurrencyHelper.parseCurrency(restoreValue ? getPersistedString(null) : (String) defaultValue);
        newValue = value.copy();
    }

    public void setValue(String value) {
        this.value = CurrencyHelper.parseCurrency(value);
        newValue = this.value.copy();
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        // Check whether this Preference is persistent (continually saved)
        if (isPersistent()) {
            // No need to save instance state since it's persistent, use superclass state
            return superState;
        }

        // Create instance of custom BaseSavedState
        final SavedState myState = new SavedState(superState);
        // Set the state's value with the class member that holds current setting value
        myState.value = value;
        myState.newValue = newValue;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Cast state to custom BaseSavedState and pass to superclass
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());

        value = myState.value;
        newValue = myState.newValue;
    }

    private static class SavedState extends BaseSavedState {
        CurrencyHelper.Currency value;
        CurrencyHelper.Currency newValue;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = CurrencyHelper.parseCurrency(source.readString());
            newValue = CurrencyHelper.parseCurrency(source.readString());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeString(CurrencyHelper.toString(value));
            dest.writeString(CurrencyHelper.toString(newValue));
        }

        // Standard creator object using an instance of this class
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }


}
