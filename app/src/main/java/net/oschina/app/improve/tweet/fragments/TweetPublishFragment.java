package net.oschina.app.improve.tweet.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.emoji.EmojiKeyboardFragment;
import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.emoji.OnEmojiClickListener;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.tweet.contract.TweetPublishContract;
import net.oschina.app.improve.tweet.contract.TweetPublishOperator;
import net.oschina.app.improve.tweet.widget.ClipView;
import net.oschina.app.improve.tweet.widget.TweetPicturesPreviewer;
import net.oschina.app.ui.SelectFriendsActivity;
import net.oschina.app.util.UIHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 发布动弹界面实现
 */
@SuppressWarnings("WeakerAccess")
public class TweetPublishFragment extends BaseFragment implements View.OnClickListener, TweetPublishContract.View {
    public static final int MAX_TEXT_LENGTH = 160;
    private static final int SELECT_FRIENDS_REQUEST_CODE = 100;
    private static final String TEXT_TAG = "#输入软件名#";

    @Bind(R.id.edit_content)
    EditText mEditContent;

    @Bind(R.id.recycler_images)
    TweetPicturesPreviewer mLayImages;

    @Bind(R.id.txt_indicator)
    TextView mIndicator;

    @Bind(R.id.icon_back)
    View mIconBack;

    @Bind(R.id.icon_send)
    View mIconSend;

    private TweetPublishContract.Operator mOperator;
    private final EmojiKeyboardFragment mEmojiKeyboard = new EmojiKeyboardFragment();

    public TweetPublishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        // init operator
        this.mOperator = new TweetPublishOperator();
        this.mOperator.setDataView(this, getArguments() != null ?
                getArguments().getString("defaultContent") : null);
        super.onAttach(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tweet_publish;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        if (root instanceof ClipView) {
            ClipView clipView = ((ClipView) root);

            if (mBundle != null) {
                clipView.setup(mBundle.getIntArray("location"), mBundle.getIntArray("size"));
            } else {
                clipView.setup(null, null);
            }

            clipView.post(new Runnable() {
                @Override
                public void run() {
                    if (mRoot instanceof ClipView) {
                        ((ClipView) mRoot).start(new Runnable() {
                            @Override
                            public void run() {
                                mEmojiKeyboard.showSoftKeyboard(mEditContent);
                            }
                        });
                    } else {
                        mEmojiKeyboard.showSoftKeyboard(mEditContent);
                    }
                }
            });
        }

        // EmojiKeyboardFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.lay_emoji_keyboard, mEmojiKeyboard)
                .commit();
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
        mEditContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                final int len = s.length();
                final int surplusLen = MAX_TEXT_LENGTH - len;
                // set the send icon state
                setSendIconStatus(len > 0 && surplusLen >= 0, s.toString());
                // check the indicator state
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
        mOperator.loadXmlData();
    }

    @OnClick({R.id.iv_picture, R.id.iv_mention, R.id.iv_tag,
            R.id.iv_emoji, R.id.txt_indicator, R.id.icon_back, R.id.icon_send})
    @Override
    public void onClick(View v) {
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
                insertTrendSoftware();
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
        if (!mEmojiKeyboard.isShow()) {
            mEmojiKeyboard.hideSoftKeyboard();
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEmojiKeyboard.showEmojiKeyBoard();
                }
            }, 280);
        } else {
            mEmojiKeyboard.hideEmojiKeyBoard();
        }
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
        }
        editText.getText().insert(editText.getSelectionStart(), software);
        editText.setSelection(start, end);
    }

    /**
     * 跳转选择好友
     */
    private void toSelectFriends() {
        Context context = getContext();
        if (context == null)
            return;
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(context);
            return;
        }
        Intent intent = new Intent(context, SelectFriendsActivity.class);
        startActivityForResult(intent, SELECT_FRIENDS_REQUEST_CODE);
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
            mEditContent.getText().insert(mEditContent.getSelectionStart(), text);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == SELECT_FRIENDS_REQUEST_CODE) {
            handleSelectFriendsResult(data);
        }
    }

    @Override
    public String getContent() {
        return mEditContent.getText().toString();
    }

    @Override
    public void setContent(String content) {
        mEditContent.setText(content);
        mEditContent.setSelection(mEditContent.getText().length());
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
        // do animation to finish
        if (mRoot instanceof ClipView) {
            ((ClipView) mRoot).exit(new Runnable() {
                @Override
                public void run() {
                    Activity activity = getActivity();
                    if (activity != null && activity instanceof BaseBackActivity) {
                        ((BaseBackActivity) activity).onSupportNavigateUp();
                    }
                }
            });
        }
    }

    @Override
    public TweetPublishContract.Operator getOperator() {
        return mOperator;
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
