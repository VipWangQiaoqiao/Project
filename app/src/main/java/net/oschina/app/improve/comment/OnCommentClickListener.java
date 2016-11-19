package net.oschina.app.improve.comment;

import android.view.View;

import net.oschina.app.improve.bean.comment.Comment;

/**
 * Created by JuQiu
 * on 16/6/21.
 */

public interface OnCommentClickListener {
    void onClick(View view, Comment comment);
}
