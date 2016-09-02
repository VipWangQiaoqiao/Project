package net.oschina.app.improve.user.bean;

import java.io.Serializable;

/**
 * Created by fei on 2016/8/30.
 * desc:
 */

public class UserFavorites implements Serializable {

    private long id;
    private int type;
    private String title;
    private String href;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return "UserFavorites{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
