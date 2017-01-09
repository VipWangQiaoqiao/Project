package net.oschina.app.improve.notice;

import android.content.Context;

import net.oschina.common.helper.SharedPreferencesHelper;

import java.io.Serializable;

/**
 * Created by JuQiu
 * on 16/8/19.
 * Note: like count always zero
 */
public class NoticeBean implements Serializable {
    private int mention;
    private int letter;
    private int review;
    private int fans;
    private int like = 0;

    public int getMention() {
        return mention;
    }

    void setMention(int mention) {
        this.mention = mention;
    }

    public int getLetter() {
        return letter;
    }

    void setLetter(int letter) {
        this.letter = letter;
    }

    public int getReview() {
        return review;
    }

    void setReview(int review) {
        this.review = review;
    }

    public int getFans() {
        return fans;
    }

    void setFans(int fans) {
        this.fans = fans;
    }

    public int getLike() {
        return like;
    }

    void setLike(int like) {
        this.like = 0;
    }

    public int getAllCount() {
        return mention + letter + review + fans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoticeBean that = (NoticeBean) o;

        return mention == that.mention
                && letter == that.letter
                && review == that.review
                && fans == that.fans
                && like == that.like;

    }

    @Override
    public String toString() {
        return "NoticeBean{" +
                "mention=" + mention +
                ", letter=" + letter +
                ", review=" + review +
                ", fans=" + fans +
                ", like=" + like +
                '}';
    }

    void clear() {
        this.mention = 0;
        this.letter = 0;
        this.review = 0;
        this.fans = 0;
        this.like = 0;
    }

    NoticeBean set(NoticeBean bean) {
        this.mention = bean.mention;
        this.letter = bean.letter;
        this.review = bean.review;
        this.fans = bean.fans;
        // 暂不累加点赞数据
        //this.like = bean.like;
        return this;
    }

    NoticeBean add(NoticeBean bean) {
        this.mention += bean.mention;
        this.letter += bean.letter;
        this.review += bean.review;
        this.fans += bean.fans;
        // 暂不累加点赞数据
        //this.like += bean.like;
        return this;
    }

    NoticeBean save(Context context) {
        SharedPreferencesHelper.save(context, this);
        return this;
    }

    static NoticeBean getInstance(Context context) {
        NoticeBean bean = SharedPreferencesHelper.load(context, NoticeBean.class);
        if (bean == null)
            bean = new NoticeBean();
        return bean;
    }
}
