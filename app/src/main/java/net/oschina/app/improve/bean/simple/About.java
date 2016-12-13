package net.oschina.app.improve.bean.simple;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

import net.oschina.app.improve.bean.Tweet;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by JuQiu
 * on 16/6/16.
 * 相关推荐实体
 */
public class About implements Serializable {
    private long id;
    private String title;
    private String content;
    private int type;
    private String href;
    private int viewCount;
    private int commentCount;
    private int transmitCount;
    private Tweet.Image[] images;
    @Expose
    private long commitTweetId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Tweet.Image[] getImages() {
        return images;
    }

    public void setImages(Tweet.Image[] images) {
        this.images = images;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTransmitCount() {
        return transmitCount;
    }

    public void setTransmitCount(int transmitCount) {
        this.transmitCount = transmitCount;
    }

    public long getCommitTweetId() {
        return commitTweetId;
    }

    public void setCommitTweetId(long commitTweetId) {
        this.commitTweetId = commitTweetId;
    }

    public static class Statistics implements Serializable {
        private int comment;
        private int view;
        private int transmit;

        public int getComment() {
            return comment;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }

        public int getTransmit() {
            return transmit;
        }

        public void setTransmit(int transmit) {
            this.transmit = transmit;
        }
    }

    @Override
    public String toString() {
        return "About{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", href='" + href + '\'' +
                ", viewCount=" + viewCount +
                ", commentCount=" + commentCount +
                ", transmitCount=" + transmitCount +
                ", images=" + Arrays.toString(images) +
                '}';
    }

    /**
     * 检查一个About节点是否有效
     *
     * @param about About
     * @return True 则有效
     */
    public static boolean check(About about) {
        return about != null
                && !(about.id <= 0 && about.type <= 0 && TextUtils.isEmpty(about.href));
    }

    public static Share buildShare(About about) {
        Share share = new Share();
        share.id = about.id;
        share.type = about.type;
        share.title = about.title;
        share.content = about.content;
        return share;
    }

    public static Share buildShare(About about, long commitTweetId) {
        Share share = buildShare(about);
        share.commitTweetId = about.commitTweetId;
        return share;
    }

    public static Share buildShare(long id, int type) {
        Share share = new Share();
        share.id = id;
        share.type = type;
        return share;
    }

    /**
     * 动弹分享节点
     */
    public static class Share implements Serializable {
        public long id;
        public int type;
        public long commitTweetId;
        public String title;
        public String content;
        public long fromTweetId;

        @Override
        public String toString() {
            return "Share{" +
                    "id=" + id +
                    ", type=" + type +
                    ", commitTweetId=" + commitTweetId +
                    ", title='" + title + '\'' +
                    ", content='" + content + '\'' +
                    ", fromTweetId=" + fromTweetId +
                    '}';
        }
    }

    /**
     * 检查About节点
     *
     * @param share Share
     * @return 返回分享节点是否正确
     */
    public static boolean check(Share share) {
        return share != null && share.id > 0 && share.type >= 0;
    }
}