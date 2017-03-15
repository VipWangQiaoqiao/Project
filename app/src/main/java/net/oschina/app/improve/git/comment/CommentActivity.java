package net.oschina.app.improve.git.comment;

import android.content.Context;
import android.content.Intent;
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

public class CommentActivity extends BaseBackActivity implements CommentContract.Action {
    private CommentPresenter mPresenter;
    protected CommentBar mDelegation;

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
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addComment(mDelegation.getBottomSheet().getCommentText());
            }
        });
    }

    @Override
    public void showAddCommentSuccess(Comment comment, int strId) {
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().dismiss();
    }

    @Override
    public void showAddCommentFailure(int strId) {

    }
}
