package net.oschina.app.adapter;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Tweet;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.ui.ImagePreviewActivity;
import net.oschina.app.ui.dialog.CommonDialog;
import net.oschina.app.ui.dialog.DialogHelper;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.KJAnimations;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;

import org.apache.http.Header;
import org.kymjs.kjframe.KJBitmap;
import org.kymjs.kjframe.bitmap.BitmapCallBack;
import org.kymjs.kjframe.bitmap.BitmapHelper;
import org.kymjs.kjframe.utils.DensityUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * @author HuangWenwei
 * @author kymjs
 * @date 2014年10月10日
 */
public class TweetAdapter extends ListBaseAdapter<Tweet> {

    static class ViewHolder {
        @InjectView(R.id.tv_tweet_name)
        TextView author;
        @InjectView(R.id.tv_tweet_time)
        TextView time;
        @InjectView(R.id.tweet_item)
        TweetTextView content;
        @InjectView(R.id.tv_tweet_comment_count)
        TextView commentcount;
        @InjectView(R.id.tv_tweet_platform)
        TextView platform;
        @InjectView(R.id.iv_tweet_face)
        AvatarView face;
        @InjectView(R.id.iv_tweet_image)
        ImageView image;
        @InjectView(R.id.iv_like_state)
        ImageView likeState;
        @InjectView(R.id.tv_del)
        TextView del;
        @InjectView(R.id.tv_likeusers)
        TextView likeUsers;
        @InjectView(R.id.ll_like)
        View likeOption;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private Bitmap recordBitmap;
    private Context context;
    private final KJBitmap kjb = new KJBitmap();

