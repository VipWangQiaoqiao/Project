package net.oschina.app.improve.bean.simple;

import java.io.Serializable;

/**
 * Created by huanghaibin_dev
 * on 2016/7/18.
 */
public class Author implements Serializable{
    private long id;
    private String name;
    private String portrait;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
