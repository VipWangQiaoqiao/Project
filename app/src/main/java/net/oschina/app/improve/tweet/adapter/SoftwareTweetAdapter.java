package net.oschina.app.improve.tweet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.TweetLikeReverse;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.TweetTextView;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by fei
 * on 2016/7/20.
 */
public class SoftwareTweetAdapter extends BaseRecyclerAdapter<Tweet> implements View.OnClickListener {

    private RequestManager requestManager;

    public SoftwareTweetAdapter(Context context, int mode) {
        super(context, mode);
        setState(BaseRecyclerAdapter.STATE_LOADING, false);
        requestManager = Glide.with(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new SoftwareTweetViewHolder(mInflater.inflate(R.layout.item_list_tweet_improve, parent, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Tweet item, int position) {
        SoftwareTweetViewHolder vh = (SoftwareTweetViewHolder) holder;

        vh.icon.setTag(R.id.iv_tweet_face, position);
        final Author author = item.getAuthor();
        vh.mIdentityView.setup(author);
        if (author == null) {
            vh.icon.setup(0, "匿名用户", "");
            vh.name.setText("匿名用户");
        } else {
            vh.icon.setup(author);
            vh.icon.setOnClickListener(this);
            vh.name.setText(author.getName());
        }

        CommentsUtil.formatHtml(mContext.getResources(), vh.content, item.getContent());
        vh.pubTime.setText(StringUtils.formatSomeAgo(item.getPubDate()));
        PlatfromUtil.setPlatFromString(vh.deviceType, item.getAppClient());
        boolean liked = item.isLiked();
        if (liked) {
            vh.likeStatus.setImageResource(R.mipmap.ic_thumbup_actived);
        } else {
            vh.likeStatus.setImageResource(R.mipmap.ic_thumb_normal);
        }
        vh.likeStatus.setTag(position);
        vh.likeStatus.setOnClickListener(this);
        vh.likeCount.setText(String.format("%s", item.getLikeCount()));
        vh.commentCount.setText(String.format("%s", item.getCommentCount()));

    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.iv_tweet_face:
                int p = (int) v.getTag(R.id.iv_tweet_face);
                final Tweet item = getItem(p);
                OtherUserHomeActivity.show(mContext, item.getAuthor());
                break;
            case R.id.iv_like_state:
                int position = (int) v.getTag();
                final Tweet tempItem = getItem(position);
                requestEventDispatcher(tempItem);
                break;
            default:
                break;
        }
    }

    /**
     *
     */
    private void requestEventDispatcher(final Tweet item) {

        if (!AccountHelper.isLogin()) {
            UIHelper.showLoginActivity(mContext);
            return;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return;
        }

        OSChinaApi.pubSoftwareLike(item.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(mContext, "操作失败...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<TweetLikeReverse>>() {
                    }.getType();
                    ResultBean<TweetLikeReverse> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.getCode() == 1) {
                        TweetLikeReverse result = resultBean.getResult();
                        boolean like = result.isLiked();
                        item.setLiked(like);
                        int likeCount = item.getLikeCount();
                        item.setLikeCount((!item.isLiked() ? likeCount - 1 : likeCount + 1));
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

    static class SoftwareTweetViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.identityView)
        IdentityView mIdentityView;
        @Bind(R.id.iv_tweet_face)
        PortraitView icon;
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

        SoftwareTweetViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
