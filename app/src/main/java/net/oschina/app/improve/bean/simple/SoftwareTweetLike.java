package net.oschina.app.improve.bean.simple;

import java.io.Serializable;

/**
 * Created by fei on 2016/7/19.
 * desc：
 */

public class SoftwareTweetLike implements Serializable {

    private Author author;
    private boolean isLike;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    @Override
    public String toString() {
        return "SoftwareTweetLike{" +
                "author=" + author +
                ", isLike=" + isLike +
                '}';
    }
}
