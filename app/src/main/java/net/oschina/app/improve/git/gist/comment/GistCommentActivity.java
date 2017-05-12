package net.oschina.app.improve.git.gist.comment;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.git.bean.Comment;
import net.oschina.app.improve.git.bean.Gist;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;

/**
 * 代码片段评论
 * Created by haibin on 2017/5/11.
 */

public class GistCommentActivity extends BaseBackActivity implements GistCommentContract.Action, View.OnClickListener {
    private GistCommentPresenter mPresenter;
    protected CommentBar mDelegation;
    private String mMentionStr = "";
    protected boolean mInputDoubleEmpty = false;

    public static void show(Context context, Gist gist) {
        Intent intent = new Intent(context, GistCommentActivity.class);
        intent.putExtra("gist", gist);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gist_comment;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        final Gist gist = (Gist) getIntent().getSerializableExtra("gist");
        GistCommentFragment fragment = GistCommentFragment.newInstance();
        addFragment(R.id.fl_content, fragment);
        mPresenter = new GistCommentPresenter(fragment, this, gist);
        LinearLayout layComment = (LinearLayout) findViewById(R.id.ll_comment);
        mDelegation = CommentBar.delegation(this, layComment);
        mDelegation.hideFav();
        mDelegation.hideShare();
        mDelegation.getBottomSheet().hideSyncAction();
        mDelegation.getBottomSheet().hideMentionAction();
        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((AccountHelper.isLogin())) {
                    UserSelectFriendsActivity.show(GistCommentActivity.this, mDelegation.getBottomSheet().getEditText());
                } else {
                    LoginActivity.show(GistCommentActivity.this, 1);
                }
            }
        });
        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addComment(mMentionStr + mDelegation.getBottomSheet().getCommentText());
            }
        });
    }

    @Override
    public void onClick(View v) {
        Comment comment = (Comment) v.getTag();
        mMentionStr = "回复 @" + comment.getAuthor().getName() + ":";
        mDelegation.getBottomSheet().show(mMentionStr);
    }

    @Override
    public void showAddCommentSuccess(Comment comment, int strId) {
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().getEditText().setHint("发表评论");
        mMentionStr = "";
        mDelegation.getBottomSheet().dismiss();
    }

    @Override
    public void showAddCommentFailure(int strId) {

    }

    private void handleKeyDel() {
        if (!TextUtils.isEmpty(mMentionStr)) {
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
                    mMentionStr = "";
                    mDelegation.setCommentHint("发表评论");
                    mDelegation.getBottomSheet().getEditText().setHint("发表评论");
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
        }
    }
}
