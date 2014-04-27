package de.nicidienase.push.pushclient;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

		if(!extras.isEmpty()){
			if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){
				Log.d("Message_type_send_error");
			} else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){
				Log.d("Message_type_deleted");
			} else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
				String msg = intent.getExtras().getString( "msg" );
				String title = intent.getExtras().getString( "title");
				String long_msg = intent.getExtras().getString("long_msg");

				de.nicidienase.push.pushclient.Model.Notification notification = new de.nicidienase.push.pushclient.Model.Notification();
				notification.title = title;
				notification.message = msg;
				notification.long_message = long_msg;
				notification.save();

				Notification.Builder mBuilder = new Notification.Builder(this)
						.setSmallIcon(android.R.drawable.ic_dialog_email)
						.setContentTitle(title)
						.setContentText(msg)
						.setAutoCancel(true);
				Notification noti = mBuilder.getNotification();
				noti.defaults |= Notification.DEFAULT_ALL;

				NotificationManager mNotificationManager =
						(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				int id = (int)System.currentTimeMillis();
				mNotificationManager.notify(id, noti );
			}
		}
	}
}
