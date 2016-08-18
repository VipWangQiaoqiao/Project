package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.widget.TweetTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserCommentAdapter extends BaseGeneralRecyclerAdapter<Comment> {
    public UserCommentAdapter(Callback callback) {
        super(callback, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new UserCommentAdapter.CommentViewHolder(mInflater.inflate(R.layout.item_list_comment, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {

    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_user_avatar;
        TextView tv_user_name, tv_time, tv_platform, tv_comment_count;
        TweetTextView tv_content, tv_origin;

        public CommentViewHolder(View itemView) {
            super(itemView);
            iv_user_avatar = (CircleImageView) itemView.findViewById(R.id.iv_user_avatar);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TweetTextView) itemView.findViewById(R.id.tv_content);
            tv_origin = (TweetTextView) itemView.findViewById(R.id.tv_origin);
            tv_platform = (TextView) itemView.findViewById(R.id.tv_platform);
            tv_comment_count = (TextView) itemView.findViewById(R.id.tv_comment_count);
        }
    }
}
