package net.oschina.app.improve.git.comment;

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
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;

/**
 * Created by haibin
 * on 2017/3/14.
 */

public class CommentActivity extends BaseBackActivity implements CommentContract.Action, View.OnClickListener {
    private CommentPresenter mPresenter;
    protected CommentBar mDelegation;
    private String mMentionStr = "";
    protected boolean mInputDoubleEmpty = false;

    public static void show(Context context, Project project) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("project", project);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_git_comment;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        CommentFragment fragment = CommentFragment.newInstance();
        final Project project = (Project) getIntent()
                .getExtras()
                .getSerializable("project");
        addFragment(R.id.fl_content, fragment);
        mPresenter = new CommentPresenter(fragment, this, project);
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
                    UserSelectFriendsActivity.show(CommentActivity.this, mDelegation.getBottomSheet().getEditText());
                } else {
                    LoginActivity.show(CommentActivity.this, 1);
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

    protected void handleKeyDel() {
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
