package net.oschina.app.improve.git.comment;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.git.bean.Comment;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;

import de.hdodenhof.circleimageview.CircleImageView;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
        CommentViewHolder h = (CommentViewHolder) holder;
        Author author = item.getAuthor();
        mCallBack.getImgLoader()
                .load(author.getPortrait())
                .asBitmap()
                .placeholder(R.mipmap.widget_default_face)
                .into(h.mImageOwner);
        h.mTextName.setText(author.getName());
        h.mTextComment.setText(item.getContent());
        h.mTextPubDate.setText(String.format("%s楼 ", (position + 1)) + StringUtils.formatSomeAgo(item.getPubDate()));
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mImageOwner;
        TextView mTextName, mTextPubDate;
        TweetTextView mTextComment;

        CommentViewHolder(View itemView) {
            super(itemView);
            mImageOwner = (CircleImageView) itemView.findViewById(R.id.civ_owner);
            mTextName = (TextView) itemView.findViewById(R.id.tv_name);
            mTextPubDate = (TextView) itemView.findViewById(R.id.tv_pub_date);
            mTextComment = (TweetTextView) itemView.findViewById(R.id.tv_comment);
        }
    }
}
