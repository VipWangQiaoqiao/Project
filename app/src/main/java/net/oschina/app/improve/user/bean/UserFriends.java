package net.oschina.app.improve.user.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by fei
 * on 2016/12/23.
 * desc:
 */

public class UserFriends implements Serializable, Comparable<UserFriends> {

    private long id;
    private String portrait;
    private String name;
    private int showViewType;
    private String showLabel;
    private transient boolean isGoneLine;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShowViewType() {
        return showViewType;
    }

    public void setShowViewType(int showViewType) {
        this.showViewType = showViewType;
    }

    public String getShowLabel() {
        return showLabel;
    }

    public void setShowLabel(String showLabel) {
        this.showLabel = showLabel;
    }

    public boolean isGoneLine() {
        return isGoneLine;
    }

    public void setGoneLine(boolean goneLine) {
        isGoneLine = goneLine;
    }

    @Override
    public String toString() {
        return "UserFriends{" +
                "id=" + id +
                ", portrait='" + portrait + '\'' +
                ", name='" + name + '\'' +
                ", showViewType=" + showViewType +
                ", showLabel='" + showLabel + '\'' +
                ", isGoneLine=" + isGoneLine +
                '}';
    }

    @Override
    public int compareTo(@NonNull UserFriends o) {
        String showLabel = o.getShowLabel();
        String tempCompLabel = this.showLabel;

        return tempCompLabel.compareTo(showLabel);
    }
}
