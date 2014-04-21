package de.nicidienase.push.pushclient;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        ListFragment listFragment = new ListFragment();
        listFragment.setListAdapter(new NotificationAdapter(this));
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

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
