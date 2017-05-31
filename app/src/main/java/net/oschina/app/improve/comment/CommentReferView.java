package net.oschina.app.improve.comment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.comment.Refer;

/**
 * Created by fei
 * on 2016/11/21.
 * desc:
 */

public class CommentReferView extends LinearLayout {

    public CommentReferView(Context context) {
        super(context);
        initView();
    }

    public CommentReferView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CommentReferView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
    }


    public void addComment(final Comment comment) {
        removeAllViews();//因为在list中有复用问题，不同的refers长度不同，并且不一样，所以需要先清除掉原先的布局，包括
        Refer[] refers = comment.getRefer();

        if (refers != null && refers.length > 0) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View child = CommentsUtil.getReferLayout(inflater, refers, 0);
            addView(child, indexOfChild(child));
        }
    }
}
