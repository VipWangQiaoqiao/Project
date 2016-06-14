package net.oschina.app.improve.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.User;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.CircleImageView;
import net.oschina.app.widget.TweetTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanatos on 16/6/13.
 */
public class TweetCommentAdapter extends BaseRecyclerAdapter<Comment>{

    private RequestManager reqManager;

    public TweetCommentAdapter(Context context) {
        super(context, ONLY_FOOTER);
        reqManager = Glide.with(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new TweetCommentHolderView(LayoutInflater.from(mContext).inflate(R.layout.list_item_tweet_comment, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
        TweetCommentHolderView h = (TweetCommentHolderView)holder;
        reqManager.load(item.getPortrait()).into(h.ivPortrait);
        h.tvName.setText(item.getAuthor());
        h.tvContent.setText(InputHelper.displayEmoji(mContext.getResources(), item.getContent()));
        h.tvTime.setText(StringUtils.friendly_time(item.getPubDate()));
    }

    public static final class TweetCommentHolderView extends RecyclerView.ViewHolder{
        @Bind(R.id.iv_avatar) CircleImageView ivPortrait;
        @Bind(R.id.tv_name) TextView tvName;
        @Bind(R.id.tv_pub_date) TextView tvTime;
        @Bind(R.id.btn_comment) ImageView btnReply;
        @Bind(R.id.tv_content) TweetTextView tvContent;

        public TweetCommentHolderView(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
