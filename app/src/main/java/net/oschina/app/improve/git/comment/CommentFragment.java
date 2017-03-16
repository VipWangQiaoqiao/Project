package net.oschina.app.improve.git.comment;

import android.view.View;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Comment;
import net.oschina.app.improve.widget.SimplexToast;

/**
 * Created by haibin
 * on 2017/3/14.
 */

public class CommentFragment extends BaseRecyclerFragment<CommentContract.Presenter, Comment>
        implements CommentContract.View {

    static CommentFragment newInstance() {
        return new CommentFragment();
    }

    @Override
    protected void initData() {
        super.initData();
        ((CommentAdapter) mAdapter).setCommentListener((View.OnClickListener) getContext());
    }

    @Override
    protected void onItemClick(Comment comment, int position) {

    }

    @Override
    public void showAddCommentSuccess(Comment comment, int strId) {
        mAdapter.addItem(comment);
    }

    @Override
    public void showAddCommentFailure(int strId) {
        SimplexToast.show(mContext, strId);
    }

    @Override
    protected BaseRecyclerAdapter<Comment> getAdapter() {
        return new CommentAdapter(this);
    }
}
