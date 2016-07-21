package net.oschina.app.improve.tweet.activities;

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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Comment;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.behavior.KeyboardInputDelegation;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.ui.OSCPhotosActivity;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.viewpagerfragment.TweetDetailViewPagerFragment;
import net.oschina.app.widget.CircleImageView;
import net.oschina.app.widget.RecordButtonUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 动弹详情
 * Created by thanatos
 * on 16/6/13.
 */
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
    @Bind(R.id.layout_grid)
    GridLayout mLayoutGrid;

    EditText mViewInput;

    private Tweet tweet;
    private long tid;
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

    @Deprecated
    public static void show(Context context, net.oschina.app.bean.Tweet tweet) {
        // TODO 全部使用新接口后删除此方法
        Toast.makeText(context, "请用新的接口", Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, Tweet tweet){
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_TWEET, tweet);
        context.startActivity(intent);
    }

    public static void show(Context context, long id){
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_TWEET_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_tweet_detail;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        tweet = (Tweet) getIntent().getSerializableExtra(BUNDLE_KEY_TWEET);
        tid = getIntent().getLongExtra(BUNDLE_KEY_TWEET_ID, 0);
        if (tweet == null && tid == 0) {
            Toast.makeText(this, "对象没找到", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.initBundle(bundle);
    }

    protected void initData() {
        // TODO 请使用新接口
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

        // TODO 请使用新接口
        /*publishCommentHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(TweetDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mCmnViewImp.onCommentSuccess(null);
                reply = null; // 清除
                mViewInput.setHint("发表评论");
                mViewInput.setText(null);
                dismissDialog();
                TDevice.hideSoftKeyboard(mDelegation.getInputView());
            }
        };*/

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

        if (tweet == null && tid != 0){
            OSChinaApi.getTweetDetail(tid, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(TweetDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    ResultBean<Tweet> result = AppContext.createGson().fromJson(
                            responseString, new TypeToken<ResultBean<Tweet>>(){}.getType());
                    if (result.isSuccess()){
                        if (result.getResult() == null){
                            AppContext.showToast(R.string.tweet_detail_data_null);
                            finish();
                            return;
                        }
                        tweet = result.getResult();
                        mAgencyViewImp.resetCmnCount(tweet.getCommentCount());
                        mAgencyViewImp.resetLikeCount(tweet.getLikeCount());
                        fillDetailView();
                    }else{
                        onFailure(500, headers, "妈的智障", null);
                    }
                }
            });
        }

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
                dialog = DialogHelp.getWaitDialog(TweetDetailActivity.this, "正在发表评论...");
                dialog.show();
//                OSChinaApi.pubTweetComment(tweet.getId(), content, reply == null ? 0 : reply.getId(), publishCommentHandler);
                if (TweetDetailActivity.this.reply == null) {
                    OSChinaApi.publicComment(3, tweet.getId(), AppContext.getInstance().getLoginUid(),
                            v.getText().toString(), 1, publishCommentHandler);
                } else {
                    OSChinaApi.replyComment((int) tweet.getId(), 3, reply.getId(), reply.getAuthorId(),
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
        if (tweet.getAudio() == null || tweet.getAudio().length == 0) return;
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
        if (isDestroy())
            return;
        if (tweet.getAuthor() != null){
            if (TextUtils.isEmpty(tweet.getAuthor().getPortrait())) {
                ivPortrait.setImageResource(R.drawable.widget_dface);
            } else {
                getImageLoader()
                        .load(tweet.getAuthor().getPortrait())
                        .asBitmap()
                        .placeholder(getResources().getDrawable(R.drawable.widget_dface))
                        .error(getResources().getDrawable(R.drawable.widget_dface))
                        .into(ivPortrait);
            }
            ivPortrait.setOnClickListener(getOnPortraitClickListener());
            tvNick.setText(tweet.getAuthor().getName());
        }
        if (!TextUtils.isEmpty(tweet.getPubDate()))
            tvTime.setText(StringUtils.friendly_time(tweet.getPubDate()));
        PlatfromUtil.setPlatFromString(tvClient, tweet.getAppClient());
        if (tweet.isLiked()) {
            ivThumbup.setSelected(true);
        } else {
            ivThumbup.setSelected(false);
        }
        if (!TextUtils.isEmpty(tweet.getContent())){
            CommentsUtil.formatHtml(getResources(), mContent, tweet.getContent());
        }
        if (tweet.getImages() != null && tweet.getImages().length > 0){
            mLayoutGrid.setVisibility(View.VISIBLE);
            final View.OnClickListener l = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String mImageUrl = (String) v.getTag();
                    OSCPhotosActivity.showImagePreview(TweetDetailActivity.this, mImageUrl);
                }
            };
            for (int i = 0; i < tweet.getImages().length; i++){
                ImageView mImage = new ImageView(this);
                GridLayout.LayoutParams mParams = new GridLayout.LayoutParams();
                mParams.setMargins(0, (int) TDevice.dpToPixel(8), (int) TDevice.dpToPixel(8), 0);
                mParams.width = (int) TDevice.dpToPixel(80);
                mParams.height = (int) TDevice.dpToPixel(80);
                mImage.setLayoutParams(mParams);
                mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mLayoutGrid.addView(mImage);
                getImageLoader()
                        .load(tweet.getImages()[i].getThumb())
                        .asBitmap()
                        .placeholder(R.drawable.ic_default_image)
                        .error(R.drawable.ic_default_image)
                        .into(mImage);
                mImage.setTag(tweet.getImages()[i].getHref());
                mImage.setOnClickListener(l);
            }
        }else {
            mLayoutGrid.setVisibility(View.GONE);
        }
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

    @OnClick(R.id.iv_thumbup)
    void onClickThumbUp() {
        this.dialog = DialogHelp.getWaitDialog(this, "正在提交请求...");
        this.dialog.show();
        if (!ivThumbup.isSelected()) {
            OSChinaApi.pubLikeTweet((int) tweet.getId(), (int) tweet.getAuthor().getId(), publishAdmireHandler);
        } else {
            OSChinaApi.pubUnLikeTweet((int) tweet.getId(), (int) tweet.getAuthor().getId(), publishAdmireHandler);
        }
    }

    @OnClick(R.id.iv_comment)
    void onClickComment() {
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
