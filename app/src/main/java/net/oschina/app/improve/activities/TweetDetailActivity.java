package net.oschina.app.improve.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetDetail;
import net.oschina.app.bean.TweetsList;
import net.oschina.app.emoji.EmojiKeyboardFragment;
import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.emoji.OnEmojiClickListener;
import net.oschina.app.improve.behavior.KeyboardActionDelegation;
import net.oschina.app.improve.contract.TweetDetailContract;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.SimpleTextWatcher;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.ThemeSwitchUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.viewpagerfragment.TweetDetailViewPagerFragment;
import net.oschina.app.widget.CircleImageView;

import java.io.Serializable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * 动弹详情
 * Created by thanatos on 16/6/13.
 */
public class TweetDetailActivity extends AppCompatActivity implements TweetDetailContract.Operator {

    public static final String BUNDLE_KEY_TWEET = "BUNDLE_KEY_TWEET";

    @Bind(R.id.iv_portrait)
    CircleImageView ivPortrait;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.webview)
    WebView mWebview;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_client)
    TextView tvClient;
    @Bind(R.id.iv_thumbup)
    ImageView ivThumbup;
    @Bind(R.id.iv_comment)
    ImageView ivComment;
    @Bind(R.id.tv_comment_count)
    TextView tvCmnCount;
    @Bind(R.id.et_input)
    EditText etInput;
    @Bind(R.id.iv_emoji)
    ImageView ivEmoji;
    @Bind(R.id.emoji_keyboard_fragment)
    FrameLayout mEmojiPanelLayout;
    @Bind(R.id.layout_coordinator)
    CoordinatorLayout mCoordinatorLayout;

    private Tweet tweet;
    private Comment reply;
    private boolean isUped;
    private Dialog dialog;
    private boolean mLastIsEmpty = true;
    private AsyncHttpResponseHandler upHandler;
    private AsyncHttpResponseHandler cmnHandler;

    private TweetDetailContract.CmnView mCmnView;
    private TweetDetailContract.ThumbupView mThumbupView;
    private TweetDetailContract.AgencyView mAgencyView;

    private final EmojiKeyboardFragment mKeyboardFragment = new EmojiKeyboardFragment();
    private TweetDetailViewPagerFragment mTweetDetailViewPagerFrag;
    private KeyboardActionDelegation mKADelegation;
    private View.OnClickListener onPortraitClickListener;

    public static void show(Context context, Tweet tweet) {
        Intent intent = new Intent(context, TweetDetailActivity.class);
        intent.putExtra(BUNDLE_KEY_TWEET, (Serializable) tweet);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tweet_detail);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setTitle("动弹详情");
        }
        initData();
        initView();


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.emoji_keyboard_fragment, mKeyboardFragment)
                .commit();

        mKADelegation = KeyboardActionDelegation.delegation(this, etInput, ivEmoji, mEmojiPanelLayout);

        mKeyboardFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
            @Override
            public void onEmojiClick(Emojicon v) {
                mKADelegation.onEmotionItemSelected(v);
            }

            @Override
            public void onDeleteButtonClick(View v) {
                InputHelper.backspace(etInput);
            }
        });
        etInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if ("@".equals(s.toString())) {
                    //toSelectFriends();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_KEY_TWEET, tweet);
    }

    private void initData() {
        tweet = (Tweet) getIntent().getSerializableExtra(BUNDLE_KEY_TWEET);
        if (tweet == null) {
            Toast.makeText(this, "对象没找到", Toast.LENGTH_SHORT).show();
            finish();
        }

        upHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ivThumbup.setImageResource(isUped ? R.drawable.ic_thumbup_normal : R.drawable.ic_thumbup_actived);
                mThumbupView.onLikeSuccess(!isUped, null);
                isUped = !isUped;
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(TweetDetailActivity.this, isUped ? "取消失败" : "点赞失败", Toast.LENGTH_SHORT).show();
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        };

        cmnHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mCmnView.onCommentSuccess(null);
                tvCmnCount.setText(String.valueOf(tweet.getCommentCount()));
                reply = null; // 清除
                etInput.setHint("发表评论");
                etInput.setText(null);
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(TweetDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        };

        OSChinaApi.getTweetDetail(tweet.getId(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                TweetDetail data = XmlUtils.toBean(TweetDetail.class, responseBody);
                tweet = data.getTweet();
                mAgencyView.resetCmnCount(Integer.valueOf(tweet.getCommentCount()));
                mAgencyView.resetLikeCount(tweet.getLikeCount());
                fillDetailView();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void initView() {
        fillDetailView();

        if (tweet.getIsLike() == 1) {
            ivThumbup.setImageResource(R.drawable.ic_thumbup_actived);
        } else {
            ivThumbup.setImageResource(R.drawable.ic_thumbup_normal);
        }

        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    onClickSend();
                    return true;
                }
                return false;
            }
        });
        etInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    onClickDelete();
                }
                return false;
            }
        });

        mCoordinatorLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) return;
                if (mKADelegation == null) return;
                mKADelegation.onTurnBack();
            }
        });


        mTweetDetailViewPagerFrag = TweetDetailViewPagerFragment.instantiate(this);
        mCmnView = mTweetDetailViewPagerFrag;
        mThumbupView = mTweetDetailViewPagerFrag;
        mAgencyView = mTweetDetailViewPagerFrag;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mTweetDetailViewPagerFrag)
                .commitAllowingStateLoss();
    }

    private void fillDetailView(){
        RequestManager reqManager = Glide.with(this);

        reqManager.load(tweet.getPortrait()).into(ivPortrait);
        ivPortrait.setOnClickListener(getOnPortraitClickListener());
        tvNick.setText(tweet.getAuthor());
        tvTime.setText(StringUtils.friendly_time(tweet.getPubDate()));
        tvCmnCount.setText(tweet.getCommentCount());
        PlatfromUtil.setPlatFromString(tvClient, tweet.getAppclient());

        fillWebViewBody();
    }

    /**
     * 填充webview内容
     */
    private void fillWebViewBody() {
        StringBuffer body = new StringBuffer();
        body.append(ThemeSwitchUtils.getWebViewBodyString());
        body.append(UIHelper.WEB_STYLE + UIHelper.WEB_LOAD_IMAGES);

        StringBuilder tweetbody = new StringBuilder(tweet.getBody());

        String tweetBody = TextUtils.isEmpty(tweet.getImgSmall())
                ? tweetbody.toString()
                : tweetbody.toString() + "<br/><img src=\"" + tweet.getImgSmall() + "\">";
        body.append(setHtmlCotentSupportImagePreview(tweetBody));

        UIHelper.addWebImageShow(this, mWebview);
        // 封尾
        body.append("</div></body>");
        mWebview.loadDataWithBaseURL(null, body.toString(), "text/html", "utf-8", null);
    }

    /**
     * 添加图片放大支持
     *
     * @param body
     * @return
     */
    private String setHtmlCotentSupportImagePreview(String body) {
        // 过滤掉 img标签的width,height属性
        body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
        body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
        return body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                "$1$2\" onClick=\"javascript:mWebViewImageListener.showImagePreview('" + tweet.getImgBig() + "')\"");
    }

    private View.OnClickListener getOnPortraitClickListener(){
        if (onPortraitClickListener == null){
            onPortraitClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showUserCenter(TweetDetailActivity.this, tweet.getAuthorid(), tweet.getAuthor());
                }
            };
        }
        return onPortraitClickListener;
    }

    private void onClickSend() {
        if (TextUtils.isEmpty(etInput.getText().toString().replaceAll("[ \\s\\n]+", ""))) {
            Toast.makeText(this, "请输入文字", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            return;
        }

        this.dialog = DialogHelp.getWaitDialog(this, "正在发表评论...");
        this.dialog.show();

        if (reply == null) {
            OSChinaApi.publicComment(3, tweet.getId(), AppContext.getInstance().getLoginUid(),
                    etInput.getText().toString(), 1, cmnHandler);
        } else {
            OSChinaApi.replyComment(tweet.getId(), 3, reply.getId(), reply.getAuthorId(),
                    AppContext.getInstance().getLoginUid(), etInput.getText().toString(), cmnHandler);
        }
    }

    private void onClickDelete() {
        if (!TextUtils.isEmpty(etInput.getText().toString())) {
            mLastIsEmpty = false;
            return;
        }
        if (TextUtils.isEmpty(etInput.getText().toString()) && !mLastIsEmpty){
            mLastIsEmpty = true;
            return;
        }
        if (this.reply == null) return;
        reply = null;
        etInput.setHint("发表评论");
    }

    @Override
    public Tweet getTweetDetail() {
        return tweet;
    }

    @Override
    public void toReply(Comment comment) {
        this.reply = comment;
        etInput.setHint("回复@ " + comment.getAuthor());
    }

    @OnClick(R.id.iv_thumbup)
    void onClickThumbup() {
        this.dialog = DialogHelp.getWaitDialog(this, "正在提交请求...");
        this.dialog.show();
        if (!isUped) {
            OSChinaApi.pubLikeTweet(tweet.getId(), tweet.getAuthorid(), upHandler);
        } else {
            OSChinaApi.pubUnLikeTweet(tweet.getId(), tweet.getAuthorid(), upHandler);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if (!mKADelegation.onTurnBack()) return true;
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
