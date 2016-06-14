package net.oschina.app.improve.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Tweet;
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
    private View.OnClickListener onReplyClickListener;
    private View.OnClickListener onPortraitClickListener;
    private OnClickReplyCallback onClickReplyCallback;
    private OnClickPortraitCallback onClickPortraitCallback;

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
        h.ivPortrait.setTag(null);
        if (TextUtils.isEmpty(item.getPortrait())){
            h.ivPortrait.setImageResource(R.drawable.widget_dface);
        }else{
            reqManager.load(item.getPortrait()).into(h.ivPortrait);
        }
        h.ivPortrait.setTag(item);
        h.ivPortrait.setOnClickListener(getOnPortraitClickListener());

        h.tvName.setText(item.getAuthor());
        h.tvContent.setText(InputHelper.displayEmoji(mContext.getResources(), item.getContent()));
        h.tvTime.setText(StringUtils.friendly_time(item.getPubDate()));

        if (AppContext.getInstance().isLogin() && AppContext.getInstance().getLoginUid() == item.getAuthorId()){
            h.btnReply.setVisibility(View.GONE);
        }else{
            h.btnReply.setVisibility(View.VISIBLE);
            h.btnReply.setTag(item);
            h.btnReply.setOnClickListener(getOnReplyClickListener());
        }
    }

    private View.OnClickListener getOnReplyClickListener(){
        if (onReplyClickListener == null){
            onReplyClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickReplyCallback == null) return;
                    Comment comment = (Comment) v.getTag();
                    onClickReplyCallback.onReplyOther(comment);
                }
            };
        }
        return onReplyClickListener;
    }

    private View.OnClickListener getOnPortraitClickListener(){
        if (onPortraitClickListener == null){
            onPortraitClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickPortraitCallback == null) return;
                    Comment comment = (Comment) v.getTag();
                    onClickPortraitCallback.onClickPortrait(comment.getAuthorId());
                }
            };
        }
        return onPortraitClickListener;
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

    public void setOnClickReplyCallback(OnClickReplyCallback callback){
        onClickReplyCallback = callback;
    }

    public void setOnClickPortraitCallback(OnClickPortraitCallback callback){
        onClickPortraitCallback = callback;
    }

    public interface OnClickReplyCallback{
        void onReplyOther(Comment comment);
    }

    public interface OnClickPortraitCallback{
        void onClickPortrait(int oid);
    }
}
