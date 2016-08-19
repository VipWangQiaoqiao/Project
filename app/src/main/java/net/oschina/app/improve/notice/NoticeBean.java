package net.oschina.app.improve.notice;

/**
 * Created by JuQiu on 16/8/19.
 */

public class NoticeBean {
    private int mention;
    private int letter;
    private int review;
    private int fans;
    private int like;

    public int getMention() {
        return mention;
    }

    public void setMention(int mention) {
        this.mention = mention;
    }

    public int getLetter() {
        return letter;
    }

    public void setLetter(int letter) {
        this.letter = letter;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getAllCount() {
        return mention + letter + review + fans + like;
    }
}
