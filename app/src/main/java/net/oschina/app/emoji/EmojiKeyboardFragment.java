package net.oschina.app.emoji;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.emoji.SoftKeyboardStateHelper.SoftKeyboardStateListener;
import net.oschina.app.improve.widget.TitleBar;
import net.oschina.app.util.TDevice;

/**
 * 表情选择界面
 */
public class EmojiKeyboardFragment extends Fragment implements
        SoftKeyboardStateListener {

    private ViewPager mEmojiPager;
    private LinearLayout mRootView;
    private OnEmojiClickListener listener;
    public int EMOJI_TAB_CONTENT;

    private boolean isDelegate = false;
    private int keyboardHeightInPx = -1;
    private int keyboardMinHeightInPx;
    private int keyboardMaxHeightInPx;
    private boolean isClipStatusHeight = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != mRootView) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        } else {
            mRootView = (LinearLayout) inflater.inflate(R.layout.frag_keyboard, container, false);
            initWidget(mRootView);
        }

        intKeyboardHeight();
        return mRootView;
    }

    private void intKeyboardHeight() {
        // init keyboard min and max height
        keyboardMinHeightInPx = (int) TDevice.dipToPx(getResources(), isClipStatusHeight ? 176 : 151);
        keyboardMaxHeightInPx = (int) TDevice.dipToPx(getResources(), 254);
    }

    private void initWidget(View rootView) {
        // bottom
        ViewGroup mEmojiBottom = (ViewGroup) rootView.findViewById(R.id.emoji_bottom);
        EMOJI_TAB_CONTENT = mEmojiBottom.getChildCount() - 1; // 减一是因为有一个删除按钮
        View[] mEmojiTabs = new View[EMOJI_TAB_CONTENT];
        if (EMOJI_TAB_CONTENT <= 1) { // 只有一个分类的时候就不显示了
            mEmojiBottom.setVisibility(View.GONE);
        }
        for (int i = 0; i < EMOJI_TAB_CONTENT; i++) {
            mEmojiTabs[i] = mEmojiBottom.getChildAt(i);
            mEmojiTabs[i].setOnClickListener(getBottomBarClickListener(i));
        }
        mEmojiBottom.findViewById(R.id.btn_del).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onDeleteButtonClick(v);
                        }
                    }
                });

        // content必须放在bottom下面初始化
        mEmojiPager = (ViewPager) rootView.findViewById(R.id.emoji_pager);
        EmojiPagerAdapter adapter = new EmojiPagerAdapter(getChildFragmentManager(), EMOJI_TAB_CONTENT, new OnEmojiClickListener() {
            @Override
            public void onDeleteButtonClick(View v) {
                if (listener != null) {
                    listener.onDeleteButtonClick(v);
                }
            }

            @Override
            public void onEmojiClick(Emojicon v) {
                if (listener != null) {
                    listener.onEmojiClick(v);
                }
            }
        });
        mEmojiPager.setAdapter(adapter);

        SoftKeyboardStateHelper mKeyboardHelper = new SoftKeyboardStateHelper(getActivity().getWindow().getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
    }

    /**
     * 底部栏点击事件监听器
     */
    private OnClickListener getBottomBarClickListener(final int index) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmojiPager.setCurrentItem(index);
            }
        };
    }

    public void setOnEmojiClickListener(OnEmojiClickListener l) {
        this.listener = l;
    }

    public boolean isShow() {
        return mRootView.getVisibility() == View.VISIBLE;
    }

    /**
     * 隐藏Emoji并显示软键盘
     */
    public void hideEmojiKeyBoard() {
        if (!isDelegate) {
            mRootView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示Emoji并隐藏软键盘
     */
    public void showEmojiKeyBoard() {
        int height = keyboardHeightInPx;
        height = Math.max(height, keyboardMinHeightInPx);
        height = isClipStatusHeight
                ? height - TitleBar.getExtPaddingTop(mRootView.getResources())
                : height;
        if (height > 0)
            height = Math.min(height, keyboardMaxHeightInPx);

        ViewGroup.LayoutParams params = mRootView.getLayoutParams();
        params.height = height;
        mRootView.requestLayout();

        mRootView.setVisibility(View.VISIBLE);
    }

    /**
     * 当软键盘显示时回调
     */
    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        this.keyboardHeightInPx = keyboardHeightInPx;
        hideEmojiKeyBoard();
    }

    @Override
    public void onSoftKeyboardClosed() {
    }

    public void setClipStatusHeight(boolean clipStatusHeight) {
        isClipStatusHeight = clipStatusHeight;
        intKeyboardHeight();
    }

    public void setDelegate(boolean delegate) {
        isDelegate = delegate;
    }
}
