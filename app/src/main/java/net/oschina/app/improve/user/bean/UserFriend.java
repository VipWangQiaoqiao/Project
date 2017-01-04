package net.oschina.app.improve.user.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by fei
 * on 2016/12/23.
 * desc:
 */

public class UserFriend implements Serializable, Comparable<UserFriend> {

    private long id;
    private String portrait;
    private String name;
    private int showViewType;
    private String showLabel;
    private transient boolean isGoneLine;
    private transient boolean isSelected;
    private transient int selectPosition;

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    @Override
    public String toString() {
        return "UserFriend{" +
                "id=" + id +
                ", portrait='" + portrait + '\'' +
                ", name='" + name + '\'' +
                ", showViewType=" + showViewType +
                ", showLabel='" + showLabel + '\'' +
                ", isGoneLine=" + isGoneLine +
                ", isSelected=" + isSelected +
                ", selectPosition=" + selectPosition +
                '}';
    }

    @Override
    public int compareTo(@NonNull UserFriend o) {
        String showLabel = o.getShowLabel();
        String tempCompLabel = this.showLabel;

        return tempCompLabel.compareTo(showLabel);
    }

}
