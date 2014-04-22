package de.nicidienase.push.pushclient;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import de.nicidienase.push.pushclient.Model.Notification;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PreferenceManager.setDefaultValues(this, R.xml.settings, false);

		ListFragment listFragment = new ListFragment();

		NotificationAdapter notificationAdapter = new NotificationAdapter(this);

		listFragment.setListAdapter(notificationAdapter);
		getFragmentManager().beginTransaction()
				.replace(R.id.fragmentcontainer, listFragment)
				.commit();
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
		case R.id.action_add_items:
			for(int i = 0; i<10;i++){
				Notification n = new Notification();
				n.title = "Title " + i;
				n.message = "Message " + i;
				n.received = new Date();
				n.save();
			}

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
