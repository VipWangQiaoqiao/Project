package net.oschina.app.improve.bean;

import java.io.Serializable;

/**
 * 私信
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */
public class Message implements Serializable {
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 3;
    public static final int TYPE_FILE = 5;
    private long id;
    private String content;
    private String pubDate;
    private int type;
    private String resource;
    private User sender;
    private User receiver;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}
