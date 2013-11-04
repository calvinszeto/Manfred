package com.calvins.manfred;

import android.preference.PreferenceFragment;
import android.os.Bundle;

/**
 * Created by puppyplus on 11/3/13.
 */
public class SettingsFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load preferences from an XML Resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
