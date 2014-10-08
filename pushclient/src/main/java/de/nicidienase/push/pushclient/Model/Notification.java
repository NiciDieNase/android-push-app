package de.nicidienase.push.pushclient.Model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by felix on 21.04.14.
 */
@Table(name = "notifications",id = BaseColumns._ID)
public class Notification extends Model {

	@Column
	public String title;
	@Column
	public String message;
	@Column
	public String long_message;
	@Column(index = true)
	public Date received = new Date();
	@Column
	public int priority = 0;
	@Column
	public Group group = new Group();
	@Column
	public String url;
	@Column
	public String url_title;
	@Column
	public String sound;
	@Column
	public String token;
}
