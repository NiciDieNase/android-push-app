package de.nicidienase.push.pushclient.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by felix on 21.04.14.
 */
@Table(name = "notifications")
public class Notification extends Model {

    @Column
    public String title;
    @Column
    public String message;
    @Column
    public String long_message;
    @Column
    public Date received;
    @Column
    public int priority = 0;
    @Column
    public Group group = new Group();
}
