package net.oschina.app.improve.bean.simple;

/**
 * Created by thanatos on 16/7/19.
 */
public class TweetLike {

    private String pubDate;
    private Author author;
    private boolean liked;

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

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
