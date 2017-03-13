package net.oschina.app.improve.git.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 分支
 * Created by haibin
 * on 2016/12/9.
 */

public class Branch implements Serializable {
    private String name;
    @SerializedName("protected")
    private boolean isProtected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }
}
