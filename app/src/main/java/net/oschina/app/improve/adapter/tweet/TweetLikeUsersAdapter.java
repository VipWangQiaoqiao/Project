package net.oschina.app.improve.adapter.tweet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.User;
import net.oschina.app.improve.adapter.base.BaseRecyclerAdapter;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thanatos on 16/6/13.
 */
public class TweetLikeUsersAdapter extends BaseRecyclerAdapter<User> {

    private RequestManager reqManager;
    private View.OnClickListener onPortraitClickListener;

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
        h.ivPortrait.setTag(null);
        if (TextUtils.isEmpty(item.getPortrait())){
            h.ivPortrait.setImageResource(R.drawable.widget_dface);
        }else{
            reqManager.load(item.getPortrait())
                    .asBitmap()
                    .placeholder(mContext.getResources().getDrawable(R.drawable.widget_dface))
                    .error(mContext.getResources().getDrawable(R.drawable.widget_dface))
                    .into(h.ivPortrait);
        }
        h.ivPortrait.setTag(item);
        h.ivPortrait.setOnClickListener(getOnPortraitClickListener());
        h.tvName.setText(item.getName());
    }

    private View.OnClickListener getOnPortraitClickListener(){
        if (onPortraitClickListener == null){
            onPortraitClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = (User) v.getTag();
                    UIHelper.showUserCenter(mContext, user.getId(), user.getName());
                }
            };
        }
        return onPortraitClickListener;
    }

    public static final class LikeUsersHolderView extends RecyclerView.ViewHolder{
        @Bind(R.id.iv_avatar) CircleImageView ivPortrait;
        @Bind(R.id.tv_name) TextView tvName;

        public LikeUsersHolderView(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
