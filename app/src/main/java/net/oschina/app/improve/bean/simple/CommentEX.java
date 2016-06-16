package net.oschina.app.improve.bean.simple;

import java.io.Serializable;

/**
 * Created by JuQiu
 * on 16/6/16.
 * 评论实体增强,适用于:问答模块
 */
public class CommentEX extends Comment {
    private Reply[] replies;

    public static class Reply implements Serializable {
        public String author;
        public String content;
    }

    public Reply[] getReplies() {
        return replies;
    }

    public void setReplies(Reply[] replies) {
        this.replies = replies;
    }
}
