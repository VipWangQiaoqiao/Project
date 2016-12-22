package net.oschina.app.improve.bean.simple;

/**
 * 动弹赞和取消赞
 * Created by huanghaibin_dev
 * on 2016/7/25.
 */
public class TweetLikeReverse {
    private Author author;
    private boolean liked;
    private int likeCount;

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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
