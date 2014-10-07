package de.nicidienase.push.pushclient;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.activeandroid.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by felix on 27.04.14.
 */
public class GcmIntentService extends IntentService {
	private static final int MSG_LENGTH = 80;

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

		int notificationID = 2342;

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.d("Message_type_send_error");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.d("Message_type_deleted");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				String msg = extras.getString("msg", "");
				String title = extras.getString("title", "");
				String url = extras.getString("url","");
				String url_title = extras.getString("url_title","");
				String token = extras.getString("token","");
				String sound = extras.getString("sound","");
				int priority = extras.getInt("priority", 0);

				if (!("".equals(title) && "".equals(msg))) {
					de.nicidienase.push.pushclient.Model.Notification notification = new de.nicidienase.push.pushclient.Model.Notification();
					notification.title = title;
					if( msg.length() > MSG_LENGTH){
						notification.long_message = msg;
						notification.message = msg.substring(0,MSG_LENGTH) + " [...]";
					} else {
						notification.message = msg;
						notification.long_message = "";
					}
					notification.url = url;
					notification.url_title = url_title;
					notification.sound = sound;
					notification.token = token;
					notification.priority = priority;
					notification.save();

					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
					boolean notify = preferences.getBoolean(getString(R.string.setting_key_notify), false);
					boolean play_sound = preferences.getBoolean(getString(R.string.setting_key_sound), false);
					boolean vibrate = preferences.getBoolean(getString(R.string.setting_key_vibrate), false);
					if (notify) {
						Intent i = new Intent(this, MainActivity.class);
						TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
						stackBuilder.addNextIntent(i);
						PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
								.setSmallIcon(android.R.drawable.sym_action_email)
								.setContentTitle(notification.title)
								.setContentText(notification.message)
								.setAutoCancel(true)
								.setContentIntent(pendingIntent)
								.setNumber(5);

						if(!notification.url.isEmpty()){
							Uri page = Uri.parse(notification.url);
							Intent open_url = new Intent(Intent.ACTION_VIEW, page);
							PendingIntent pi = PendingIntent.getActivity(this,0,open_url,0);
							mBuilder.addAction(R.drawable.ic_action_web_site ,getString(R.string.open_url), pi);
						}

						Notification noti = mBuilder.build();

						if (play_sound) {
							noti.defaults |= Notification.DEFAULT_SOUND;
						}
						if (vibrate) {
							noti.defaults |= Notification.DEFAULT_VIBRATE;
						}
						NotificationManagerCompat mNotificationManager =
								NotificationManagerCompat.from(this);
						mNotificationManager.notify(notificationID, noti);
					}
				} else {
					Log.i("empty Message");
				}
			}
		}
		GcmBroadcastReveiver.completeWakefulIntent(intent);
	}
}
