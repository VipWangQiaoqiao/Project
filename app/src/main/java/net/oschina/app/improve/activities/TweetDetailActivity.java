package net.oschina.app.improve.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import net.oschina.app.emoji.EmojiKeyboardFragment;
import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.emoji.OnEmojiClickListener;
import net.oschina.app.improve.contract.TweetDetailContract;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.SimpleTextWatcher;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
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
    @Bind(R.id.iv_small_img)
    ImageView ivSmallImg;
    @Bind(R.id.tv_content)
    TextView tvContent;
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

    private Tweet tweet;
    private Comment reply;
    private boolean isUped;
    private Dialog dialog;
    private AsyncHttpResponseHandler upHandler;
    private AsyncHttpResponseHandler cmnHandler;

    private TweetDetailContract.CmnView mCmnView;
    private TweetDetailContract.ThumbupView mThumbupView;

    private final EmojiKeyboardFragment keyboardFragment = new EmojiKeyboardFragment();

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
                .replace(R.id.emoji_keyboard_fragment, keyboardFragment)
                .commit();

        keyboardFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
            @Override
            public void onEmojiClick(Emojicon v) {
                InputHelper.input2OSC(etInput, v);
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
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
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
    }

    private void initView() {
        RequestManager reqManager = Glide.with(this);

        reqManager.load(tweet.getPortrait()).into(ivPortrait);
        tvNick.setText(tweet.getAuthor());
        tvContent.setText(InputHelper.displayEmoji(getResources(), tweet.getBody()));
        tvTime.setText(StringUtils.friendly_time(tweet.getPubDate()));
        tvCmnCount.setText(tweet.getCommentCount());
        PlatfromUtil.setPlatFromString(tvClient, tweet.getAppclient());

        if (!TextUtils.isEmpty(tweet.getImgSmall())) {
            ivSmallImg.setVisibility(View.VISIBLE);
            reqManager.load(tweet.getImgSmall()).into(ivSmallImg);
        }

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


        TweetDetailViewPagerFragment frag = TweetDetailViewPagerFragment.instantiate(this);
        mCmnView = frag;
        mThumbupView = frag;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag)
                .commitAllowingStateLoss();


        ivEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!keyboardFragment.isShow()) {// emoji隐藏中
                    keyboardFragment.showEmojiKeyBoard();
                    keyboardFragment.hideSoftKeyboard();
                } else {
                    keyboardFragment.hideEmojiKeyBoard();
                    keyboardFragment.showSoftKeyboard(etInput);
                }
            }
        });
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
        if (!TextUtils.isEmpty(etInput.getText().toString())) return;
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

    @Override
    public void toUserHome(int oid) {

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
}
