package de.nicidienase.push.pushclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by felix on 27.04.14.
 */
public class Registrator {

	private static final String LOG_TAG = "Registrator";
	Context context;

	Registrator(Context context){
		this.context = context;
	}

	boolean register(String username, String dev_id, String reg_id){
		JSONObject params = new JSONObject();

		try {
			params.put("reg_id",reg_id);
		} catch (JSONException e) {
			Log.e(LOG_TAG, "JSONexception: " + e.getMessage());
		}


		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String serverUrl = prefs.getString(context.getString(R.string.server_url_setting_key),"");

		return this.sendRequest(params,username, dev_id,serverUrl + "register/");
	}

	boolean unregister(String username, String dev_id){
		JSONObject params = new JSONObject();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String serverUrl = prefs.getString(context.getString(R.string.server_url),"");

		return this.sendRequest(params,username, dev_id, serverUrl + "unregister/");
	}

	private boolean sendRequest(JSONObject parameters, String username, String dev_id, String url){
		try {
			parameters.put("username",username);
			parameters.put("dev_id",dev_id);
		} catch (JSONException e) {
			Log.e(LOG_TAG, "JSONexception: " + e.getMessage());
		}

		HttpPost request = new HttpPost(url);
		StringEntity entity;
		try {
			entity = new StringEntity(parameters.toString(), HTTP.UTF_8);
			entity.setContentType("application/json");
			request.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			Log.e(LOG_TAG, "UnsupportedEncodingException: " + e);
			return false;
		}

		HttpClient client = new DefaultHttpClient();
		try {

			HttpResponse httpResponse = client.execute(request);
			Integer responseCode = httpResponse.getStatusLine().getStatusCode();
			String responseMessage = httpResponse.getStatusLine().getReasonPhrase();

			Log.d(LOG_TAG, "Response code: " + responseCode);
			Log.d(LOG_TAG, "Response message: " + responseMessage);

			return responseCode == HttpStatus.SC_OK;

		} catch (ClientProtocolException e)  {
			client.getConnectionManager().shutdown();
			Log.e(LOG_TAG, "ClientProtocolException: " + e);
		} catch (IOException e) {
			client.getConnectionManager().shutdown();
			Log.e(LOG_TAG, "IOException: " + e);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception: " + e);
		}
		return false;

	}
}
