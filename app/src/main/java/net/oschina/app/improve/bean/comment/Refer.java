package net.oschina.app.improve.bean.comment;

import java.io.Serializable;

/**
 * Created by fei
 * on 2016/11/15.
 * desc:评论的引用,比如对他人评论的引用
 */

public class Refer implements Serializable {
    private String author;
    private String content;
    private String pubDate;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
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

    @Override
    public String toString() {
        return "Refer{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }
}
