package com.thucfb.qw.phonesimcardselector;

import android.Manifest;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.settings_container, new SettingsFragment())
				.commit();

		requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
	}
}