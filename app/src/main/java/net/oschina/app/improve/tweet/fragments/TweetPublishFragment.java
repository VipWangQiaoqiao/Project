package net.oschina.app.improve.tweet.fragments;


import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.emoji.EmojiKeyboardFragment;
import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.emoji.OnEmojiClickListener;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.tweet.contract.TweetPublishContract;
import net.oschina.app.improve.tweet.widget.TweetPicturesPreviewer;
import net.oschina.app.ui.SelectFriendsActivity;
import net.oschina.app.util.UIHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 发布动弹界面实现
 */
<<<<<<< HEAD
public class TweetPublishFragment extends BaseFragment<T> implements View.OnClickListener, TweetPublishContract.View {
=======
@SuppressWarnings("WeakerAccess")
public class TweetPublishFragment extends BaseFragment implements View.OnClickListener, TweetPublishContract.View {
>>>>>>> 86047d6916691f7a10c4efcfbc43796b03787659
    public static final int MAX_TEXT_LENGTH = 160;
    private static final int SELECT_FRIENDS_REQUEST_CODE = 100;
    private static final String TEXT_TAG = "#请输入软件名#";

    @Bind(R.id.edit_content)
    EditText mEditContent;

    @Bind(R.id.recycler_images)
    TweetPicturesPreviewer mLayImages;

    private final EmojiKeyboardFragment keyboardFragment = new EmojiKeyboardFragment();

    public TweetPublishFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tweet_publish;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.lay_emoji_keyboard, keyboardFragment)
                .commit();
        keyboardFragment.setOnEmojiClickListener(new OnEmojiClickListener() {
            @Override
            public void onEmojiClick(Emojicon v) {
                InputHelper.input2OSC(mEditContent, v);
            }

            @Override
            public void onDeleteButtonClick(View v) {
                InputHelper.backspace(mEditContent);
            }
        });

        mLayImages.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                keyboardFragment.hideAllKeyBoard();
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick({R.id.iv_picture, R.id.iv_mention, R.id.iv_tag, R.id.iv_emoji})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_picture:
                mLayImages.onLoadMoreClick();
                break;
            case R.id.iv_mention:
                toSelectFriends();
                break;
            case R.id.iv_tag:
                insertTrendSoftware();
                break;
            case R.id.iv_emoji:
                handleEmojiClick(v);
                break;
        }
    }

    /**
     * Emoji 表情点击
     *
     * @param v View
     */
    private void handleEmojiClick(View v) {
        if (!keyboardFragment.isShow()) {
            keyboardFragment.hideSoftKeyboard();
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    keyboardFragment.showEmojiKeyBoard();
                }
            }, 280);
        } else {
            keyboardFragment.hideEmojiKeyBoard();
            keyboardFragment.showSoftKeyboard(mEditContent);
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
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getContext());
            return;
        }
        Intent intent = new Intent(getContext(), SelectFriendsActivity.class);
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
    public List<String> getImagePath() {
        return mLayImages.getPaths();
    }

    @Override
    public void onDestroyView() {
        mLayImages.destroy();
        super.onDestroyView();
    }
}
