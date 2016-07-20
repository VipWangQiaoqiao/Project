package net.oschina.app.improve.tweet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.SoftwareTweetLike;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.TweetTextView;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by fei on 2016/7/20.
 */

public class SoftwareTweetAdapter extends BaseRecyclerAdapter<Tweet> implements View.OnClickListener {

    private static final String TAG = "SoftwareTweetAdapter";
    private RequestManager requestManager;

    public SoftwareTweetAdapter(Context context, int mode) {
        super(context, mode);
        requestManager = Glide.with(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new SoftwareTweetViewholder(mInflater.inflate(R.layout.item_list_tweet_improve, parent, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Tweet item, int position) {
        SoftwareTweetViewholder vh = (SoftwareTweetViewholder) holder;
        if (TextUtils.isEmpty(item.getAuthor().getPortrait())) {
            vh.icon.setImageResource(R.drawable.widget_dface);
        } else {
            requestManager.load(item.getAuthor().getPortrait())
                    .asBitmap()
                    .placeholder(mContext.getResources().getDrawable(R.drawable.widget_dface))
                    .error(mContext.getResources().getDrawable(R.drawable.widget_dface))
                    .into(vh.icon);
        }
        vh.name.setText(item.getAuthor().getName());
        CommentsUtil.formatHtml(mContext.getResources(), vh.content, item.getContent());
        vh.pubTime.setText(StringUtils.friendly_time(item.getPubDate()));
        PlatfromUtil.setPlatFromString(vh.deviceType, item.getAppClient());
        boolean liked = item.isLiked();
        if (liked) {
            vh.likeStatus.setImageResource(R.drawable.ic_thumbup_actived);
        } else {
            vh.likeStatus.setImageResource(R.drawable.ic_thumbup_normal);
        }
        vh.likeStatus.setTag(position);
        vh.likeStatus.setOnClickListener(this);
        vh.likeCount.setText(item.getLikeCount() + "");
        vh.commentCount.setText(item.getCommentCount() + "");

    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);

    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();

        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(mContext);
            return;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return;
        }
        final Tweet item = getItem(position);
        OSChinaApi.pubSoftwareLike(item.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(mContext, "操作失败...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<SoftwareTweetLike>>() {
                    }.getType();
                    ResultBean resultBean = AppContext.createGson().fromJson(responseString, type);
                    if (resultBean.getCode() == 1) {
                        SoftwareTweetLike softwareTweetLike = (SoftwareTweetLike) resultBean.getResult();
                        boolean like = softwareTweetLike.isLike();
                        item.setLiked(like);
                        int likeCount = item.getLikeCount();
                        if (!like) {
                            item.setLikeCount((likeCount - 1));
                        } else {
                            item.setLikeCount((likeCount + 1));
                        }
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "操作失败...", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    static class SoftwareTweetViewholder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_tweet_face)
        CircleImageView icon;
        @Bind(R.id.tv_tweet_name)
        TextView name;
        @Bind(R.id.tweet_item)
        TweetTextView content;
        @Bind(R.id.tv_tweet_time)
        TextView pubTime;
        @Bind(R.id.tv_tweet_platform)
        TextView deviceType;
        @Bind(R.id.iv_like_state)
        ImageView likeStatus;
        @Bind(R.id.tv_tweet_like_count)
        TextView likeCount;
        @Bind(R.id.tv_tweet_comment_count)
        TextView commentCount;

        SoftwareTweetViewholder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
