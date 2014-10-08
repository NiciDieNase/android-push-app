package de.nicidienase.push.pushclient;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by felix on 08/10/14.
 */
public class NotificationDetailsFragment extends Fragment{

	private String title = "";
	private String message = "";
	private String long_message = "";
	private String url = "";
	private String url_title = "";
	private String date = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getArguments().containsKey("title")){
			title = getArguments().getString("title");
		}
		if(getArguments().containsKey("message")){
			message = getArguments().getString("message");
		}
		if(getArguments().containsKey("long_message")){
			long_message = getArguments().getString("long_message");
		}
		if(getArguments().containsKey("url")){
			url = getArguments().getString("url");
		}
		if(getArguments().containsKey("url_title")){
			url_title = getArguments().getString("url_title");
		}
		if(getArguments().containsKey("date")){
			date = getArguments().getString("date");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.notfication_detail,container,false);

		((TextView)rootView.findViewById(R.id.title_view)).setText(title);
		((TextView)rootView.findViewById(R.id.date_view)).setText(date);
		String msg;
		if(long_message.isEmpty()){
			msg = message;
		} else {
			msg = long_message;
		}
		((TextView)rootView.findViewById(R.id.message_view)).setText(msg);
		return rootView;
	}
}
