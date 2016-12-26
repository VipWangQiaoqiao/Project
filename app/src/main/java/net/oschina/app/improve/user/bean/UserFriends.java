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
    private transient boolean isCheck;

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

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "UserFriends{" +
                "portrait='" + portrait + '\'' +
                ", name='" + name + '\'' +
                ", showViewType=" + showViewType +
                ", showLabel='" + showLabel + '\'' +
                ", isCheck=" + isCheck +
                '}';
    }

    @Override
    public int compareTo(@NonNull UserFriends o) {
        String showLabel = o.getShowLabel();
        String compLabel = showLabel == null ? "" : showLabel;

        String tempCompLabel = this.showLabel == null ? "" : this.showLabel;

        return tempCompLabel.compareTo(compLabel);
    }
}
