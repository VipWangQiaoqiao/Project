package net.oschina.app.improve.tweet.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
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
import net.oschina.app.improve.widget.listenerAdapter.TextWatcherAdapter;
import net.oschina.app.util.UIHelper;
import net.oschina.common.widget.RichEditText;
import net.oschina.common.widget.drawable.shape.BorderShape;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 发布动弹界面实现
 */
@SuppressWarnings("WeakerAccess")
public class TweetPublishFragment extends BaseFragment implements View.OnClickListener,
        TweetPublishContract.View {
    private final static String TAG = TweetPublishFragment.class.getName();
    public static final int MAX_TEXT_LENGTH = 160;
    public static final int REQUEST_CODE_SELECT_FRIENDS = 0x0001;
    public static final int REQUEST_CODE_SELECT_TOPIC = 0x0002;
    private static final String TEXT_TAG = "#输入软件名#";

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

    @Bind(R.id.lay_enter_tag)
    View mLayTagEnter;

    @Bind(R.id.edit_enter_tag)
    EditText mEditTagEnter;

    private TweetPublishContract.Operator mOperator;
    private final EmojiKeyboardFragment mEmojiKeyboard = new EmojiKeyboardFragment();

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
        getChildFragmentManager().beginTransaction()
                .replace(R.id.lay_emoji_keyboard, mEmojiKeyboard)
                .commitNowAllowingStateLoss();

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
                mEmojiKeyboard.hideAllKeyBoard();
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

        mEditContent.setOnKeyArrivedListener(new RichEditText.OnKeyArrivedListener() {
            @Override
            public boolean onMentionKeyArrived() {
                onClick(findView(R.id.iv_mention));
                return true;
            }

            @Override
            public boolean onTopicKeyArrived() {
                onClick(findView(R.id.iv_tag));
                return true;
            }
        });

        mEditTagEnter.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mLayTagEnter.animate()
                            .alphaBy(1)
                            .alpha(0)
                            .setDuration(100)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mLayTagEnter.setVisibility(View.GONE);
                                    mLayTagEnter.setAlpha(1);
                                }
                            }).start();
                }
            }
        });

        ShapeDrawable doubleLineDrawable = new ShapeDrawable(new BorderShape(new RectF(0, 1, 0, 1)));
        doubleLineDrawable.getPaint().setColor(0xFF24cf5f);
        mLayTagEnter.setBackground(doubleLineDrawable);

        // Show keyboard
        mEmojiKeyboard.showSoftKeyboard(mEditContent);
    }

    private void setSendIconStatus(boolean haveContent, String content) {
        if (haveContent) {
            content = content.trim();
            haveContent = (!TextUtils.isEmpty(content))
                    && (!TEXT_TAG.equals(content));
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
            R.id.icon_send, R.id.btn_submit_enter_tag})
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
                    mEmojiKeyboard.hideAllKeyBoard();
                    mLayImages.onLoadMoreClick();
                    break;
                case R.id.iv_mention:
                    mEmojiKeyboard.hideAllKeyBoard();
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
                case R.id.btn_submit_enter_tag:
                    handleInsertTag();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleClearContentClick() {
        mIndicator.setSelected(!mIndicator.isSelected());
        if (mIndicator.isSelected()) {
            mIndicator.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIndicator.setSelected(false);
                }
            }, 1000);
        } else {
            mEditContent.setText("");
        }
    }

    /**
     * Emoji 表情点击
     *
     * @param v View
     */
    private void handleEmojiClick(View v) {
        hideInsertTag();
        if (mEmojiKeyboard.isShow()) {
            mEmojiKeyboard.hideEmojiKeyBoard();
            showSoftKeyboard(mEditContent);
        } else {
            mEmojiKeyboard.hideSoftKeyboard();
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

    private void handleInsertTag() {
        String text = mEditTagEnter.getText().toString();
        if (text.trim().length() > 0) {
            Editable msg = mEditContent.getText();
            int selStart = mEditContent.getSelectionStart();
            int selEnd = mEditContent.getSelectionEnd();

            text = String.format("#%s# ", text.replace("#", ""));

            int selStartBefore = selStart - 1;
            if (selStart == selEnd && selStart > 0
                    && "#".equals(msg.subSequence(selStartBefore, selEnd).toString())
                    && msg.getSpans(selStartBefore, selEnd, RichEditText.TagSpan.class).length == 0) {
                selStart = selStartBefore;
            }

            Spannable spannable = RichEditText.matchMention(new SpannableString(text));
            spannable = RichEditText.matchTopic(spannable);

            msg.replace(selStart, selEnd, spannable);
        }

        hideInsertTag();
    }

    private void hideInsertTag() {
        mLayTagEnter.setVisibility(View.GONE);
        mEditTagEnter.clearFocus();
        mEditContent.requestFocus();
    }

    private void showInsertTag() {
        mEditTagEnter.setText("");
        mLayTagEnter.setVisibility(View.VISIBLE);
        mEditContent.clearFocus();
        mEditTagEnter.requestFocus();
    }

    /**
     * 插入 #软件名#
     */
    private void insertTrendSoftware() {
        final EditText editText = mEditContent;
        final int maxTextLen = MAX_TEXT_LENGTH;
        int curTextLength = editText.getText().length();
        if (curTextLength >= maxTextLen)
            return;
        String software = TEXT_TAG;
        int start, end;
        if ((maxTextLen - curTextLength) >= software.length()) {
            start = editText.getSelectionStart() + 1;
            end = start + software.length() - 2;
        } else {
            int num = maxTextLen - curTextLength;
            if (num < software.length()) {
                software = software.substring(0, num);
            }
            start = editText.getSelectionStart() + 1;
            end = start + software.length() - 1;
        }
        if (start > maxTextLen || end > maxTextLen) {
            start = maxTextLen;
            end = maxTextLen;
            mLayTagEnter.setVisibility(View.VISIBLE);
            mEditContent.clearFocus();
            mEditTagEnter.requestFocus();
        }
        editText.getText().insert(editText.getSelectionStart(), software);
        editText.setSelection(start, end);
        /*
        if (mLayTagEnter.getVisibility() == View.VISIBLE) {
            hideInsertTag();
        } else {
            showInsertTag();
        }
        */
    }

    private void toSelectTopic() {
        Context context = getContext();
        if (context == null)
            return;
        if (!AccountHelper.isLogin()) {
            UIHelper.showLoginActivity(context);
            return;
        }

        Intent intent = new Intent(context, TweetTopicActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_TOPIC);
    }

    /**
     * 好友名字选择
     *
     * @param data Intent
     */
    private void handleSelectTopicResult(Intent data) {
        String topic = data.getStringExtra("topic");
        if (!TextUtils.isEmpty(topic)) {
            topic = String.format("#%s#", topic.trim());

            SpannableString spannable = new SpannableString(topic);
            RichEditText.matchTopic(spannable);

            Editable msg = mEditContent.getText();
            int selStart = mEditContent.getSelectionStart();
            int selEnd = mEditContent.getSelectionEnd();

            int selStartBefore = selStart - 1;
            if (selStart == selEnd && selStart > 0
                    && "#".equals(msg.subSequence(selStartBefore, selEnd).toString())
                    && msg.getSpans(selStartBefore, selEnd, RichEditText.TagSpan.class).length == 0) {
                selStart = selStartBefore;
            }

            msg.replace(selStart, selEnd, spannable);
        }
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

        Intent intent = new Intent(context, UserSelectFriendsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_FRIENDS);
    }

    /**
     * 好友名字选择
     *
     * @param data Intent
     */
    private void handleSelectFriendsResult(Intent data) {
        String names[] = data.getStringArrayExtra("names");
        if (names != null && names.length > 0) {
            String text = "";
            for (String n : names) {
                text += "@" + n + " ";
            }

            SpannableString spannable = new SpannableString(text);
            RichEditText.matchMention(spannable);
            RichEditText.matchTopic(spannable);

            Editable msg = mEditContent.getText();
            int selStart = mEditContent.getSelectionStart();
            int selEnd = mEditContent.getSelectionEnd();

            int selStartBefore = selStart - 1;
            if (selStart == selEnd && selStart > 0
                    && "@".equals(msg.subSequence(selStartBefore, selEnd).toString())
                    && msg.getSpans(selStartBefore, selEnd, RichEditText.TagSpan.class).length == 0) {
                selStart = selStartBefore;
            }

            msg.replace(selStart, selEnd, spannable);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_FRIENDS:
                    handleSelectFriendsResult(data);
                    break;
                case REQUEST_CODE_SELECT_TOPIC:
                    handleSelectTopicResult(data);
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

    private void showSoftKeyboard(final EditText requestView) {
        if (requestView == null)
            return;
        requestView.requestFocus();
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(requestView,
                InputMethodManager.SHOW_FORCED);
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
        mEmojiKeyboard.hideAllKeyBoard();
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
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        if (savedInstanceState != null)
            mOperator.onRestoreInstanceState(savedInstanceState);
    }
}
