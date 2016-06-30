package net.oschina.app.improve.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetDetail;
import net.oschina.app.improve.behavior.KeyboardInputDelegation;
import net.oschina.app.improve.detail.contract.TweetDetailContract;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.viewpagerfragment.TweetDetailViewPagerFragment;
import net.oschina.app.widget.CircleImageView;
import net.oschina.app.widget.RecordButtonUtil;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 动弹详情
 * Created by thanatos on 16/6/13.
 */
public class TweetDetailActivity extends BaseBackActivity implements TweetDetailContract.Operator {

    public static final String BUNDLE_KEY_TWEET = "BUNDLE_KEY_TWEET";


    @Bind(R.id.iv_portrait)
    CircleImageView ivPortrait;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.webview)
    OWebView mWebview;
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

    EditText mViewInput;

    private Tweet tweet;
    private Comment reply;
    private Dialog dialog;
    private RecordButtonUtil mRecordUtil;
    private AsyncHttpResponseHandler publishAdmireHandler;
    private AsyncHttpResponseHandler publishCommentHandler;

    private TweetDetailContract.ICmnView mCmnViewImp;
    private TweetDetailContract.IThumbupView mThumbupViewImp;
    private TweetDetailContract.IAgencyView mAgencyViewImp;

    private KeyboardInputDelegation mDelegation;
    private View.OnClickListener onPortraitClickListener;

    public static void show(Context context, Tweet tweet) {
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_TWEET, (Serializable) tweet);
        context.startActivity(intent);
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
        publishAdmireHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ivThumbup.setSelected(!ivThumbup.isSelected());
                mThumbupViewImp.onLikeSuccess(ivThumbup.isSelected(), null);
                dismissDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(TweetDetailActivity.this, ivThumbup.isSelected() ? "取消失败" : "点赞失败", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        };

        publishCommentHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mCmnViewImp.onCommentSuccess(null);
                reply = null; // 清除
                mViewInput.setHint("发表评论");
                mViewInput.setText(null);
                dismissDialog();
                TDevice.hideSoftKeyboard(mDelegation.getInputView());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(TweetDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        };

        OSChinaApi.getTweetDetail(tweet.getId(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                TweetDetail data = XmlUtils.toBean(TweetDetail.class, responseBody);
                if (data == null || data.getTweet() == null) {
                    AppContext.showToast(R.string.tweet_detail_data_null);
                    finish();
                    return;
                }
                tweet = data.getTweet();
                mAgencyViewImp.resetCmnCount(tweet.getCommentCount() != null ? Integer.valueOf(tweet.getCommentCount()) : 0);
                mAgencyViewImp.resetLikeCount(tweet.getLikeCount());
                fillDetailView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(TweetDetailActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void initWidget() {
        mDelegation = KeyboardInputDelegation.delegation(this, mCoordinatorLayout, mFrameLayout);
        mDelegation.showEmoji(getSupportFragmentManager());
        mDelegation.setAdapter(new KeyboardInputDelegation.KeyboardInputAdapter() {
            @Override
            public void onSubmit(TextView v, String content) {
                if (TextUtils.isEmpty(content.replaceAll("[ \\s\\n]+", ""))) {
                    Toast.makeText(TweetDetailActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AppContext.getInstance().isLogin()) {
                    UIHelper.showLoginActivity(TweetDetailActivity.this);
                    return;
                }

                TweetDetailActivity.this.dialog = DialogHelp.getWaitDialog(TweetDetailActivity.this, "正在发表评论...");
                TweetDetailActivity.this.dialog.show();

                if (TweetDetailActivity.this.reply == null) {
                    OSChinaApi.publicComment(3, tweet.getId(), AppContext.getInstance().getLoginUid(),
                            v.getText().toString(), 1, publishCommentHandler);
                } else {
                    OSChinaApi.replyComment(tweet.getId(), 3, reply.getId(), reply.getAuthorId(),
                            AppContext.getInstance().getLoginUid(), v.getText().toString(), publishCommentHandler);
                }
            }

            @Override
            public void onFinalBackSpace(View v) {
                if (reply == null) return;
                reply = null;
                mViewInput.setHint("发表评论");
            }
        });
        mViewInput = mDelegation.getInputView();

        // TODO to select friends when input @ character

        resolveVoice();

        fillDetailView();

        TweetDetailViewPagerFragment mTweetDetailViewPagerFrag = TweetDetailViewPagerFragment.instantiate(this);
        mCmnViewImp = mTweetDetailViewPagerFrag;
        mThumbupViewImp = mTweetDetailViewPagerFrag;
        mAgencyViewImp = mTweetDetailViewPagerFrag;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mTweetDetailViewPagerFrag)
                .commit();
    }

    private void resolveVoice() {
        if (TextUtils.isEmpty(tweet.getAttach())) return;
        mRecordLayout.setVisibility(View.VISIBLE);
        final AnimationDrawable drawable = (AnimationDrawable) mImgRecord.getBackground();
        mRecordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet == null) return;
                getRecordUtil().startPlay(tweet.getAttach(), mSecondRecord);
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

    private RecordButtonUtil getRecordUtil() {
        if (mRecordUtil == null) {
            mRecordUtil = new RecordButtonUtil();
        }
        return mRecordUtil;
    }

    private void dismissDialog(){
        if (dialog == null) return;
        dialog.dismiss();
        dialog = null;
    }

    private void fillDetailView() {
        // 有可能穿入的tweet只有id这一个值
        if (isDestroy())
            return;
        if (TextUtils.isEmpty(tweet.getPortrait())) {
            ivPortrait.setImageResource(R.drawable.widget_dface);
        } else {
            getImageLoader()
                    .load(tweet.getPortrait())
                    .asBitmap()
                    .placeholder(getResources().getDrawable(R.drawable.widget_dface))
                    .error(getResources().getDrawable(R.drawable.widget_dface))
                    .into(ivPortrait);
        }
        ivPortrait.setOnClickListener(getOnPortraitClickListener());
        tvNick.setText(tweet.getAuthor());
        if (!TextUtils.isEmpty(tweet.getPubDate()))
            tvTime.setText(StringUtils.friendly_time(tweet.getPubDate()));
        PlatfromUtil.setPlatFromString(tvClient, tweet.getAppclient());
        if (tweet.getIsLike() == 1) {
            ivThumbup.setSelected(true);
        } else {
            ivThumbup.setSelected(false);
        }

        fillWebViewBody();
    }

    /**
     * 填充webview内容
     */
    private void fillWebViewBody() {
        if (TextUtils.isEmpty(tweet.getBody())) return;
        String html = tweet.getBody() + "<br/><img src=\"" + tweet.getImgSmall() + "\" data-url=\"" + tweet.getImgBig() + "\"/>";
        mWebview.loadTweetDataAsync(html, null);
    }

    private View.OnClickListener getOnPortraitClickListener() {
        if (onPortraitClickListener == null) {
            onPortraitClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showUserCenter(TweetDetailActivity.this, tweet.getAuthorid(), tweet.getAuthor());
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
    public void toReply(Comment comment) {
        mDelegation.notifyWrapper();
        this.reply = comment;
        mViewInput.setHint("回复@ " + comment.getAuthor());
        TDevice.showSoftKeyboard(mViewInput);
    }

    @Override
    public void onScroll() {
        if (mDelegation != null) mDelegation.onTurnBack();
    }

    @OnClick(R.id.iv_thumbup) void onClickThumbUp() {
        this.dialog = DialogHelp.getWaitDialog(this, "正在提交请求...");
        this.dialog.show();
        if (!ivThumbup.isSelected()) {
            OSChinaApi.pubLikeTweet(tweet.getId(), tweet.getAuthorid(), publishAdmireHandler);
        } else {
            OSChinaApi.pubUnLikeTweet(tweet.getId(), tweet.getAuthorid(), publishAdmireHandler);
        }
    }

    @OnClick(R.id.iv_comment) void onClickComment(){
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
