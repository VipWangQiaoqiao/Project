package net.oschina.app.improve.behavior;

import android.content.Context;
import android.os.Handler;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;

/**
 * 键盘, emotion按钮, 输入框, emotion面板之间的相互关系委派给这个类管理
 * Created by thanatos on 3/4/16.
 */
public class KeyboardActionDelegation {
    private ImageView mBtnEmotion;
    private EditText mInput;
    private Context mContext;
    private ViewGroup mEmotionPanel;

    private boolean isShowSoftInput;

    // 事件回馈
    private OnActionChangeListener mOnActionChangeListener;

    private KeyboardActionDelegation(Context context, EditText input, ImageView button, ViewGroup view, OnActionChangeListener listener) {
        this.mBtnEmotion = button;
        this.mInput = input;
        this.mContext = context;
        this.mEmotionPanel = view;
        this.mOnActionChangeListener = listener;
        init();
    }

    public static KeyboardActionDelegation delegation(Context context, EditText input, ImageView button, ViewGroup view) {
        return new KeyboardActionDelegation(context, input, button, view, null);
    }


    public static KeyboardActionDelegation delegation(Context context, EditText input, ImageView button, ViewGroup view, OnActionChangeListener listener) {
        return new KeyboardActionDelegation(context, input, button, view, listener);
    }

    /**
     * 初始化, 绑定事件
     */
    private void init() {
        mInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideEmotionPanel();
                } else {
                    hideSoftKeyboard();
                }
            }
        });

        mInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmotionPanelShowing()) return;
                hideEmotionPanel();
            }
        });

        mBtnEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmotionPanelShowing()) {
                    hideEmotionPanel();
                } else {
                    showEmotionPanel();
                }
            }
        });
    }

    public void showEmotionPanel() {
        mBtnEmotion.setSelected(true);
        hideSoftKeyboard();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEmotionPanel.setVisibility(View.VISIBLE);

                OnActionChangeListener listener = mOnActionChangeListener;
                if (listener != null) {
                    listener.onShowEmotionPanel(KeyboardActionDelegation.this);
                }
            }
        }, 300);
    }

    private boolean isEmotionPanelShowing() {
        return mEmotionPanel.getVisibility() == View.VISIBLE;
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftKeyboard() {
        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        isShowSoftInput = false;
    }

    /**
     * 隐藏表情面板
     */
    private void hideEmotionPanel() {
        mEmotionPanel.setVisibility(View.GONE);
        mBtnEmotion.setSelected(false);

        OnActionChangeListener listener = mOnActionChangeListener;
        if (listener != null) {
            listener.onHideEmotionPanel(this);
        }
    }

    public boolean isShowSoftInput() {
        return isShowSoftInput;
    }

    public void onEmotionItemSelected(Emojicon emotion) {
        if (mInput == null || emotion == null) {
            return;
        }
        int start = mInput.getSelectionStart();
        int end = mInput.getSelectionEnd();
        if (start == end) {
            mInput.append(InputHelper.displayEmoji(mContext.getResources(), emotion.getRemote()));
        } else {
            Spannable str = InputHelper.displayEmoji(mContext.getResources(), emotion.getRemote());
            mInput.getText().replace(Math.min(start, end), Math.max(start, end), str, 0, str.length());
        }
    }

    /**
     * 当使用回退键时
     *
     * @return
     */
    public boolean onTurnBack() {
        if (isEmotionPanelShowing()) {
            hideEmotionPanel();
            return false;
        }
        if (isShowSoftInput()) {
            hideEmotionPanel();
            return false;
        }
        return true;
    }

    public interface OnActionChangeListener {
        void onHideEmotionPanel(KeyboardActionDelegation delegation);

        void onShowEmotionPanel(KeyboardActionDelegation delegation);
    }
}
