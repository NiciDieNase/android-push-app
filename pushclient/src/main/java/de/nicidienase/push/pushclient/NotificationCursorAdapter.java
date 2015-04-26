package de.nicidienase.push.pushclient;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

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
		this.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				switch (cursor.getColumnName(columnIndex)) {
					case "title":
					case "message":
						((TextView) view).setText(cursor.getString(columnIndex));
						return true;
					case "recieved":
						Date d = new Date(cursor.getLong(columnIndex));
						((TextView) view).setText(d.toString());
						return true;
					default:
						return false;
				}
			}
		});
	}
}
