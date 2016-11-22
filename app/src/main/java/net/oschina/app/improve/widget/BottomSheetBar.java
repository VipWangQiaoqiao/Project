package net.oschina.app.improve.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import net.oschina.app.R;
import net.oschina.app.emoji.Emojicon;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.emoji.OnEmojiClickListener;
import net.oschina.app.improve.emoji.EmojiView;
import net.oschina.app.util.TDevice;

/**
 * 底部弹出评论框
 * Created by haibin
 * on 2016/11/10.
 */
@SuppressWarnings("unused")
public class BottomSheetBar {
    private View mRootView;
    private EditText mEditText;
    private ImageButton mAtView;
    private ImageButton mFaceView;
    private CheckBox mSyncToTweetView;
    private Context mContext;
    private Button mBtnCommit;
    private Dialog mDialog;
    private FrameLayout mFrameLayout;
    private EmojiView mEmojiView;

    private BottomSheetBar(Context context) {
        this.mContext = context;
    }

    public static BottomSheetBar delegation(Context context) {
        BottomSheetBar bar = new BottomSheetBar(context);
        bar.mRootView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_comment_bar, null);
        bar.initView();
        return bar;
    }

    private void initView() {
        mFrameLayout = (FrameLayout) mRootView.findViewById(R.id.fl_face);
        mEditText = (EditText) mRootView.findViewById(R.id.et_comment);
        mAtView = (ImageButton) mRootView.findViewById(R.id.ib_mention);
        mFaceView = (ImageButton) mRootView.findViewById(R.id.ib_face);
        mFaceView.setVisibility(View.GONE);
        mSyncToTweetView = (CheckBox) mRootView.findViewById(R.id.cb_sync);
        mBtnCommit = (Button) mRootView.findViewById(R.id.btn_comment);
        mBtnCommit.setEnabled(false);

        mDialog = new Dialog(mContext, R.style.Comment_Dialog);
        mDialog.setContentView(mRootView);
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.FILL);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mFrameLayout.setVisibility(View.GONE);
            }
        });

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TDevice.closeKeyboard(mEditText);
                dismiss();
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mBtnCommit.setEnabled(s.length() > 0);
            }
        });
    }

    public void showEmoji() {
        mFaceView.setVisibility(View.VISIBLE);
        mFaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiView == null) {
                    mEmojiView = new EmojiView(mContext, mEditText);
                    mEmojiView.setListener(new OnEmojiClickListener() {
                        @Override
                        public void onDeleteButtonClick(View v) {
                            InputHelper.backspace(mEditText);
                        }

                        @Override
                        public void onEmojiClick(Emojicon v) {

                        }
                    });
                    mFrameLayout.addView(mEmojiView);
                }

                TDevice.closeKeyboard(mEditText);
                mFrameLayout.setVisibility(View.VISIBLE);

            }
        });

        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrameLayout.setVisibility(View.GONE);
            }
        });
    }

    public void show(String hint) {
        mDialog.show();
        if (!"添加评论".equals(hint)) {
            mEditText.setHint(hint + " ");
        }
        //Selection.setSelection(mEditText.getText(), mEditText.length());
        mRootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                TDevice.showSoftKeyboard(mEditText);
            }
        }, 50);
    }

    public void dismiss() {
        TDevice.closeKeyboard(mEditText);
        mDialog.dismiss();
    }

    public void setMentionListener(View.OnClickListener listener) {
        mAtView.setOnClickListener(listener);
    }

    public void setFaceListener(View.OnClickListener listener) {
        mFaceView.setOnClickListener(listener);
    }

    public void showSyncView() {
        mSyncToTweetView.setVisibility(View.INVISIBLE); //hide for temp
        mRootView.findViewById(R.id.tv_sync).setVisibility(View.INVISIBLE); //hide for temp
    }

    public boolean isSyncToTweet() {
        return mSyncToTweetView.isChecked();
    }

    public void setCommitListener(View.OnClickListener listener) {
        mBtnCommit.setOnClickListener(listener);
    }

    public void handleSelectFriendsResult(Intent data) {
        String names[] = data.getStringArrayExtra("names");
        if (names != null && names.length > 0) {
            String text = "";
            for (String n : names) {
                text += "@" + n + " ";
            }
            mEditText.getText().insert(mEditText.getSelectionEnd(), text);
        }
    }

    public EditText getEditText() {
        return mEditText;
    }

    public String getCommentText() {
        return mEditText.getText().toString().trim();
    }

    public Button getBtnCommit() {
        return mBtnCommit;
    }
}
