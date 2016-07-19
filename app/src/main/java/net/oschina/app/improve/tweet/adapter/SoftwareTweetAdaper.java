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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.SoftwareTweetLike;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.TweetTextView;

import org.kymjs.kjframe.utils.DensityUtils;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fei on 2016/7/19.
 * desc:
 */

public class SoftwareTweetAdaper extends BaseListAdapter<Tweet> implements View.OnClickListener {

    private Bitmap recordBitmap;


    public SoftwareTweetAdaper(Callback callback) {
        super(callback);
    }

    private void initRecordImg(Context cxt) {
        recordBitmap = BitmapFactory.decodeResource(cxt.getResources(),
                R.drawable.audio3);
        recordBitmap = ImageUtils.zoomBitmap(recordBitmap,
                DensityUtils.dip2px(cxt, 20f), DensityUtils.dip2px(cxt, 20f));
    }


    @Override
    protected void convert(ViewHolder vh, Tweet item, int position) {

        vh.setImageForNet(R.id.iv_tweet_face, item.getAuthor().getPortrait());
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
        ImageView ivLike = vh.getView(R.id.iv_like_state);
        ivLike.setTag(position);

        ivLike.setOnClickListener(this);
        if (item.isLiked()) {
            ivLike.setImageResource(R.drawable.ic_thumbup_actived);
        } else {
            ivLike.setImageResource(R.drawable.ic_thumbup_normal);
        }

    }

    @Override
    protected int getLayoutId(int position, Tweet item) {
        return R.layout.item_list_tweet_improve;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();

        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(mCallback.getContext());
            return;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return;
        }
        final Tweet item = getItem(position);
        OSChinaApi.pubSoftwareLike(item.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(mCallback.getContext(), "点赞失败...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
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
                        Toast.makeText(mCallback.getContext(), "点赞失败...", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

}
