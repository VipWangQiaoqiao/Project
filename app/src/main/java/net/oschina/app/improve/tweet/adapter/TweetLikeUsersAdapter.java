package net.oschina.app.improve.tweet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.simple.TweetLike;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by
 * thanatos on 16/6/13.
 * Updated by
 * fei on 17/01/11.
 */
public class TweetLikeUsersAdapter extends BaseRecyclerAdapter<TweetLike> {
    private View.OnClickListener onPortraitClickListener;

    public TweetLikeUsersAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new LikeUsersHolderView(LayoutInflater.from(mContext).inflate(R.layout.list_cell_tweet_like_user, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, TweetLike item, int position) {
        LikeUsersHolderView h = (LikeUsersHolderView) holder;
        h.identityView.setup(item.getAuthor());
        h.ivPortrait.setup(item.getAuthor());
        h.ivPortrait.setTag(R.id.iv_tag, item);
        h.ivPortrait.setOnClickListener(getOnPortraitClickListener());
        h.tvName.setText(item.getAuthor().getName());
    }

    private View.OnClickListener getOnPortraitClickListener() {
        if (onPortraitClickListener == null) {
            onPortraitClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object object = v.getTag(R.id.iv_tag);
                    if (object != null && object instanceof TweetLike) {
                        TweetLike liker = (TweetLike) object;
                        UIHelper.showUserCenter(mContext, liker.getAuthor().getId(), liker.getAuthor().getName());
                    }
                }
            };
        }
        return onPortraitClickListener;
    }

    public static final class LikeUsersHolderView extends RecyclerView.ViewHolder {
        @Bind(R.id.identityView)
        IdentityView identityView;
        @Bind(R.id.iv_avatar)
        PortraitView ivPortrait;
        @Bind(R.id.tv_name)
        TextView tvName;

        public LikeUsersHolderView(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
