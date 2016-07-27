package net.oschina.app.improve.tweet.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.User;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.TweetLikeReverse;
import net.oschina.app.improve.media.ImageGalleryActivity;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.widget.FlowLayout;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.TweetTextView;
import net.qiujuer.genius.ui.Ui;

import org.kymjs.kjframe.utils.DensityUtils;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 动弹列表适配器
 * Created by huanghaibin_dev
 * on 2016/7/18.
 */
public class TweetAdapter extends BaseListAdapter<Tweet> {
    private Bitmap recordBitmap;
    private OnTweetLikeClickListener listener;
    private OnTweetImageClickListener imageClickListener;

    public TweetAdapter(Callback callback) {
        super(callback);
        initListener();
    }

    private void initListener() {
        imageClickListener = new OnTweetImageClickListener() {
            @Override
            public void onClick(View v, int position, int imagePosition) {
                String[] images = Tweet.Image.getImagePath(getItem(position).getImages());
                ImageGalleryActivity.show(mCallback.getContext(), images, imagePosition);
            }
        };

        listener = new OnTweetLikeClickListener() {
            @Override
            public void onClick(View v, int position) {
                OSChinaApi.reverseTweetLike(getItem(position).getId(), new TweetLikedHandler(position));
            }
        };

    }

    private void initRecordImg(Context cxt) {
        recordBitmap = BitmapFactory.decodeResource(cxt.getResources(),
                R.mipmap.audio3);
        recordBitmap = ImageUtils.zoomBitmap(recordBitmap,
                DensityUtils.dip2px(cxt, 20f), DensityUtils.dip2px(cxt, 20f));
    }

    @Override
    protected void convert(ViewHolder vh, final Tweet item, int position) {
        vh.setImageForNet(R.id.iv_tweet_face, item.getAuthor().getPortrait(), R.mipmap.widget_dface);
        CircleImageView iv_tweet_face = vh.getView(R.id.iv_tweet_face);
        iv_tweet_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Author author = item.getAuthor();
                User user = new User();
                user.setId((int) author.getId());
                user.setName(author.getName());
                user.setPortrait(author.getPortrait());
                OtherUserHomeActivity.show(mCallback.getContext(), user);
            }
        });

        vh.setText(R.id.tv_tweet_name, item.getAuthor().getName());
        vh.setText(R.id.tv_tweet_time, StringUtils.friendly_time(item.getPubDate()));
        PlatfromUtil.setPlatFromString((TextView) vh.getView(R.id.tv_tweet_platform), item.getAppClient());
        vh.setText(R.id.tv_tweet_like_count, String.valueOf(item.getLikeCount()));
        vh.setText(R.id.tv_tweet_comment_count, String.valueOf(item.getCommentCount()));

        TweetTextView tv_content = vh.getView(R.id.tweet_item);
        tv_content.setMovementMethod(MyLinkMovementMethod.a());
        tv_content.setFocusable(false);
        tv_content.setDispatchToParent(true);
        tv_content.setLongClickable(false);
        Spanned span = Html.fromHtml(item.getContent().trim());

        if (item.getAudio() != null) {
            if (recordBitmap == null) {
                initRecordImg(mCallback.getContext());
            }
            ImageSpan recordImg = new ImageSpan(mCallback.getContext(), recordBitmap);
            SpannableString str = new SpannableString("c");
            str.setSpan(recordImg, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_content.setText(str);
            span = InputHelper.displayEmoji(mCallback.getContext().getResources(), span);
            tv_content.append(span);
        } else {
            span = InputHelper.displayEmoji(mCallback.getContext().getResources(), span);
            tv_content.setText(span);
        }

        ImageView iv_tweet_like = vh.getView(R.id.iv_like_state);
        iv_tweet_like.setImageResource(item.isLiked() ? R.mipmap.ic_thumbup_actived : R.mipmap.ic_thumbup_normal);
        iv_tweet_like.setTag(position);
        iv_tweet_like.setOnClickListener(listener);

        Tweet.Image[] images = item.getImages();
        FlowLayout flowLayout = vh.getView(R.id.fl_image);
        flowLayout.removeAllViews();
        if (images != null && images.length > 0) {
            flowLayout.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) Ui.dipToPx(mCallback.getContext().getResources(), 64)
                    , (int) Ui.dipToPx(mCallback.getContext().getResources(), 64));
            for (int i = 0; i < images.length; i++) {
                ImageView imageView = new ImageView(mCallback.getContext());
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setTag(R.id.iv_tweet_image, i);
                imageView.setTag(R.id.iv_tweet_face, position);
                imageView.setOnClickListener(imageClickListener);
                vh.setImageForNet(imageView, images[i].getThumb(), R.color.grey_200, R.mipmap.ic_default_image);
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

    private abstract class OnTweetLikeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onClick(v, Integer.parseInt(v.getTag().toString()));
        }

        public abstract void onClick(View v, int position);
    }

    private abstract class OnTweetImageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onClick(v, Integer.parseInt(v.getTag(R.id.iv_tweet_face).toString()),
                    Integer.parseInt(v.getTag(R.id.iv_tweet_image).toString()));
        }

        public abstract void onClick(View v, int position, int imagePosition);
    }

    //点赞回调
    private class TweetLikedHandler extends TextHttpResponseHandler {
        private int position;

        public TweetLikedHandler(int position) {
            this.position = position;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                Type type = new TypeToken<ResultBean<TweetLikeReverse>>() {
                }.getType();
                ResultBean<TweetLikeReverse> resultBean = AppContext.createGson().fromJson(responseString, type);
                Tweet tweet = getItem(position);
                tweet.setLiked(resultBean.getResult().isLiked());
                tweet.setLikeCount(resultBean.getResult().getLikeCount());
                updateItem(position, tweet);
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    }
}
