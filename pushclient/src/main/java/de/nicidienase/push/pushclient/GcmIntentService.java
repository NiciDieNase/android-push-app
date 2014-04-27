package de.nicidienase.push.pushclient;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by felix on 27.04.14.
 */
public class GcmIntentService extends IntentService {
	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 */
	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

	}
}
