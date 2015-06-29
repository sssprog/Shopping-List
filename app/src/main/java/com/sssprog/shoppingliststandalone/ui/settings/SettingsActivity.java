package com.sssprog.shoppingliststandalone.ui.settings;

import android.os.Bundle;

import com.sssprog.shoppingliststandalone.R;
import com.sssprog.shoppingliststandalone.ui.BaseActivity;

public class SettingsActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();
    }


}
