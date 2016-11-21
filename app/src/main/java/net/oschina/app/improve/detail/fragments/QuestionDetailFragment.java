package net.oschina.app.improve.detail.fragments;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.QuestionDetail;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.CommentView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.contract.QuestionDetailContract;
import net.oschina.app.improve.widget.BottomSheetBar;
import net.oschina.app.improve.widget.FlowLayout;
import net.oschina.app.ui.SelectFriendsActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;

import java.util.List;

/**
 * Created by fei
 * on 16/5/26.
 * desc:
 */

public class QuestionDetailFragment extends DetailFragment<QuestionDetail, QuestionDetailContract.View, QuestionDetailContract.Operator>
        implements View.OnClickListener, QuestionDetailContract.View, OnCommentClickListener, BottomSheetBar.OnSyncListener {
    private long mId;
    private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;

    private long mCommentId;
    private long mCommentAuthorId;

    private FlowLayout mFlowLayout;

    private CommentBar mDelegation;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_question_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mTVAuthorName = (TextView) root.findViewById(R.id.tv_ques_detail_author);
        mTVPubDate = (TextView) root.findViewById(R.id.tv_ques_detail_pub_date);
        mTVTitle = (TextView) root.findViewById(R.id.tv_ques_detail_title);

        mFlowLayout = (FlowLayout) root.findViewById(R.id.ques_detail_flow);

        CommentView mComments = (CommentView) root.findViewById(R.id.lay_detail_comment);
        mComments.setVisibility(View.GONE);

        CoordinatorLayout mLayCoordinator = (CoordinatorLayout) root.findViewById(R.id.activity_blog_detail);

        mDelegation = CommentBar.delegation(getActivity(), mLayCoordinator);
        mDelegation.setOnSyncListener(this);

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });

        mDelegation.setFavListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFavorite();
            }
        });
        mDelegation.setShareListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleShare();
            }
        });
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendComment();
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

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin())
                    SelectFriendsActivity.show(QuestionDetailFragment.this);
                else
                    LoginActivity.show(getActivity());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 评论列表
            case R.id.tv_see_more_comment:
                UIHelper.showBlogComment(getActivity(), (int) mId, (int) mOperator.getData().getAuthorId());
                break;
        }
    }

    @Override
    protected void initData() {

        QuestionDetail questionDetail = mOperator.getData();
        if (questionDetail == null)
            return;

        mId = mCommentId = questionDetail.getId();

        String body = questionDetail.getBody();

        setCommentCount(questionDetail.getCommentCount());
        setBodyContent(body);

        String author = questionDetail.getAuthor();
        if (!TextUtils.isEmpty(author))
            mTVAuthorName.setText(author);

        List<String> tags = questionDetail.getTags();

        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                TextView tvTag = (TextView) getActivity().getLayoutInflater().inflate(R.layout.flowlayout_item, mFlowLayout, false);
                if (!TextUtils.isEmpty(tag))
                    tvTag.setText(tag);
                mFlowLayout.addView(tvTag);
            }
        }

        mTVPubDate.setText(StringUtils.formatSomeAgo(questionDetail.getPubDate()));

        String title = questionDetail.getTitle();
        if (!TextUtils.isEmpty(title))
            mTVTitle.setText(title);

        toFavoriteOk(questionDetail);

        setText(R.id.tv_info_view, String.valueOf(questionDetail.getViewCount()));
        setText(R.id.tv_info_comment, String.valueOf(questionDetail.getCommentCount()));

    }

    private boolean mInputDoubleEmpty = false;

    private void handleKeyDel() {
        if (mCommentId != mId) {
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
                    mCommentId = mId;
                    mCommentAuthorId = 0;
                    mDelegation.setCommentHint(getString(R.string.pub_comment_hint));
                    mDelegation.getBottomSheet().getEditText().setHint(getString(R.string.pub_comment_hint));
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
        }
    }


    private void handleFavorite() {
        mOperator.toFavorite();
    }

    private void handleShare() {
        mOperator.toShare();
    }

    private void handleSendComment() {
        mOperator.toSendComment(mId, mCommentId, mCommentAuthorId, mDelegation.getBottomSheet().getCommentText());
    }


    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(QuestionDetail questionDetail) {
        if (questionDetail.isFavorite())
            mDelegation.setFavDrawable(R.drawable.ic_faved);
        else
            mDelegation.setFavDrawable(R.drawable.ic_fav);
    }

    @Override
    public void toSendCommentOk(Comment comment) {
        (Toast.makeText(getContext(), getString(R.string.pub_comment_success), Toast.LENGTH_LONG)).show();
        mDelegation.setCommentHint(getString(R.string.add_comment_hint));
        mDelegation.getBottomSheet().dismiss();
    }

    @Override
    public void onClick(View view, Comment comment) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            mDelegation.getBottomSheet().handleSelectFriendsResult(data);
            mDelegation.setCommentHint(mDelegation.getBottomSheet().getEditText().getHint().toString());
        }
    }

    @Override
    public void sync(boolean isSync) {
        // if (isSync)

    }
}
