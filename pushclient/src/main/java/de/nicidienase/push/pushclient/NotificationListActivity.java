package de.nicidienase.push.pushclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Date;

import de.nicidienase.push.pushclient.Model.Notification;

public class NotificationListActivity extends FragmentActivity implements NotificationListFragment.Callbacks{

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String PROPERTY_REG_ID = "reg_id";
	private static final String TAG = "PushClient";
	private static final String PROPERTY_APP_VERSION = "0.1";
	private static final String SENDER_ID = "160575761640";

	private Context context = null;
	private GoogleCloudMessaging gcm;
	protected String regid;
	private boolean mTwoPane;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();

		if (findViewById(R.id.notification_detail_container) != null) {
			mTwoPane = true;

			((NotificationListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.notification_list)).setActivateOnItemClick(true);
		}

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
				n.title = "Bacon Ipsum";
				n.message = getString(R.string.bacon_ipsum).substring(0, 80) + " [...]";
				n.long_message = getString(R.string.bacon_ipsum);
				n.received = new Date();
				n.url = "http://google.com";
				n.url_title = "google";
				n.save();
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
				break;
			case R.id.action_update:
				((NotificationListFragment)getSupportFragmentManager()
						.findFragmentById(R.id.notification_list)).updateCursor();
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
		return getSharedPreferences(NotificationListActivity.class.getSimpleName(),
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
				String devname = preferences.getString(getString(R.string.setting_key_devicename), "");
				String devId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
				String userKey = preferences.getString(getString(R.string.setting_key_user_key), "");

				return new Registrator(context).register(userKey, devname, devId, regid);
			}
		}.execute();
	}

	private void unregisterAtBackend() {
		new AsyncTask() {

			@Override
			protected Object doInBackground(Object[] params) {
				Log.d(TAG, "Unregister RegId = " + regid);
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
				String devname = preferences.getString(getString(R.string.setting_key_devicename), "");
				String devId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

				return new Registrator(context).unregister(devname, devId);
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

	@Override
	public void onItemSelected(Notification notification) {
		Notification selected = notification;
		Bundle arguments = new Bundle();
		arguments.putString("title", selected.title);
		arguments.putString("long_message", selected.long_message);
		arguments.putString("message", selected.message);
		arguments.putString("url", selected.url);
		arguments.putString("url_title", selected.url_title);
		arguments.putString("date",selected.received.toString());
		if(mTwoPane){

			NotificationDetailFragment fragment = new NotificationDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.notification_detail_container, fragment, "details").commit();
		} else {
			Intent detailIntent = new Intent(this, NotificationDetailActivity.class);
			detailIntent.putExtra("arguments",arguments);
			startActivity(detailIntent);
		}
	}
}