    final private AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {}

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                Throwable arg3) {}
    };

    private void initRecordImg(Context cxt) {
        recordBitmap = BitmapFactory.decodeResource(cxt.getResources(),
                R.drawable.audio3);
        recordBitmap = ImageUtils.zoomBitmap(recordBitmap,
                DensityUtils.dip2px(cxt, 20f), DensityUtils.dip2px(cxt, 20f));
    }

    @Override
    protected View getRealView(final int position, View convertView,
            final ViewGroup parent) {
        context = parent.getContext();
        final ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_tweet, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Tweet tweet = mDatas.get(position);

        if (tweet.getAuthorid() == AppContext.getInstance().getLoginUid()) {
            vh.del.setVisibility(View.VISIBLE);
            vh.del.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    optionDel(parent.getContext(), tweet, position);
                }
            });
        } else {
            vh.del.setVisibility(View.GONE);
        }

        vh.face.setUserInfo(tweet.getAuthorid(), tweet.getAuthor());
        vh.face.setAvatarUrl(tweet.getPortrait());
        vh.author.setText(tweet.getAuthor());
        vh.time.setText(StringUtils.friendly_time(tweet.getPubDate()));
        vh.content.setMovementMethod(MyLinkMovementMethod.a());
        vh.content.setFocusable(false);
        vh.content.setDispatchToParent(true);
        vh.content.setLongClickable(false);

        Spanned span = Html.fromHtml(tweet.getBody().trim());

        if (!StringUtils.isEmpty(tweet.getAttach())) {
            if (recordBitmap == null) {
                initRecordImg(parent.getContext());
            }
            ImageSpan recordImg = new ImageSpan(parent.getContext(),
                    recordBitmap);
            SpannableString str = new SpannableString("c");
            str.setSpan(recordImg, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            vh.content.setText(str);
            span = InputHelper.displayEmoji(context.getResources(), span);
            vh.content.append(span);
        } else {
            span = InputHelper.displayEmoji(context.getResources(), span);
            vh.content.setText(span);
        }
        MyURLSpan.parseLinkText(vh.content, span);

        vh.commentcount.setText(tweet.getCommentCount() + "");

        showTweetImage(vh, tweet.getImgSmall(), tweet.getImgBig(),
                parent.getContext());
        tweet.setLikeUsers(context, vh.likeUsers, true);
        final ViewHolder vh1 = vh;
        OnClickListener likeClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    if (tweet.getAuthorid() == AppContext.getInstance()
                            .getLoginUid()) {
                        AppContext.showToast("不能给自己点赞~");
                    } else {
                        updateLikeState(vh1, tweet);
                    }

                } else {
                    AppContext.showToast("先登陆再赞~");
                    UIHelper.showLoginActivity(parent.getContext());
                }
            }
        };
        if (tweet.getLikeUser() == null) {
            vh.likeOption.setVisibility(View.GONE);
        }

        vh.likeOption.setOnClickListener(likeClick);
        if (tweet.getIsLike() == 1) {
            vh.likeState.setBackgroundResource(R.drawable.ic_likeed);
        } else {
            vh.likeState.setBackgroundResource(R.drawable.ic_unlike);
        }
        vh.platform.setVisibility(View.VISIBLE);
        switch (tweet.getAppclient()) {
        case Tweet.CLIENT_MOBILE:
            vh.platform.setText(R.string.from_mobile);
            break;
        case Tweet.CLIENT_ANDROID:
            vh.platform.setText(R.string.from_android);
            break;
        case Tweet.CLIENT_IPHONE:
            vh.platform.setText(R.string.from_iphone);
            break;
        case Tweet.CLIENT_WINDOWS_PHONE:
            vh.platform.setText(R.string.from_windows_phone);
            break;
        case Tweet.CLIENT_WECHAT:
            vh.platform.setText(R.string.from_wechat);
            break;
        default:
            vh.platform.setText("");
            vh.platform.setVisibility(View.GONE);
            break;
        }
        return convertView;
    }

    private void updateLikeState(ViewHolder vh, Tweet tweet) {
        if (tweet.getIsLike() == 1) {
            tweet.setIsLike(0);
            tweet.setLikeCount(tweet.getLikeCount() - 1);
            if (!tweet.getLikeUser().isEmpty()) {
                tweet.getLikeUser().remove(0);
            }
            OSChinaApi.pubUnLikeTweet(tweet.getId(), tweet.getAuthorid(),
                    handler);
            vh.likeState.setBackgroundResource(R.drawable.ic_unlike);
        } else {
            tweet.setIsLike(1);
            vh.likeState
                    .setAnimation(KJAnimations.getScaleAnimation(1.5f, 300));
            tweet.getLikeUser().add(0, AppContext.getInstance().getLoginUser());
            OSChinaApi
                    .pubLikeTweet(tweet.getId(), tweet.getAuthorid(), handler);
            vh.likeState.setBackgroundResource(R.drawable.ic_likeed);
            tweet.setIsLike(1);
            tweet.setLikeCount(tweet.getLikeCount() + 1);
        }
        tweet.setLikeUsers(context, vh.likeUsers, true);
    }

    private void optionDel(Context context, final Tweet tweet,
            final int position) {

        CommonDialog dialog = DialogHelper
                .getPinterestDialogCancelable(context);
        dialog.setTitle("提示");
        dialog.setMessage("确定删除吗？");
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OSChinaApi.deleteTweet(tweet.getAuthorid(), tweet.getId(),
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int arg0, Header[] arg1,
                                    byte[] arg2) {
                                mDatas.remove(position);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(int arg0, Header[] arg1,
                                    byte[] arg2, Throwable arg3) {}
                        });
            }
        });
        dialog.setPositiveButton("取消", null);

        dialog.show();
    }

    /**
     * 动态设置动弹列表图片显示规则
     * 
     * @author kymjs
     */
    private void showTweetImage(final ViewHolder vh, String imgSmall,
            final String imgBig, final Context context) {
        if (imgSmall != null && !TextUtils.isEmpty(imgSmall)) {
            kjb.display(vh.image, imgSmall, new BitmapCallBack() {
                @Override
                public void onPreLoad() {
                    super.onPreLoad();
                    vh.image.setImageResource(R.drawable.pic_bg);
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    super.onSuccess(bitmap);
                    if (bitmap != null) {
                        bitmap = BitmapHelper.scaleWithXY(bitmap,
                                300 / bitmap.getHeight());
                        vh.image.setImageBitmap(bitmap);
                    }
                }
            });
            vh.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImagePreviewActivity.showImagePrivew(context, 0,
                            new String[] { imgBig });
                }
            });
            vh.image.setVisibility(AvatarView.VISIBLE);
        } else {
            vh.image.setVisibility(AvatarView.GONE);
        }
    }
}
