package de.nicidienase.push.pushclient;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.activeandroid.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

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
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.d("Message_type_send_error");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.d("Message_type_deleted");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				String msg = extras.getString("msg", "");
				String title = extras.getString("title", "");
				String long_msg = extras.getString("long_msg", "");
				int priority = extras.getInt("priority", 0);

				if (!("".equals(title) && "".equals(msg))) {
					de.nicidienase.push.pushclient.Model.Notification notification = new de.nicidienase.push.pushclient.Model.Notification();
					notification.title = title;
					notification.message = msg;
					notification.long_message = long_msg;
					notification.save();

					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
					boolean notify = preferences.getBoolean(getString(R.string.setting_key_notify), false);
					boolean sound = preferences.getBoolean(getString(R.string.setting_key_sound), false);
					boolean vibrate = preferences.getBoolean(getString(R.string.setting_key_vibrate), false);
					if (notify) {
						Intent i = new Intent(this, MainActivity.class);
						TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
						stackBuilder.addNextIntent(i);
						PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

						Notification.Builder mBuilder = new Notification.Builder(this)
								.setSmallIcon(android.R.drawable.ic_dialog_email)
								.setContentTitle(title)
								.setContentText(msg)
								.setAutoCancel(true)
								.setContentIntent(pendingIntent);
						Notification noti = mBuilder.build();
						if (sound) {
							noti.defaults |= Notification.DEFAULT_SOUND;
						}
						if (vibrate) {
							noti.defaults |= Notification.DEFAULT_VIBRATE;
						}
						NotificationManager mNotificationManager =
								(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						int id = (int) System.currentTimeMillis();
						mNotificationManager.notify(id, noti);
					}
				} else {
					Log.i("empty Message");
				}
			}
		}
		GcmBroadcastReveiver.completeWakefulIntent(intent);
	}
}
