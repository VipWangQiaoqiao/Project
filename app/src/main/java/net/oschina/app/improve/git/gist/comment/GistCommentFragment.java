package net.oschina.app.improve.git.gist.comment;

import net.oschina.app.improve.base.BaseRecyclerFragment;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Comment;
import net.oschina.app.improve.git.comment.CommentAdapter;
import net.oschina.app.improve.widget.SimplexToast;

/**
 * 代码片段评论
 * Created by haibin on 2017/5/11.
 */

public class GistCommentFragment extends BaseRecyclerFragment<GistCommentContract.Presenter, Comment>
        implements GistCommentContract.View {

     static GistCommentFragment newInstance() {
        return new GistCommentFragment();
    }

    @Override
    protected void onItemClick(Comment comment, int position) {
        // TODO: 2017/5/11
    }

    @Override
    public void showAddCommentSuccess(Comment comment, int strId) {
        mAdapter.addItem(comment);
    }

    @Override
    public void showMoreMore() {
        super.showMoreMore();
        mRefreshLayout.setCanLoadMore(false);
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
