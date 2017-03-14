package net.oschina.app.improve.git.comment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.git.bean.Comment;

/**
 * Created by haibin
 * on 2017/3/14.
 */

class CommentAdapter extends BaseGeneralRecyclerAdapter<Comment> {
    CommentAdapter(Callback callback) {
        super(callback, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new CommentViewHolder(mInflater.inflate(R.layout.item_list_git_comment, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {

    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {
        CommentViewHolder(View itemView) {
            super(itemView);
        }
    }
}
