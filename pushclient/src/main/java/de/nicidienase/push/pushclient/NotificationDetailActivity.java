package de.nicidienase.push.pushclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * Created by felix on 08/10/14.
 */
public class NotificationDetailActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		if(getIntent().getExtras().containsKey("arguments")){
			Bundle arguments = getIntent().getBundleExtra("arguments");
			NotificationDetailsFragment fragment = new NotificationDetailsFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.notification_detail_container,fragment)
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if(id == android.R.id.home){
			NavUtils.navigateUpTo(this,new Intent(this, MainActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
