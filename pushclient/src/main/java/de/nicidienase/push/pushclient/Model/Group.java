package de.nicidienase.push.pushclient.Model;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by felix on 21.04.14.
 */
@Table(name = "groups")
public class Group {

    @Column
    public String groupname ="";
}
