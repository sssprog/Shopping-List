package com.sssprog.shoppingliststandalone.ui.settings;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.text.InputType;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.utils.Prefs;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.preferences);

        EditTextPreference taxPercent = (EditTextPreference) findPreference(Prefs.getKey(R.string.prefs_tax_percent));
        taxPercent.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }
}
