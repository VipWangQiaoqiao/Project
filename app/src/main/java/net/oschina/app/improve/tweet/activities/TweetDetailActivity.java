package net.oschina.app.improve.tweet.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetComment;
import net.oschina.app.improve.bean.simple.TweetLike;
import net.oschina.app.improve.behavior.FloatingAutoHideDownBehavior;
import net.oschina.app.improve.behavior.KeyboardInputDelegation;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.improve.widget.TweetPicturesLayout;
import net.oschina.app.ui.ShareDialog;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.viewpagerfragment.TweetDetailViewPagerFragment;
import net.oschina.app.widget.CircleImageView;
import net.oschina.app.widget.RecordButtonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 动弹详情
 * Created by thanatos
 * on 16/6/13.
 */
@SuppressWarnings("deprecation")
public class TweetDetailActivity extends BaseBackActivity implements TweetDetailContract.Operator {

    public static final String BUNDLE_KEY_TWEET = "BUNDLE_KEY_TWEET";
    public static final String BUNDLE_KEY_TWEET_ID = "BUNDLE_KEY_TWEET_ID";

    @Bind(R.id.iv_portrait)
    CircleImageView ivPortrait;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_client)
    TextView tvClient;
    @Bind(R.id.iv_thumbup)
    ImageView ivThumbup;
    @Bind(R.id.layout_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.fragment_container)
    FrameLayout mFrameLayout;
    @Bind(R.id.tweet_img_record)
    ImageView mImgRecord;
    @Bind(R.id.tweet_tv_record)
    TextView mSecondRecord;
    @Bind(R.id.tweet_bg_record)
    RelativeLayout mRecordLayout;
    @Bind(R.id.tv_content)
    TextView mContent;
    @Bind(R.id.tweet_pics_layout)
    TweetPicturesLayout mLayoutGrid;

    EditText mViewInput;

    private Tweet tweet;
    private List<TweetComment> replies = new ArrayList<>();
    private Dialog dialog;
    private ShareDialog mShareDialog;
    private RecordButtonUtil mRecordUtil;
    private TextHttpResponseHandler publishAdmireHandler;
    private TextHttpResponseHandler publishCommentHandler;

    private TweetDetailContract.ICmnView mCmnViewImp;
    private TweetDetailContract.IThumbupView mThumbupViewImp;
    private TweetDetailContract.IAgencyView mAgencyViewImp;

    private KeyboardInputDelegation mDelegation;
    private View.OnClickListener onPortraitClickListener;

    public static void show(Context context, Tweet tweet) {
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_TWEET, tweet);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        Tweet tweet = new Tweet();
        tweet.setId(id);
        show(context, tweet);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_tweet_detail;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        tweet = (Tweet) getIntent().getSerializableExtra(BUNDLE_KEY_TWEET);
        if (tweet == null) {
            Toast.makeText(this, "对象没找到", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.initBundle(bundle);
    }

    protected void initData() {
        // admire tweet
        publishAdmireHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(TweetDetailActivity.this, ivThumbup.isSelected() ? "取消失败" : "点赞失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<TweetLike> result = AppContext.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<TweetLike>>() {
                        }.getType());
                if (result != null && result.isSuccess()) {
                    ivThumbup.setSelected(result.getResult().isLiked());
                    mThumbupViewImp.onLikeSuccess(result.getResult().isLiked(), null);
                } else {
                    onFailure(statusCode, headers, responseString, null);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dismissDialog();
            }
        };

        // publish tweet comment
        publishCommentHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(TweetDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mCmnViewImp.onCommentSuccess(null);
                replies.clear(); // 清除
                mViewInput.setHint("发表评论");
                mViewInput.setText(null);
                dismissDialog();
                TDevice.hideSoftKeyboard(mDelegation.getInputView());
            }
        };

        OSChinaApi.getTweetDetail(tweet.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(TweetDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<Tweet> result = AppContext.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<Tweet>>() {
                        }.getType());
                if (result.isSuccess()) {
                    if (result.getResult() == null) {
                        AppContext.showToast(R.string.tweet_detail_data_null);
                        finish();
                        return;
                    }
                    tweet = result.getResult();
                    mAgencyViewImp.resetCmnCount(tweet.getCommentCount());
                    mAgencyViewImp.resetLikeCount(tweet.getLikeCount());
                    fillDetailView();
                } else {
                    onFailure(500, headers, "妈的智障", null);
                }
            }
        });

    }

    protected void initWidget() {
        mDelegation = KeyboardInputDelegation.delegation(this, mCoordinatorLayout, mFrameLayout);
        mDelegation.setBehavior(new FloatingAutoHideDownBehavior());
        mDelegation.showEmoji(getSupportFragmentManager());
        mDelegation.setAdapter(new KeyboardInputDelegation.KeyboardInputAdapter() {
            @Override
            public void onSubmit(TextView v, String content) {
                content = content.replaceAll("[\\s\\n]+", " ");
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(TweetDetailActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AppContext.getInstance().isLogin()) {
                    UIHelper.showLoginActivity(TweetDetailActivity.this);
                    return;
                }
                if (replies != null && replies.size() > 0)
                    content = mViewInput.getHint() + ": " + content;
                dialog = DialogHelp.getWaitDialog(TweetDetailActivity.this, "正在发表评论...");
                dialog.show();
                OSChinaApi.pubTweetComment(tweet.getId(), content, 0, publishCommentHandler);
            }

            @Override
            public void onFinalBackSpace(View v) {
                if (replies == null || replies.size() == 0) return;
                replies.remove(replies.size() - 1);
                if (replies.size() == 0){
                    mViewInput.setHint("发表评论");
                    return;
                }
                mViewInput.setHint("回复: @" + replies.get(0).getAuthor().getName());
                if (replies.size() == 2){
                    mViewInput.setHint(mViewInput.getHint() + " @" + replies.get(1).getAuthor().getName());
                }
            }
        });
        mViewInput = mDelegation.getInputView();

        // TODO to select friends when input @ character
        resolveVoice();
        fillDetailView();

        TweetDetailViewPagerFragment mPagerFrag = TweetDetailViewPagerFragment.instantiate(this);
        mCmnViewImp = mPagerFrag.getCommentViewHandler();
        mThumbupViewImp = mPagerFrag.getThumbupViewHandler();
        mAgencyViewImp = mPagerFrag.getAgencyViewHandler();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mPagerFrag)
                .commit();
    }

    private void resolveVoice() {
        if (tweet == null || tweet.getAudio() == null || tweet.getAudio().length == 0) return;
        mRecordLayout.setVisibility(View.VISIBLE);
        final AnimationDrawable drawable = (AnimationDrawable) mImgRecord.getBackground();
        mRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet == null) return;
                getRecordUtil().startPlay(tweet.getAudio()[0].getHref(), mSecondRecord);
            }
        });
        getRecordUtil().setOnPlayListener(new RecordButtonUtil.OnPlayListener() {
            @Override
            public void stopPlay() {
                drawable.stop();
                mImgRecord.setBackgroundDrawable(drawable.getFrame(0));
            }

            @Override
            public void starPlay() {
                drawable.start();
                mImgRecord.setBackgroundDrawable(drawable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_share:
                if (tweet == null || tweet.getId() <= 0) break;
                if (mShareDialog == null) {
                    mShareDialog = new ShareDialog(this);
                }
                mShareDialog.setCancelable(true);
                mShareDialog.setCanceledOnTouchOutside(true);
                mShareDialog.setTitle(R.string.share_to);
                mShareDialog.setShareInfo(tweet.getAuthor().getName() + "的动弹 - 开源中国社区",
                        tweet.getContent(), tweet.getHref());
                mShareDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private RecordButtonUtil getRecordUtil() {
        if (mRecordUtil == null) {
            mRecordUtil = new RecordButtonUtil();
        }
        return mRecordUtil;
    }

    private void dismissDialog() {
        if (dialog == null) return;
        dialog.dismiss();
        dialog = null;
    }

    private void fillDetailView() {
        // 有可能传入的tweet只有id这一个值
        if (tweet == null || isDestroy())
            return;
        if (tweet.getAuthor() != null) {
            if (TextUtils.isEmpty(tweet.getAuthor().getPortrait())) {
                ivPortrait.setImageResource(R.mipmap.widget_dface);
            } else {
                getImageLoader()
                        .load(tweet.getAuthor().getPortrait())
                        .asBitmap()
                        .placeholder(getResources().getDrawable(R.mipmap.widget_dface))
                        .error(getResources().getDrawable(R.mipmap.widget_dface))
                        .into(ivPortrait);
            }
            ivPortrait.setOnClickListener(getOnPortraitClickListener());
            tvNick.setText(tweet.getAuthor().getName());
        }
        if (!TextUtils.isEmpty(tweet.getPubDate()))
            tvTime.setText(StringUtils.formatSomeAgo(tweet.getPubDate()));
        PlatfromUtil.setPlatFromString(tvClient, tweet.getAppClient());
        if (tweet.isLiked()) {
            ivThumbup.setSelected(true);
        } else {
            ivThumbup.setSelected(false);
        }
        if (!TextUtils.isEmpty(tweet.getContent())) {
            String content = tweet.getContent().replaceAll("[\n\\s]+", " ");
            Spannable spannable = AssimilateUtils.assimilateOnlyAtUser(this, content);
            spannable = AssimilateUtils.assimilateOnlyTag(this, spannable);
            spannable = AssimilateUtils.assimilateOnlyLink(this, spannable);
            spannable = InputHelper.displayEmoji(getResources(), spannable);
            mContent.setText(spannable);
            mContent.setMovementMethod(LinkMovementMethod.getInstance());
        }

        mLayoutGrid.setImage(tweet.getImages());
    }

    private View.OnClickListener getOnPortraitClickListener() {
        if (onPortraitClickListener == null) {
            onPortraitClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showUserCenter(TweetDetailActivity.this, tweet.getAuthor().getId(), tweet.getAuthor().getName());
                }
            };
        }
        return onPortraitClickListener;
    }

    @Override
    public Tweet getTweetDetail() {
        return tweet;
    }

    @Override
    public void toReply(TweetComment comment) {
        mDelegation.notifyWrapper();
        if (replies.size() >= 3) return;
        for (TweetComment cmm : replies){
            if (cmm.getAuthor().getId() == comment.getAuthor().getId()) return;
        }
        if (replies.size() == 0) {
            mViewInput.setHint("回复 @" + comment.getAuthor().getName());
        }else {
            mViewInput.setHint(mViewInput.getHint() + " @" + comment.getAuthor().getName());
        }
        this.replies.add(comment);
        TDevice.showSoftKeyboard(mViewInput);
    }

    @Override
    public void onScroll() {
        if (mDelegation != null) mDelegation.onTurnBack();
    }

    @OnClick(R.id.iv_thumbup)
    void onClickThumbUp() {
        this.dialog = DialogHelp.getWaitDialog(this, "正在提交请求...");
        this.dialog.show();
        OSChinaApi.reverseTweetLike(tweet.getId(), publishAdmireHandler);
    }

    @OnClick(R.id.iv_comment)
    void onClickComment() {
        mDelegation.notifyWrapper();
        TDevice.showSoftKeyboard(mViewInput);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (!mDelegation.onTurnBack()) return true;
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
