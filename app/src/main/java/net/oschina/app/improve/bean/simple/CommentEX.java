package net.oschina.app.improve.bean.simple;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by JuQiu
 * on 16/6/16.
 * 评论实体增强,适用于:问答模块
 */
public class CommentEX extends Comment {

    public static final int VOTE_STATE_DEFAULT = 0;
    public static final int VOTE_STATE_UP = 1;
    public static final int VOTE_STATE_DOWN = 2;

    private int voteCount;
    private boolean best;
    private int voteState;
    @SerializedName("reply")
    private Reply[] replies;

    public static class Reply implements Serializable {
        private String authorId;
        private String author;
        private String content;
        private String authorPortrait;
        private String pubDate;

        public String getAuthorId() {
            return authorId;
        }

        public void setAuthorId(String authorId) {
            this.authorId = authorId;
        }

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

        public String getAuthorPortrait() {
            return authorPortrait;
        }

        public void setAuthorPortrait(String authorPortrait) {
            this.authorPortrait = authorPortrait;
        }

        public String getPubDate() {
            return pubDate;
        }

        public void setPubDate(String pubDate) {
            this.pubDate = pubDate;
        }
    }

    public int getVoteState() {
        return voteState;
    }

    public void setVoteState(int voteState) {
        this.voteState = voteState;
    }

    public Reply[] getReplies() {
        return replies;
    }

    public void setReplies(Reply[] replies) {
        this.replies = replies;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isBest() {
        return best;
    }

    public void setBest(boolean best) {
        this.best = best;
    }
}
