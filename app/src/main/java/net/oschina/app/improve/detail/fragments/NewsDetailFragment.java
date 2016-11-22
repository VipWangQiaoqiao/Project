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
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.CommentsView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.activities.SoftwareDetailActivity;
import net.oschina.app.improve.detail.contract.NewsDetailContract;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.ui.SelectFriendsActivity;
import net.oschina.app.util.StringUtils;

/**
 * Created by qiujuer
 * on 16/5/26.
 */

public class NewsDetailFragment extends DetailFragment<NewsDetail, NewsDetailContract.View, NewsDetailContract.Operator>
        implements View.OnClickListener, NewsDetailContract.View, OnCommentClickListener {

    private long mId;
    // private TextView mTVAuthorName;
    private TextView mTVPubDate;
    private TextView mTVTitle;
    // private ImageView mIVAuthorPortrait;

    private long mCommentId;
    private long mCommentAuthorId;
    private boolean mInputDoubleEmpty = false;
    private DetailAboutView mAbouts;
    private CommentsView mComments;
    private CoordinatorLayout mLayCoordinator;
    private NestedScrollView mLayContent;
    private TextView mAbhoutSoftwareTitle;
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
        mAbhoutSoftwareTitle = (TextView) root.findViewById(R.id.tv_about_software_title);
        mComments = (CommentsView) root.findViewById(R.id.lay_detail_comment);

        mLayCoordinator = (CoordinatorLayout) root.findViewById(R.id.fragment_blog_detail);
        mLayContent = (NestedScrollView) root.findViewById(R.id.lay_nsv);

        registerScroller(mLayContent, mComments);

        mDelegation = CommentBar.delegation(getActivity(), mLayCoordinator);

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

        mDelegation.getBottomSheet().showSyncView();
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

        mTVName.setText(String.format("%s%s%s%s", "@", newsDetail.getAuthor(), "  ", "发布于 "));

        mTVPubDate.setText(StringUtils.formatSomeAgo(newsDetail.getPubDate()));

        mTVTitle.setText(newsDetail.getTitle());

        toFavoriteOk(newsDetail);

        setText(R.id.tv_info_comment, StringUtils.formatYearMonthDay(newsDetail.getPubDate()));

        Software software = newsDetail.getSoftware();
        if (software != null) {
            mAboutSoftware.setOnClickListener(this);
            mAbhoutSoftwareTitle.setText(software.getName());
        } else {
            mAboutSoftware.setVisibility(View.GONE);
        }

        mAbouts.setAbout(newsDetail.getAbouts(), 6);

        mComments.setTitle(String.format("评论 (%s)", newsDetail.getCommentCount()));
        mComments.init(newsDetail.getId(), OSChinaApi.COMMENT_NEWS, newsDetail.getCommentCount(), getImgLoader(), this);
    }

    private void handleKeyDel() {
        if (mCommentId != mId) {
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
                    mCommentId = mId;
                    mCommentAuthorId = 0;
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


    private void handleFavorite() {
        mOperator.toFavorite();
    }

    private void handleShare() {
        mOperator.toShare();
    }

    private void handleSendComment() {
        mOperator.toSendComment(mId, mCommentId, mCommentAuthorId, mDelegation.getBottomSheet().getCommentText());
        if (mDelegation.getBottomSheet().isSyncToTweet()) {
            About about = new About();
            NewsDetail detail = mOperator.getData();
            about.setId(detail.getId());
            about.setType(detail.getType());
            TweetPublishService.startActionPublish(getActivity(), mDelegation.getBottomSheet().getCommentText(), null, about);
        }
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
        (Toast.makeText(getContext(), "评论成功", Toast.LENGTH_LONG)).show();
        mDelegation.getCommentText().setHint("添加评论");
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().getEditText().setHint("添加评论");
        mComments.addComment(comment, getImgLoader(), this);
        mDelegation.getBottomSheet().dismiss();
    }

    @Override
    public void onClick(View view, Comment comment) {
        mCommentId = comment.getId();

        mCommentAuthorId = comment.getAuthorId();
        mDelegation.getCommentText().setHint(String.format("回复: %s", comment.getAuthor()));

//        mCommentAuthorId = comment.getAuthor().getId();
//        mDelegation.getCommentText().setHint(String.format("%s %s", getResources().getString(R.string.reply_hint), comment.getAuthor().getName()));

        mDelegation.getBottomSheet().show(String.format("回复: %s", comment.getAuthor()));
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
