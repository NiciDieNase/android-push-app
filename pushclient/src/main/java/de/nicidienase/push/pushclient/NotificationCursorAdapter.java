package de.nicidienase.push.pushclient;

import android.content.Context;
import android.support.v4.widget.SimpleCursorAdapter;

/**
 * Created by felix on 08/10/14.
 */
public class NotificationCursorAdapter extends SimpleCursorAdapter {
	public NotificationCursorAdapter(Context context) {
		super(context,
				R.layout.notification_list_item,
				null,
				new String[] {"title","message","received"},
				new int[] {R.id.title_view, R.id.messageText,R.id.dateText},
				0);
	}
}
