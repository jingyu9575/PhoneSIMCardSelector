package com.thucfb.qw.phonesimcardselector;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

public class SimChangedReceiver extends BroadcastReceiver {
	private static final String TAG = "SimChangedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!"android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction()))
			return;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		if (!sp.getBoolean("auto_select", true))
			return;
		apply(context);
	}

	@SuppressWarnings("ConstantConditions")
	public static Pair<PhoneAccount, String> apply(Context context) {
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			Log.e(TAG, "No permission: READ_PHONE_STATE");
			return Pair.create(null, context.getString(R.string.permission_denied));
		}

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		AccountMatcher matcher = new AccountMatcher(sp);

		TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
		for (PhoneAccountHandle handle : telecomManager.getCallCapablePhoneAccounts()) {
			PhoneAccount account = telecomManager.getPhoneAccount(handle);
			if (matcher.match(account)) {
				try {
					telecomManager.getClass()
							.getMethod("setUserSelectedOutgoingPhoneAccount", PhoneAccountHandle.class)
							.invoke(telecomManager, handle);
					return Pair.create(account, null);
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
					return Pair.create(null, context.getString(R.string.permission_denied));
				}
			}
		}
		return Pair.create(null, context.getString(R.string.sim_card_not_found));
	}

	private static class AccountMatcher {
		private final Pattern pattern;

		public AccountMatcher(SharedPreferences sp) {
			pattern = Pattern.compile(sp.getString("pattern", "_^"));
		}

		public boolean match(PhoneAccount account) {
			return pattern.matcher(account.getLabel()).find() ||
					pattern.matcher(account.getAddress().toString()).find();
		}
	}
}