package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.bean.simple.Comment;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserCommentAdapter extends BaseGeneralRecyclerAdapter<Comment> {
    public UserCommentAdapter(Callback callback) {
        super(callback,ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return null;
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {

    }
}
