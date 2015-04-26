package de.nicidienase.push.pushclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by felix on 08/10/14.
 */
public class NotificationDetailActivity extends Activity{

	private static final String TAG = "NotificationDetail";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_activity);

		try {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);
		}catch (NullPointerException e){
			Log.d(TAG, e.getMessage());
		}


		if(getIntent().getExtras().containsKey("arguments")){
			Bundle arguments = getIntent().getBundleExtra("arguments");
			NotificationDetailFragment fragment = new NotificationDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.add(R.id.notification_detail_container,fragment)
					.commit();
		}
	}

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 int id = item.getItemId();

		 if(id == android.R.id.home){
			 NavUtils.navigateUpTo(this, new Intent(this, NotificationListActivity.class));
			 return true;
		 }
		 return super.onOptionsItemSelected(item);
	 }

}
