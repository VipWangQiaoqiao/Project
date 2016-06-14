package net.oschina.app.improve.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.User;
import net.oschina.app.widget.AvatarView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanatos on 16/6/13.
 */
public class TweetLikeUsersAdapter extends BaseRecyclerAdapter<User>{

    private RequestManager reqManager;

    public TweetLikeUsersAdapter(Context context) {
        super(context, ONLY_FOOTER);
        reqManager = Glide.with(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new LikeUsersHolderView(LayoutInflater.from(mContext).inflate(R.layout.list_cell_tweet_like_user, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, User item, int position) {
        LikeUsersHolderView h = (LikeUsersHolderView)holder;
        reqManager.load(item.getPortrait()).into(h.ivPortrait);
        h.tvName.setText(item.getName());
    }

    public static final class LikeUsersHolderView extends RecyclerView.ViewHolder{
        @Bind(R.id.iv_avatar) AvatarView ivPortrait;
        @Bind(R.id.tv_name) TextView tvName;

        public LikeUsersHolderView(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
