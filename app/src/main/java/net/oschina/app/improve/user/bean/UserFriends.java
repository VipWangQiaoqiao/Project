package net.oschina.app.improve.user.bean;

import java.io.Serializable;

/**
 * Created by fei
 * on 2016/12/23.
 * desc:
 */

public class UserFriends implements Serializable {

    private UserFansOrFollows userFansOrFollows;
    private int showVIewType;
    private String showLabel;
    private boolean isCheck;

    public UserFansOrFollows getUserFansOrFollows() {
        return userFansOrFollows;
    }

    public void setUserFansOrFollows(UserFansOrFollows userFansOrFollows) {
        this.userFansOrFollows = userFansOrFollows;
    }

    public int getShowVIewType() {
        return showVIewType;
    }

    public void setShowVIewType(int showVIewType) {
        this.showVIewType = showVIewType;
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
                "userFansOrFollows=" + userFansOrFollows +
                ", showVIewType=" + showVIewType +
                ", showLabel=" + showLabel +
                ", isCheck=" + isCheck +
                '}';
    }
}
