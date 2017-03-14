package net.oschina.app.improve.git.comment;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Comment;

/**
 * Created by haibin
 * on 2017/3/14.
 */

public class CommentFragment extends BaseRecyclerFragment<CommentContract.Presenter, Comment> {

    static CommentFragment newInstance() {
        return new CommentFragment();
    }

    @Override
    protected void onItemClick(Comment comment, int position) {

    }

    @Override
    protected BaseRecyclerAdapter<Comment> getAdapter() {
        return new CommentAdapter(this);
    }
}
