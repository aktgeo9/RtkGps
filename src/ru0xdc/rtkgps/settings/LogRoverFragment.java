package ru0xdc.rtkgps.settings;

import javax.annotation.Nonnull;

import ru0xdc.rtkgps.BuildConfig;
import ru0xdc.rtkgps.R;
import ru0xdc.rtkgps.settings.widget.StreamTypePreference;
import ru0xdc.rtklib.RtkServerSettings.LogStream;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;


public class LogRoverFragment extends PreferenceFragment {

	private static final boolean DBG = BuildConfig.DEBUG & true;

	static final String SHARED_PREFS_NAME = "LogRover";

	protected static final String KEY_ENABLE = "enable";
	protected static final String KEY_TYPE = "type";
	protected static final String KEY_STREAM_SETTINGS_BUTTON = "stream_settings_button";

	private final PreferenceChangeListener mPreferenceChangeListener;

	public LogRoverFragment() {
		mPreferenceChangeListener = new PreferenceChangeListener();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (DBG) Log.v(getSharedPreferenceName(), "onCreate() bundle: " + savedInstanceState);

		getPreferenceManager().setSharedPreferencesName(getSharedPreferenceName());

		initPreferenceScreen();
		findPreference(KEY_STREAM_SETTINGS_BUTTON).setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				streamSettingsButtonClicked();
				return true;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (DBG) Log.v(getSharedPreferenceName(), "onResume()");
		refresh();
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
	}

	@Override
	public void onPause() {
		if (DBG) Log.v(getSharedPreferenceName(), "onPause()");
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
		super.onPause();
	}

	protected String getSharedPreferenceName() {
		return SHARED_PREFS_NAME;
	}

	protected void initPreferenceScreen() {
		final StreamTypePreference typePref;

		if (DBG) Log.v(getSharedPreferenceName(), "initPreferenceScreen()");

		addPreferencesFromResource(R.xml.log_stream_settings);

		typePref = (StreamTypePreference)findPreference(KEY_TYPE);
		typePref.setValues(InputRoverFragment.INPUT_STREAM_TYPES);
		typePref.setDefaultValue(InputRoverFragment.DEFAULT_STREAM_TYPE);
	}

	protected void streamSettingsButtonClicked() {
		final Intent intent;
		final Bundle fragmentArgs;
		final StreamTypePreference typePref;

		intent = new Intent(getActivity(), StreamDialogActivity.class);

		typePref = (StreamTypePreference) findPreference(KEY_TYPE);

		fragmentArgs = new Bundle(1);
		fragmentArgs.putString(StreamDialogActivity.ARG_SHARED_PREFS_NAME, getSharedPreferenceName());

		intent.putExtra(StreamDialogActivity.ARG_FRAGMENT_ARGUMENTS, fragmentArgs);
		intent.putExtra(StreamDialogActivity.ARG_FRAGMENT_TYPE, typePref.getValueT().name());

		startActivity(intent);
	}

	private void refresh() {
		final StreamTypePreference typePref;

		if (DBG) Log.v(getSharedPreferenceName(), "refresh()");

		typePref = (StreamTypePreference) findPreference(KEY_TYPE);
    	typePref.setSummary(getString(typePref.getValueT().getNameResId()));
	}

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			refresh();
		}
    };

	@Nonnull
	public static LogStream readPrefs(Context ctx) {
		return SettingsHelper.readLogStreamPrefs(ctx, SHARED_PREFS_NAME);
	}

    public static void setDefaultValues(Context ctx, boolean force) {
    	SettingsHelper.setLogStreamDefaultValues(ctx, SHARED_PREFS_NAME, force);
    }

}
