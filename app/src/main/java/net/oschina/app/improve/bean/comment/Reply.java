package net.oschina.app.improve.bean.comment;

import net.oschina.app.improve.bean.simple.Author;

import java.io.Serializable;

/**
 * Created by fei
 * on 2016/11/15.
 * desc:  对某一个引用评论的回复(评论)
 */

public class Reply implements Serializable {
    private long id;
    private Author author;
    private String content;
    private String pubDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", author=" + author +
                ", content='" + content + '\'' +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }
}
