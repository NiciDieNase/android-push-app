package de.nicidienase.push.pushclient;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by felix on 25.04.15.
 */
public class NotificationRecyclerAdapter extends CursorRecyclerViewAdapter<NotificationRecyclerAdapter.ViewHolder> {


	public NotificationRecyclerAdapter(Context context, Cursor cursor) {
		super(context, cursor);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.notification_list_item,parent,false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
		viewHolder.title.setText(cursor.getString(cursor.getColumnIndex("title")));
		viewHolder.text.setText(cursor.getString(cursor.getColumnIndex("message")));
		Date received = new Date(cursor.getLong(cursor.getColumnIndex("received")));
		viewHolder.date.setText(received.toString());
	}


	public class ViewHolder extends RecyclerView.ViewHolder {
		private final View card;
		private final TextView title;
		private final TextView text;
		private final TextView date;

		public ViewHolder(View itemView) {
			super(itemView);
			this.card = itemView;
			this.title = (TextView) itemView.findViewById(R.id.title_view);
			this.text = (TextView) itemView.findViewById(R.id.messageText);
			this.date = (TextView) itemView.findViewById(R.id.dateText);
		}
	}
}
