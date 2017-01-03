package net.oschina.app.improve.detail.fragments;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.TranslationDetail;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.behavior.FloatingAutoHideDownBehavior;
import net.oschina.app.improve.comment.CommentView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.contract.TranslateDetailContract;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.ui.SelectFriendsActivity;
import net.oschina.app.util.StringUtils;

/**
 * Created by fei
 * on 16/06/28.
 * Change by fei
 * on 16/11/17
 * desc: translation detail
 */

public class TranslationDetailFragment extends DetailFragment<TranslationDetail,
        TranslateDetailContract.View, TranslateDetailContract.Operator>
        implements TranslateDetailContract.View, OnCommentClickListener {

    private long mId;
    private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    private ImageView mIVAuthorPortrait;

    private long mCommentId;
    private long mCommentAuthorId;
    private boolean mInputDoubleEmpty = false;
    private DetailAboutView mAbouts;
    private CoordinatorLayout mLayCoordinator;
    private NestedScrollView mLayContent;
    private View mLayBottom;
    private LinearLayout mAboutSoftware;

    private CommentBar mDelegation;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_news_detail;
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mTVAuthorName = (TextView) root.findViewById(R.id.tv_name);
        mTVPubDate = (TextView) root.findViewById(R.id.tv_pub_date);
        mTVTitle = (TextView) root.findViewById(R.id.tv_title);

        setGone(R.id.iv_info_view);
        setGone(R.id.tv_info_view);
        setGone(R.id.iv_info_comment);

        mIVAuthorPortrait = (ImageView) root.findViewById(R.id.iv_avatar);
        mAbouts = (DetailAboutView) root.findViewById(R.id.lay_detail_about);
        mAboutSoftware = (LinearLayout) root.findViewById(R.id.lay_about_software);
        CommentView mComments = (CommentView) root.findViewById(R.id.lay_detail_comment);
        mComments.setVisibility(View.GONE);

        mLayCoordinator = (CoordinatorLayout) root.findViewById(R.id.fragment_blog_detail);
        mLayContent = (NestedScrollView) root.findViewById(R.id.lay_nsv);

        mLayBottom = root.findViewById(R.id.lay_option);

        mDelegation = CommentBar.delegation(getActivity(), mLayCoordinator);

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

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin())
                    SelectFriendsActivity.show(TranslationDetailFragment.this);
                else
                    LoginActivity.show(getActivity());
            }
        });

    }

    @Override
    protected void initData() {
        final TranslationDetail translationDetail = mOperator.getData();
        if (translationDetail == null)
            return;

        mId = mCommentId = translationDetail.getId();

        setCommentCount(translationDetail.getCommentCount());
        setBodyContent(translationDetail.getBody());

        mTVAuthorName.setText(translationDetail.getAuthor());
        getImgLoader().load(translationDetail.getAuthorPortrait()).error(R.mipmap.widget_dface).into(mIVAuthorPortrait);

        mTVPubDate.setText(StringUtils.formatSomeAgo(translationDetail.getPubDate()));

        mTVTitle.setText(translationDetail.getTitle());

        toFavoriteOk(translationDetail);

        setText(R.id.tv_info_comment, translationDetail.getPubDate());

        mAboutSoftware.setVisibility(View.GONE);
        mAbouts.setVisibility(View.GONE);
    }

    private void handleKeyDel() {
        if (mCommentId != mId) {
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
                    mCommentId = mId;
                    mCommentAuthorId = 0;
                    mDelegation.setCommentHint(getResources().getString(R.string.pub_comment_hint));
                    mDelegation.getBottomSheet().getEditText().setHint(getResources().getString(R.string.pub_comment_hint));
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
    public void toFavoriteOk(TranslationDetail translationDetail) {
        if (translationDetail.isFavorite())
            mDelegation.setFavDrawable(R.drawable.ic_faved);
        else
            mDelegation.setFavDrawable(R.drawable.ic_fav);
    }

    @Override
    public void toSendCommentOk(Comment comment) {
        if (mDelegation.getBottomSheet().isSyncToTweet()) {
            TranslationDetail detail = mOperator.getData();
            if (detail == null) return;
            TweetPublishService.startActionPublish(getActivity(),
                    mDelegation.getBottomSheet().getCommentText(), null,
                    About.buildShare(detail.getId(), OSChinaApi.COMMENT_TRANSLATION));
        }
        Toast.makeText(getContext(), getResources().getString(R.string.pub_comment_success), Toast.LENGTH_SHORT).show();
        mDelegation.setCommentHint(getResources().getString(R.string.add_comment_hint));
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().getEditText().setHint(getResources().getString(R.string.add_comment_hint));
        // mComments.addComment(comment, getImgLoader(), this);
        mDelegation.getBottomSheet().dismiss();
    }

    @Override
    public void onClick(View view, Comment comment) {
        FloatingAutoHideDownBehavior.showBottomLayout(mLayCoordinator, mLayContent, mLayBottom);
        mCommentId = comment.getId();


        mCommentAuthorId = comment.getAuthor().getId();
        mDelegation.setCommentHint(String.format("%s %s", getResources().getString(R.string.reply_hint),
                comment.getAuthor().getName()));

        mDelegation.getBottomSheet().show(String.format("%s %s", getResources().getString(R.string.reply_hint),
                comment.getAuthor().getName()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            mDelegation.getBottomSheet().handleSelectFriendsResult(data);
            mDelegation.setCommentHint(mDelegation.getBottomSheet().getEditText().getHint().toString());
        }
    }
}
