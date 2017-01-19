package net.oschina.app.improve.tweet.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.emoji.EmojiKeyboardFragment;
import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.emoji.OnEmojiClickListener;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.tweet.activities.TweetTopicActivity;
import net.oschina.app.improve.tweet.contract.TweetPublishContract;
import net.oschina.app.improve.tweet.contract.TweetPublishOperator;
import net.oschina.app.improve.tweet.widget.TweetPicturesPreviewer;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapter;
import net.oschina.app.util.UIHelper;
import net.oschina.common.adapter.TextWatcherAdapter;
import net.oschina.common.widget.RichEditText;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 发布动弹界面实现
 */
@SuppressWarnings("WeakerAccess")
public class TweetPublishFragment extends BaseFragment implements View.OnClickListener,
        TweetPublishContract.View {

    public static final int MAX_TEXT_LENGTH = 160;
    public static final int REQUEST_CODE_SELECT_FRIENDS = 0x0001;
    public static final int REQUEST_CODE_SELECT_TOPIC = 0x0002;

    @Bind(R.id.edit_content)
    RichEditText mEditContent;

    @Bind(R.id.recycler_images)
    TweetPicturesPreviewer mLayImages;

    @Bind(R.id.txt_indicator)
    TextView mIndicator;

    @Bind(R.id.icon_back)
    View mIconBack;

    @Bind(R.id.icon_send)
    View mIconSend;

    private TweetPublishContract.Operator mOperator;
    private EmojiKeyboardFragment mEmojiKeyboard;

    public TweetPublishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        // init operator
        this.mOperator = new TweetPublishOperator();
        String defaultContent = null;
        String[] paths = null;
        About.Share share = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            defaultContent = bundle.getString("defaultContent");
            paths = bundle.getStringArray("defaultImages");
            share = (About.Share) bundle.getSerializable("aboutShare");
        }
        this.mOperator.setDataView(this, defaultContent, paths, share);

        super.onAttach(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tweet_publish;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // EmojiKeyboardFragment
        mEmojiKeyboard = (EmojiKeyboardFragment) getChildFragmentManager().findFragmentById(R.id.frag_emoji_keyboard);
        mEmojiKeyboard.setClipStatusHeight(true);
        mEmojiKeyboard.setOnEmojiClickListener(new OnEmojiClickListener() {
            @Override
            public void onEmojiClick(Emojicon v) {
                InputHelper.input2OSC(mEditContent, v);
            }

            @Override
            public void onDeleteButtonClick(View v) {
                InputHelper.backspace(mEditContent);
            }
        });

        // set hide action
        mLayImages.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideAllKeyBoard();
                return false;
            }
        });

        // add text change listener
        mEditContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                final int len = s.length();
                final int surplusLen = MAX_TEXT_LENGTH - len;
                // set the send icon state
                setSendIconStatus(len > 0 && surplusLen >= 0, s.toString());
                // checkShare the indicator state
                if (surplusLen > 10) {
                    // hide
                    if (mIndicator.getVisibility() != View.INVISIBLE) {
                        ViewCompat.animate(mIndicator)
                                .alpha(0)
                                .setDuration(200)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIndicator.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .start();
                    }
                } else {
                    // show
                    if (mIndicator.getVisibility() != View.VISIBLE) {
                        ViewCompat.animate(mIndicator)
                                .alpha(1f)
                                .setDuration(200)
                                .withStartAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mIndicator.setVisibility(View.VISIBLE);
                                    }
                                })
                                .start();
                    }

                    mIndicator.setText(String.valueOf(surplusLen));
                    //noinspection deprecation
                    mIndicator.setTextColor(surplusLen >= 0 ?
                            getResources().getColor(R.color.tweet_indicator_text_color) :
                            getResources().getColor(R.color.tweet_indicator_text_color_error));
                }
            }
        });

        // 设置键盘输入#或者@适合的监听器
        mEditContent.setOnKeyArrivedListener(new OnKeyArrivedListenerAdapter(this));
        mEditContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mEmojiKeyboard.hideEmojiKeyBoard();
                return false;
            }
        });

        // Show keyboard
        showSoftKeyboard(mEditContent);
    }

    private void setSendIconStatus(boolean haveContent, String content) {
        if (haveContent) {
            content = content.trim();
            haveContent = !TextUtils.isEmpty(content);
        }
        mIconSend.setEnabled(haveContent);
    }


    @Override
    protected void initData() {
        super.initData();
        mOperator.loadData();
    }

    // 用于拦截后续的点击事件
    private long mLastClickTime;

    @OnClick({R.id.iv_picture, R.id.iv_mention, R.id.iv_tag,
            R.id.iv_emoji, R.id.txt_indicator, R.id.icon_back,
            R.id.icon_send, R.id.edit_content})
    @Override
    public void onClick(View v) {
        // 用来解决快速点击多个按钮弹出多个界面的情况
        long nowTime = System.currentTimeMillis();
        if ((nowTime - mLastClickTime) < 1000)
            return;
        mLastClickTime = nowTime;

        try {
            switch (v.getId()) {
                case R.id.iv_picture:
                    hideAllKeyBoard();
                    mLayImages.onLoadMoreClick();
                    break;
                case R.id.iv_mention:
                    hideAllKeyBoard();
                    toSelectFriends();
                    break;
                case R.id.iv_tag:
                    toSelectTopic();
                    break;
                case R.id.iv_emoji:
                    handleEmojiClick(v);
                    break;
                case R.id.txt_indicator:
                    handleClearContentClick();
                    break;
                case R.id.icon_back:
                    mOperator.onBack();
                    break;
                case R.id.icon_send:
                    mOperator.publish();
                    break;
                case R.id.edit_content: {
                    mEmojiKeyboard.hideEmojiKeyBoard();
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClearContentClick() {
        if (mIndicator.isSelected()) {
            mIndicator.setSelected(false);
            mEditContent.setText("");
        } else {
            mIndicator.setSelected(true);
            mIndicator.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIndicator.setSelected(false);
                }
            }, 1000);
        }
    }

    /**
     * Emoji 表情点击
     *
     * @param v View
     */
    private void handleEmojiClick(View v) {
        if (mEmojiKeyboard.isShow()) {
            mEmojiKeyboard.hideEmojiKeyBoard();
            showSoftKeyboard(mEditContent);
        } else {
            hideSoftKeyboard();
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mEmojiKeyboard.showEmojiKeyBoard();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 200);
        }
    }

    /**
     * 跳转选择话题
     */
    private void toSelectTopic() {
        Context context = getContext();
        if (context == null)
            return;
        if (!AccountHelper.isLogin()) {
            UIHelper.showLoginActivity(context);
            return;
        }

        TweetTopicActivity.show(this, mEditContent);
    }


    /**
     * 跳转选择好友
     */
    private void toSelectFriends() {
        Context context = getContext();
        if (context == null)
            return;
        if (!AccountHelper.isLogin()) {
            UIHelper.showLoginActivity(context);
            return;
        }

        UserSelectFriendsActivity.show(this, mEditContent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_FRIENDS:
                    // Nun Do handleSelectFriendsResult(data);
                    break;
                case REQUEST_CODE_SELECT_TOPIC:
                    // Nun Do handleSelectTopicResult(data);
                    break;
            }
        }

        mEditContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSoftKeyboard(mEditContent);
            }
        }, 200);
    }

    private void hideSoftKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                mEditContent.getWindowToken(), 0);
    }

    private void showSoftKeyboard(final EditText requestView) {
        if (requestView == null)
            return;
        requestView.requestFocus();
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(requestView,
                InputMethodManager.SHOW_FORCED);
    }

    private void hideAllKeyBoard() {
        mEmojiKeyboard.hideEmojiKeyBoard();
        hideSoftKeyboard();
    }

    @Override
    public String getContent() {
        return mEditContent.getText().toString();
    }

    @Override
    public void setContent(String content, boolean needSelectionEnd) {
        Spannable span = InputHelper.displayEmoji(getResources(), content, (int) mEditContent.getTextSize());
        mEditContent.setText(span);
        //if (needSelectionEnd)
        mEditContent.setSelection(mEditContent.getText().length());
    }

    @Override
    public void setAbout(About.Share share, boolean needCommit) {
        if (TextUtils.isEmpty(share.title) && TextUtils.isEmpty(share.content))
            return;
        // Change the layout visibility
        mLayImages.setVisibility(View.GONE);
        setVisibility(R.id.lay_about);
        // Set title and content
        ((TextView) findView(R.id.txt_about_title)).setText(share.type == OSChinaApi.COMMENT_TWEET ?
                "@" + share.title : share.title);
        ((TextView) findView(R.id.txt_about_content)).setText(AssimilateUtils.clearHtmlTag(share.content));
        findView(R.id.iv_picture).setEnabled(false);

        if (needCommit)
            setVisibility(R.id.cb_commit_control);
        else
            setGone(R.id.cb_commit_control);
    }

    @Override
    public boolean needCommit() {
        return ((CheckBox) findView(R.id.cb_commit_control)).isChecked();
    }

    @Override
    public String[] getImages() {
        return mLayImages.getPaths();
    }

    @Override
    public void setImages(String[] paths) {
        mLayImages.set(paths);
    }

    @Override
    public void finish() {
        // hide key board before finish
        hideAllKeyBoard();
        // finish
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseBackActivity) {
            ((BaseBackActivity) activity).onSupportNavigateUp();
        }
    }

    @Override
    public TweetPublishContract.Operator getOperator() {
        return mOperator;
    }

    @Override
    public boolean onBackPressed() {
        if (mEmojiKeyboard.isShow()) {
            mEmojiKeyboard.hideEmojiKeyBoard();
            return false;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mOperator.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestartInstance(Bundle bundle) {
        super.onRestartInstance(bundle);
        if (bundle != null)
            mOperator.onRestoreInstanceState(bundle);
    }
}
