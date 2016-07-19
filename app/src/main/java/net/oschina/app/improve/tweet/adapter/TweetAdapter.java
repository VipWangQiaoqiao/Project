package net.oschina.app.improve.tweet.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.widget.FlowLayout;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;

/**
 * 动弹列表适配器
 * Created by huanghaibin_dev
 * on 2016/7/18.
 */
public class TweetAdapter extends BaseListAdapter<Tweet> {
    public TweetAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Tweet item, int position) {
        vh.setImageForNet(R.id.iv_tweet_face, item.getAuthor().getPortrait());
        vh.setText(R.id.tv_tweet_name, item.getAuthor().getName());
        vh.setText(R.id.tweet_item, item.getContent());
        vh.setText(R.id.tv_tweet_time, StringUtils.friendly_time(item.getPubDate()));
        PlatfromUtil.setPlatFromString((TextView) vh.getView(R.id.tv_tweet_platform), item.getAppClient());
        vh.setText(R.id.tv_tweet_like_count, String.valueOf(item.getLikeCount()));
        vh.setText(R.id.tv_tweet_comment_count, String.valueOf(item.getCommentCount()));
        Tweet.Image[] images = item.getImages();
        FlowLayout flowLayout = (FlowLayout) vh.getView(R.id.fl_image);
        flowLayout.removeAllViews();
        if (images != null && images.length > 0) {
            flowLayout.setVisibility(View.VISIBLE);
            for (Tweet.Image image : images) {
                ImageView imageView = new ImageView(mCallback.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(80, 80);
                imageView.setLayoutParams(layoutParams);
                vh.setImageForNet(imageView, image.getThumb(), R.drawable.ic_default_image);
                flowLayout.addView(imageView);
            }
        } else {
            flowLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutId(int position, Tweet item) {
        return R.layout.item_list_tweet_improve;
    }
}
