package net.oschina.app.improve.detail.fragments;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.NewsDetail;
import net.oschina.app.improve.bean.Software;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.CommentView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.activities.SoftwareDetailActivity;
import net.oschina.app.improve.detail.contract.NewsDetailContract;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.widget.BottomSheetBar;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.ui.SelectFriendsActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;

/**
 * Created by fei
 * on 16/5/26.
 * change by fei
 * on 16/11/17
 * desc:
 */

public class NewsDetailFragment extends DetailFragment<NewsDetail, NewsDetailContract.View, NewsDetailContract.Operator>
        implements View.OnClickListener, NewsDetailContract.View, OnCommentClickListener, BottomSheetBar.OnSyncListener {

    private long mId;
    // private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    // private ImageView mIVAuthorPortrait;

    private long mCommentId;
    private long mCommentAuthorId;
    private boolean mInputDoubleEmpty = false;
    private DetailAboutView mAbouts;
    private CommentView mComment;
    private TextView mAboutSoftwareTitle;
    private LinearLayout mAboutSoftware;
    private TextView mTVName;

    //private KeyboardInputDelegation mDelegation;

    private CommentBar mDelegation;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_news_detail;
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        //mTVAuthorName = (TextView) root.findViewById(R.id.tv_name);
        mTVPubDate = (TextView) root.findViewById(R.id.tv_pub_date);
        mTVTitle = (TextView) root.findViewById(R.id.tv_title);
        mTVName = (TextView) root.findViewById(R.id.tv_info_view);
        mTVName.setOnClickListener(this);

        setGone(R.id.iv_info_view);
        //setGone(R.id.tv_info_view);
        setGone(R.id.iv_info_comment);

        mAbouts = (DetailAboutView) root.findViewById(R.id.lay_detail_about);
        mAboutSoftware = (LinearLayout) root.findViewById(R.id.lay_about_software);
        mAboutSoftwareTitle = (TextView) root.findViewById(R.id.tv_about_software_title);
        mComment = (CommentView) root.findViewById(R.id.lay_detail_comment);

        CoordinatorLayout mLayCoordinator = (CoordinatorLayout) root.findViewById(R.id.fragment_blog_detail);
        NestedScrollView mLayContent = (NestedScrollView) root.findViewById(R.id.lay_nsv);

        registerScroller(mLayContent, mComment);

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
        mDelegation.setShareListener(this);
        mDelegation.getBottomSheet().setFaceListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TDevice.showSoftKeyboard(mDelegation.getBottomSheet().getEditText());
            }
        });
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendComment();
            }
        });

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin())
                    SelectFriendsActivity.show(NewsDetailFragment.this);
                else
                    LoginActivity.show(getActivity());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 相关软件
            case R.id.lay_about_software:
                SoftwareDetailActivity.show(getActivity(), mOperator.getData().getSoftware().getId());
                break;
            case R.id.tv_info_view:
                OtherUserHomeActivity.show(getActivity(), mOperator.getData().getAuthorId());
                break;
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        final NewsDetail newsDetail = mOperator.getData();
        if (newsDetail == null)
            return;

        mId = mCommentId = newsDetail.getId();

        setCommentCount(newsDetail.getCommentCount());
        setBodyContent(newsDetail.getBody());

        mTVName.setText(String.format("%s%s%s%s", "@", newsDetail.getAuthor(), "  ", getResources().getString(R.string.pub_about) + " "));

        mTVPubDate.setText(StringUtils.formatSomeAgo(newsDetail.getPubDate()));

        mTVTitle.setText(newsDetail.getTitle());

        toFavoriteOk(newsDetail);

        setText(R.id.tv_info_comment, StringUtils.formatYearMonthDay(newsDetail.getPubDate()));

        Software software = newsDetail.getSoftware();
        if (software != null) {
            mAboutSoftware.setOnClickListener(this);
            mAboutSoftwareTitle.setText(software.getName());
        } else {
            mAboutSoftware.setVisibility(View.GONE);
        }

        mAbouts.setAbout(newsDetail.getAbouts(), 6);

        mComment.setTitle(String.format("%s (%s)", getResources().getString(R.string.hot_comment_hint), newsDetail.getCommentCount()));
        mComment.init(newsDetail.getId(), OSChinaApi.COMMENT_NEWS, getImgLoader(), this);
    }

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
    public void toFavoriteOk(NewsDetail newsDetail) {
        if (newsDetail.isFavorite())
            mDelegation.setFavDrawable(R.drawable.ic_faved);
        else
            mDelegation.setFavDrawable(R.drawable.ic_fav);
    }

    @Override
    public void toSendCommentOk(Comment comment) {
        (Toast.makeText(getContext(), getString(R.string.pub_comment_success), Toast.LENGTH_LONG)).show();
        mDelegation.getCommentText().setHint(getString(R.string.add_comment_hint));
        mComment.addComment(comment, getImgLoader(), this);
        mDelegation.getBottomSheet().dismiss();
    }

    @Override
    public void onClick(View view, Comment comment) {
        mCommentId = comment.getId();
        mCommentAuthorId = comment.getAuthor().getId();
        mDelegation.getCommentText().setHint(String.format("%s %s", getResources().getString(R.string.reply_hint), comment.getAuthor().getName()));
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
        if (isSync)
            handleShare();
    }
}
