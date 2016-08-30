package net.oschina.app.improve.notice;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

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
        this.like = 0;
    }

    public int getAllCount() {
        return mention + letter + review + fans;
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

    NoticeBean add(NoticeBean bean) {
        this.mention += bean.mention;
        this.letter += bean.letter;
        this.review += bean.review;
        this.fans += bean.fans;
        this.like += bean.like;
        return this;
    }

    NoticeBean save(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NoticeBean.class.getName(), Context.MODE_PRIVATE).edit();
        editor.putInt("mention", mention);
        editor.putInt("letter", letter);
        editor.putInt("review", review);
        editor.putInt("fans", fans);
        editor.putInt("like", like);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        return this;
    }

    static NoticeBean getInstance(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NoticeBean.class.getName(), Context.MODE_PRIVATE);
        NoticeBean bean = new NoticeBean();
        bean.mention = preferences.getInt("mention", 0);
        bean.letter = preferences.getInt("letter", 0);
        bean.review = preferences.getInt("review", 0);
        bean.fans = preferences.getInt("fans", 0);
        bean.like = preferences.getInt("like", 0);
        return bean;
    }
}
