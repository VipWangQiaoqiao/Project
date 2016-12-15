package net.oschina.app.improve.bean;

import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.Origin;

import java.io.Serializable;

/**
 * AT我的
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */
public class Mention implements Serializable {
    private long id;
    private String content;
    private String pubDate;
    private Author author;
    private Origin origin;
    private int appClient;
    private int commentCount;

    public int getAppClient() {
        return appClient;
    }

    public void setAppClient(int appClient) {
        this.appClient = appClient;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }
}
