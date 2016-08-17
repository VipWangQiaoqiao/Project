package net.oschina.app.improve.user.adapter;

import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.bean.simple.Comment;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserCommentAdapter extends BaseListAdapter<Comment> {
    public UserCommentAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Comment item, int position) {

    }

    @Override
    protected int getLayoutId(int position, Comment item) {
        return 0;
    }
}
