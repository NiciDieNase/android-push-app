package de.nicidienase.push.pushclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by felix on 27.04.14.
 */
public class Registrator {

	private static final String LOG_TAG = "Registrator";
	Context context;

	Registrator(Context context) {
		this.context = context;
	}

	boolean register(String userKey, String devname, String dev_id, String reg_id) {
		JSONObject params = new JSONObject();

		try {
			params.put("reg_id", reg_id);
			params.put("user_key", userKey);
		} catch (JSONException e) {
			Log.e(LOG_TAG, "JSONexception: " + e.getMessage());
		}


		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String serverUrl = prefs.getString(context.getString(R.string.setting_key_server_url), "");

		return this.sendRequest(params, devname, dev_id, serverUrl + "register");
	}

	boolean unregister(String username, String dev_id) {
		JSONObject params = new JSONObject();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String serverUrl = prefs.getString(context.getString(R.string.server_url), "");

		return this.sendRequest(params, username, dev_id, serverUrl + "unregister");
	}

	private boolean sendRequest(JSONObject parameters, String devname, String dev_id, String url) {
		try {
			parameters.put("dev_name", devname);
			parameters.put("dev_id", dev_id);
		} catch (JSONException e) {
			Log.e(LOG_TAG, "JSONexception: " + e.getMessage());
		}

		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			connection.setDoOutput(true);
			connection.setChunkedStreamingMode(0);
			connection.addRequestProperty("Content-Type","application/json");
			connection.setRequestMethod("POST");

			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(parameters.toString().getBytes());

			int responseCode = connection.getResponseCode();
			Log.d(LOG_TAG, "Response code: " + responseCode);
			Log.d(LOG_TAG, "Response message: " + connection.getResponseMessage());
			return responseCode == HttpURLConnection.HTTP_OK;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}
		return false;

	}
}
