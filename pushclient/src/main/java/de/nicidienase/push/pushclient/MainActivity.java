package de.nicidienase.push.pushclient;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

import de.nicidienase.push.pushclient.Model.Notification;

public class MainActivity extends Activity {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_REG_ID = "reg_id";
	private static final String TAG = "PushClient";
	private static final String PROPERTY_APP_VERSION = "0.1";
	private static final String SENDER_ID = "160575761640";

	private Context context = null;
	private GoogleCloudMessaging gcm;
	protected String regid;
	private NotificationAdapter notificationAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();


		ListFragment listFragment = new ListFragment();

		notificationAdapter = new NotificationAdapter(this);

		listFragment.setListAdapter(notificationAdapter);
		getFragmentManager().beginTransaction()
				.replace(R.id.fragmentcontainer, listFragment)
				.commit();

		PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String server = prefs.getString(context.getString(R.string.setting_key_server_url), "");
		if (server.equals("")) {
			Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
			Toast.makeText(context, "Server not set", Toast.LENGTH_SHORT);
			startActivityForResult(i, 1);
		}

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);

			if (regid.isEmpty()) {
				registerInBackground();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.controller, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				Intent j = new Intent(getApplicationContext(), SettingsActivity.class);
				startActivity(j);
				break;
			case R.id.action_add_item:
				Notification n = new Notification();
				SecureRandom random = new SecureRandom();
				n.title = "Title" + random.nextInt();
				n.message = String.valueOf(random.nextLong());
				n.received = new Date();
				n.save();
				notificationAdapter.notifyDataSetChanged();
				break;
			case R.id.action_reregister:
				if (regid.isEmpty()) {
					this.registerInBackground();
				} else {
					this.sendRegistrationIdToBackend();
				}
				break;
			case R.id.action_unregister:
				this.unregisterAtBackend();
				break;
			case R.id.action_clear:
				new Delete().from(Notification.class).execute();
				notificationAdapter.notifyDataSetChanged();
				break;
			case R.id.action_update:
				notificationAdapter.notifyDataSetChanged();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p/>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 * registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);

		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private void registerInBackground() {
		new AsyncTask() {
			@Override
			protected Object doInBackground(Object[] params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					storeRegistrationId(context, regid);
					sendRegistrationIdToBackend();
				} catch (IOException e) {
					msg = "Error: " + e.getMessage();
				}
				return msg;
			}
		}.execute();
	}

	private void sendRegistrationIdToBackend() {
		new AsyncTask() {

			@Override
			protected Object doInBackground(Object[] params) {
				Log.d(TAG, "Register RegId = " + regid);
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				String username = preferences.getString(getString(R.string.setting_key_username), "");
				String devId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

				return new Registrator(context).register(username, devId, regid);
			}
		}.execute();
	}

	private void unregisterAtBackend() {
		new AsyncTask() {

			@Override
			protected Object doInBackground(Object[] params) {
				Log.d(TAG, "Unregister RegId = " + regid);
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				String username = preferences.getString(getString(R.string.setting_key_username), "");
				String devId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

				return new Registrator(context).unregister(username, devId);
			}
		}.execute();
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId   registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}

	}
}
