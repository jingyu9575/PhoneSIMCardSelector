package com.thucfb.qw.phonesimcardselector;

import android.os.Bundle;
import android.telecom.PhoneAccount;
import android.util.Pair;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.root_preferences, rootKey);

		Preference selectNow = findPreference("select_now");
		selectNow.setOnPreferenceClickListener(v -> {
			Pair<PhoneAccount, String> result = SimChangedReceiver.apply(getContext());
			if (result.second != null) {
				Toast.makeText(getContext(), result.second, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getContext(), getString(R.string.card_selected, result.first.getLabel()),
						Toast.LENGTH_SHORT).show();
			}
			return true;
		});
	}
}