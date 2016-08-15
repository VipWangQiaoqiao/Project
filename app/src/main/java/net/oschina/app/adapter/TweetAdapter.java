package net.oschina.app.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.User;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.ui.OSCPhotosActivity;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.ImageUtils;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.TweetTextView;

import org.kymjs.kjframe.Core;
import org.kymjs.kjframe.utils.DensityUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TweetAdapter extends ListBaseAdapter<Tweet> {

    static class ViewHolder {
        @Bind(R.id.tv_tweet_name)
        TextView author;
        @Bind(R.id.tv_tweet_time)
        TextView time;
        @Bind(R.id.tweet_item)
        TweetTextView content;
        @Bind(R.id.tv_tweet_comment_count)
        TextView commentcount;
        @Bind(R.id.tv_tweet_platform)
        TextView platform;
        @Bind(R.id.iv_tweet_face)
        AvatarView face;
        @Bind(R.id.iv_tweet_image)
        ImageView image;
        @Bind(R.id.iv_like_state)
        ImageView ivLikeState;
//        @Bind(R.id.tv_del)
//        TextView del;
        @Bind(R.id.tv_likeusers)
        TextView likeUsers;
        @Bind(R.id.tv_tweet_like_count)
        TextView tv_tweet_like_count;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private Bitmap recordBitmap;
    private Context context;

    final private AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
        }
    };

    private void initRecordImg(Context cxt) {
        recordBitmap = BitmapFactory.decodeResource(cxt.getResources(),
                R.mipmap.audio3);
        recordBitmap = ImageUtils.zoomBitmap(recordBitmap,
                DensityUtils.dip2px(cxt, 20f), DensityUtils.dip2px(cxt, 20f));
    }

    @Override
    protected View getRealView(final int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        final ViewHolder vh;
        if (convertView == null || convertView.getTag() == null) {
            convertView = View.inflate(context, R.layout.item_list_tweet, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Tweet tweet = mDatas.get(position);

//        if (tweet.getAuthorid() == AppContext.getInstance().getLoginUid()) {
//            vh.del.setVisibility(View.VISIBLE);
//            vh.del.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    optionDel(context, tweet, position);
//                }
//            });
//        } else {
//            vh.del.setVisibility(View.GONE);
//        }

        vh.face.setUserInfo(tweet.getAuthorid(), tweet.getAuthor());
        vh.face.setAvatarUrl(tweet.getPortrait());
        vh.author.setText(tweet.getAuthor());
        vh.time.setText(StringUtils.formatSomeAgo(tweet.getPubDate()));
        vh.tv_tweet_like_count.setText(String.valueOf(tweet.getLikeCount()));

        vh.content.setMovementMethod(LinkMovementMethod.getInstance());
        vh.content.setFocusable(false);
        vh.content.setDispatchToParent(true);
        vh.content.setLongClickable(false);
        Spannable spannable = AssimilateUtils.assimilateOnlyLink(context, tweet.getBody());
        spannable = AssimilateUtils.assimilateOnlyAtUser(context, spannable);
        spannable = AssimilateUtils.assimilateOnlyTag(context, spannable);
        spannable = InputHelper.displayEmoji(context.getResources(), spannable);

        if (!StringUtils.isEmpty(tweet.getAttach())) {
            if (recordBitmap == null) {
                initRecordImg(context);
            }
            ImageSpan recordImg = new ImageSpan(context, recordBitmap);
            SpannableString str = new SpannableString("c");
            str.setSpan(recordImg, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            vh.content.setText(str);
            vh.content.append(spannable);
        } else {
            vh.content.setText(spannable);
        }

        vh.commentcount.setText(tweet.getCommentCount());

        if (TextUtils.isEmpty(tweet.getImgSmall())) {
            vh.image.setVisibility(View.GONE);
        } else {
            vh.image.setVisibility(View.VISIBLE);
            new Core.Builder().view(vh.image).size(300, 300).url(tweet.getImgSmall() + "?300X300")
                    .loadBitmapRes(R.drawable.pic_bg).doTask();
            vh.image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    OSCPhotosActivity.showImagePreview(context, tweet.getImgBig());
                }
            });
        }

        tweet.setLikeUsers(context, vh.likeUsers, true);

        vh.ivLikeState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.getInstance().isLogin()) {
                    updateLikeState(vh, tweet);
                } else {
                    AppContext.showToast("先登陆再赞~");
                    UIHelper.showLoginActivity(context);
                }
            }
        });

//        TypefaceUtils.setTypeface(vh.tvLikeState);
        if (tweet.getIsLike() == 1) {
            vh.ivLikeState.setImageResource(R.mipmap.ic_thumbup_actived);
        } else {
            vh.ivLikeState.setImageResource(R.mipmap.ic_thumbup_normal);
        }
        PlatfromUtil.setPlatFromString(vh.platform, tweet.getAppclient());
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
//            vh.ivLikeState.setTextColor(AppContext.getInstance().getResources().getColor(R.color
//                    .gray));
            vh.ivLikeState.setImageResource(R.mipmap.ic_thumbup_normal);
        } else {
            //vh.tvLikeState.setAnimation(KJAnimations.getScaleAnimation(1.5f, 300));
            List<User> likeUser = tweet.getLikeUser();
            if (likeUser!=null)
            tweet.getLikeUser().add(0, AppContext.getInstance().getLoginUser());
            OSChinaApi.pubLikeTweet(tweet.getId(), tweet.getAuthorid(), handler);
//            vh.tvLikeState.setTextColor(AppContext.getInstance().getResources().getColor(R.color
//                    .day_colorPrimary));
            vh.ivLikeState.setImageResource(R.mipmap.ic_thumbup_actived);
            tweet.setIsLike(1);
            tweet.setLikeCount(tweet.getLikeCount() + 1);
        }
        vh.tv_tweet_like_count.setText(String.valueOf(tweet.getLikeCount()));
        tweet.setLikeUsers(context, vh.likeUsers, true);
    }

    @SuppressWarnings("unused")
    private void optionDel(Context context, final Tweet tweet, final int position) {

        DialogHelp.getConfirmDialog(context, "确定删除吗?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
                                                  byte[] arg2, Throwable arg3) {
                            }
                        });
            }
        }).show();
    }
}
