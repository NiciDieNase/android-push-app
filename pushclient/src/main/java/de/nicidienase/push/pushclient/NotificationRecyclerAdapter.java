package de.nicidienase.push.pushclient;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.Date;

import de.nicidienase.push.pushclient.Model.Notification;

/**
 * Created by felix on 25.04.15.
 */
public class NotificationRecyclerAdapter extends CursorRecyclerViewAdapter<NotificationRecyclerAdapter.ViewHolder> {


	private Context mContext;

	public NotificationRecyclerAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		mContext = context;
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
		viewHolder.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
	}


	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private final View card;
		private final TextView title;
		private final TextView text;
		private final TextView date;
		private int id;

		public ViewHolder(View itemView) {
			super(itemView);
			this.card = itemView;
			this.title = (TextView) itemView.findViewById(R.id.title_view);
			this.text = (TextView) itemView.findViewById(R.id.messageText);
			this.date = (TextView) itemView.findViewById(R.id.dateText);
			itemView.setOnClickListener(this);
		}

		public void setId(int id){
			this.id = id;
		}

		public void onClick(View v){
			Notification selected = (Notification) new Select().from(Notification.class).where("_id = ?", id).executeSingle();
			Bundle arguments = new Bundle();
			arguments.putString("title", selected.title);
			arguments.putString("long_message", selected.long_message);
			arguments.putString("message", selected.message);
			arguments.putString("url", selected.url);
			arguments.putString("url_title", selected.url_title);
			arguments.putString("date",selected.received.toString());
			Intent detailIntent = new Intent(mContext, NotificationDetailActivity.class);
			detailIntent.putExtra("arguments",arguments);
			mContext.startActivity(detailIntent);
		}
	}
}
