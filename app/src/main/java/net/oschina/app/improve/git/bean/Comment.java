package net.oschina.app.improve.git.bean;

import net.oschina.app.improve.bean.simple.Author;

import java.io.Serializable;

/**
 * Created by haibin
 * on 2017/3/14.
 */

public class Comment implements Serializable {
    private int id;
    private Author author;
    private String content;
    private String pubDate;
    private int appClient;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
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

    public int getAppClient() {
        return appClient;
    }

    public void setAppClient(int appClient) {
        this.appClient = appClient;
    }
}
