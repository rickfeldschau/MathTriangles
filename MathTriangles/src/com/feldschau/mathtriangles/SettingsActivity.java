package com.feldschau.mathtriangles;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.InputType;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final String KEY_PREF_MAX_NUMBER = "pref_max";
	public static final String KEY_PREF_MAX_DIVISOR = "pref_divisor";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
		findPreference(KEY_PREF_MAX_NUMBER).setSummary(prefs.getString(
				KEY_PREF_MAX_NUMBER,
				"The maximum sum that will be randomly generated for addition/subtraction."));
		findPreference(KEY_PREF_MAX_DIVISOR).setSummary(prefs.getString(
				KEY_PREF_MAX_DIVISOR,
				"The maximum divisor that will be randomly generated for multiplication/division."));
		
		EditTextPreference textPref = (EditTextPreference)findPreference(KEY_PREF_MAX_NUMBER);
		textPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		
		EditTextPreference textPrefDivisor = (EditTextPreference)findPreference(KEY_PREF_MAX_DIVISOR);
		textPrefDivisor.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_PREF_MAX_NUMBER) || key.equals(KEY_PREF_MAX_DIVISOR)) {
			Preference maxNumberPref = findPreference(key);
			maxNumberPref.setSummary(sharedPreferences.getString(key,""));
		}	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
}
