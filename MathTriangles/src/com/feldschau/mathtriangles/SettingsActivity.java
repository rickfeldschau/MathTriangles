package com.feldschau.mathtriangles;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final String KEY_PREF_MAX_NUMBER = "pref_max";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
		findPreference(KEY_PREF_MAX_NUMBER).setSummary(prefs.getString(
				KEY_PREF_MAX_NUMBER,
				"The maximum sum that will be randomly generated."));
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_PREF_MAX_NUMBER)) {
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
