package de.nicidienase.push.pushclient;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.nicidienase.push.pushclient.Model.Notification;

/**
 * Created by felix on 21.04.14.
 */
public class NotificationAdapter extends BaseAdapter{

	List<Notification> notifications = new ArrayList<Notification>();
	private Context mContext;

	NotificationAdapter(Context context){
		mContext = context;
		updateNotifications();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		updateNotifications();
	}

	private void updateNotifications() {
		new AsyncTask(){
			@Override
			protected Object doInBackground(Object[] params) {
				notifications = new Select()
						.from(Notification.class)
						.orderBy("received DESC")
						.execute();
			return params[0];
			}

			@Override
			protected void onPostExecute(Object o) {
				((NotificationAdapter) o).notifyDataSetChanged();
			}
		}.execute(this);
}

	@Override
	public int getCount() {
		return notifications.size();
	}

	@Override
	public Object getItem(int position) {
		return notifications.get(position);
	}

	@Override
	public long getItemId(int position) {
		return notifications.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		this.updateNotifications();
		TextView title;
		TextView message;
		TextView date;
		if(convertView == null){
			LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.notification_list_item, null);
		}
		title = (TextView) convertView.findViewById(R.id.titleText);
		message = (TextView) convertView.findViewById(R.id.messageText);
		date = (TextView) convertView.findViewById(R.id.dateText);

		title.setText(notifications.get(position).title);
		message.setText(notifications.get(position).message);
		date.setText(notifications.get(position).received.toString());

	return convertView;
	}
}
