package net.oschina.app.improve.detail.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.BlogDetail;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.CommentsView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.contract.BlogDetailContract;
import net.oschina.app.improve.pay.bean.Order;
import net.oschina.app.improve.pay.dialog.RewardDialog;
import net.oschina.app.improve.pay.util.RewardUtil;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.ui.SelectFriendsActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;

import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by qiujuer
 * on 16/5/26.
 */

@SuppressWarnings("WeakerAccess")
public class BlogDetailFragment
        extends DetailFragment<BlogDetail, BlogDetailContract.View, BlogDetailContract.Operator>
        implements BlogDetailContract.View, View.OnClickListener, OnCommentClickListener {

    private long mId;
    private long mCommentId;
    private long mCommentAuthorId;

    @Bind(R.id.tv_name)
    TextView mTVAuthorName;
    @Bind(R.id.tv_pub_date)
    TextView mTVPubDate;
    @Bind(R.id.tv_title)
    TextView mTVTitle;

    @Bind(R.id.tv_blog_detail_abstract)
    TextView mTVAbstract;
    @Bind(R.id.iv_label_recommend)
    ImageView mIVLabelRecommend;
    @Bind(R.id.iv_label_originate)
    ImageView mIVLabelOriginate;
    @Bind(R.id.iv_avatar)
    ImageView mIVAuthorPortrait;

    @Bind(R.id.btn_relation)
    Button mBtnRelation;

    @Bind(R.id.lay_detail_about)
    DetailAboutView mAbouts;
    @Bind(R.id.lay_detail_comment)
    CommentsView mComments;
    @Bind(R.id.lay_blog_detail_abstract)
    LinearLayout mLayAbstract;

    @Bind(R.id.fragment_blog_detail)
    CoordinatorLayout mLayCoordinator;
    @Bind(R.id.lay_nsv)
    NestedScrollView mLayContent;

    private Dialog mWaitDialog;

    private CommentBar mDelegation;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_general_blog_detail;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        mAbouts.setTitle(getString(R.string.label_about_title));
        registerScroller(mLayContent, mComments);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBtnRelation.setElevation(0);
        }

        mDelegation = CommentBar.delegation(getActivity(), mLayCoordinator);

        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendComment();
            }
        });

        mDelegation.getBottomSheet().showSyncView();

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
                    SelectFriendsActivity.show(BlogDetailFragment.this);
                else
                    LoginActivity.show(getActivity());
            }
        });
    }

    @OnClick({R.id.btn_relation, R.id.iv_avatar, R.id.btn_reward})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 关注按钮
            case R.id.btn_relation:
                handleRelation();
                break;
            // 评论列表
            case R.id.tv_see_more_comment:
                UIHelper.showBlogComment(getActivity(), (int) mId,
                        (int) mOperator.getData().getAuthorId());
                break;
            case R.id.iv_avatar:
                OtherUserHomeActivity.show(getActivity(), mOperator.getData().getAuthorId());
                break;
            case R.id.btn_reward:
                handleReward();
            default:
                break;

        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initData() {
        BlogDetail blog = mOperator.getData();
        if (blog == null)
            return;

        mId = mCommentId = blog.getId();

        setCommentCount(blog.getCommentCount());
        setBodyContent(blog.getBody());

        mTVAuthorName.setText(blog.getAuthor());
        getImgLoader().load(blog.getAuthorPortrait()).error(R.mipmap.widget_dface).into(mIVAuthorPortrait);
        mIVAuthorPortrait.setOnClickListener(this);


        mTVPubDate.setText(StringUtils.formatSomeAgo(blog.getPubDate()));

        mTVTitle.setText(blog.getTitle());

        if (TextUtils.isEmpty(blog.getAbstract())) {
            mLayAbstract.setVisibility(View.GONE);
        } else {
            mTVAbstract.setText(blog.getAbstract());
            mLayAbstract.setVisibility(View.VISIBLE);
        }

        mIVLabelRecommend.setVisibility(blog.isRecommend() ? View.VISIBLE : View.GONE);
        mIVLabelOriginate.setImageDrawable(blog.isOriginal() ?
                getResources().getDrawable(R.mipmap.ic_label_originate) :
                getResources().getDrawable(R.mipmap.ic_label_reprint));

        toFollowOk(blog);
        toFavoriteOk(blog);

        setText(R.id.tv_info_view, String.valueOf(blog.getViewCount()));
        setText(R.id.tv_info_comment, String.valueOf(blog.getCommentCount()));

        mAbouts.setAbout(blog.getAbouts(), 3);

        mComments.setTitle(String.format("评论 (%s)", blog.getCommentCount()));
        mComments.init(blog.getId(), OSChinaApi.COMMENT_BLOG, blog.getCommentCount(), getImgLoader(), this);
    }

    private boolean mInputDoubleEmpty = false;

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

    private void handleRelation() {
        mOperator.toFollow();
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
            BlogDetail detail = mOperator.getData();
            about.setId(detail.getId());
            about.setType(detail.getType());
            TweetPublishService.startActionPublish(getActivity(), mDelegation.getBottomSheet().getCommentText(), null, about);
        }
    }

    private void handleReward() {
        final BlogDetail detail = mOperator.getData();

        final RewardDialog dialog = new RewardDialog(getContext());
        dialog.setCancelable(true);
        dialog.setPortrait(detail.getAuthorPortrait());
        dialog.setNick(detail.getAuthor());
        dialog.setOnClickRewardListener(new RewardDialog.OnClickRewardCallback() {
            @Override
            public void reward(float cast) {
                User user = AccountHelper.getUser();
                if (user == null || user.getId() <= 0) {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, String> pairs = new ConcurrentHashMap<>();
                pairs.put("objType", "16344358");
                pairs.put("objId", String.valueOf(detail.getId()));
                pairs.put("attach", Order.TYPE_ALIPAY);
                pairs.put("money", String.valueOf((int) (cast * 100)));
                pairs.put("subject", detail.getTitle());
                pairs.put("donater", String.valueOf(user.getId()));
                pairs.put("author", String.valueOf(detail.getAuthorId()));
                pairs.put("message", "Hello");
                pairs.put("returnUrl", URLEncoder.encode(detail.getHref()));
                pairs.put("notifyUrl", URLEncoder.encode(detail.getNotifyUrl()));

                String sign = RewardUtil.sign(pairs);
                pairs.put("sign", sign);

                mWaitDialog = DialogHelper.getProgressDialog(getContext(), "正在提交数据", false);
                mWaitDialog.setCancelable(false);

                OSChinaApi.reward(pairs, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                        TLog.e("oschina", "response body: " + responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                        TLog.e("oschina", "onFailure");
                        error.toString();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void toFavoriteOk(BlogDetail blogDetail) {
        if (blogDetail.isFavorite())
            mDelegation.setFavDrawable(R.drawable.ic_faved);
        else
            mDelegation.setFavDrawable(R.drawable.ic_fav);
    }

    @Override
    public void toFollowOk(BlogDetail blogDetail) {
        if (blogDetail.getAuthorRelation() <= 2) {
            mBtnRelation.setText("已关注");
        } else {
            mBtnRelation.setText("关注");
        }
    }

    @Override
    public void toSendCommentOk(Comment comment) {
        (Toast.makeText(getContext(), "评论成功", Toast.LENGTH_LONG)).show();
        mDelegation.setCommentHint("添加评论");
        mDelegation.getBottomSheet().getEditText().setText("");
        mDelegation.getBottomSheet().getEditText().setHint("添加评论");
        mComments.addComment(comment, getImgLoader(), this);
        mDelegation.getBottomSheet().dismiss();
    }

    @Override
    public void onClick(View view, Comment comment) {
        mCommentId = comment.getId();

        mCommentAuthorId = comment.getAuthorId();
        mDelegation.setCommentHint(String.format("回复: %s", comment.getAuthor()));
        mDelegation.getBottomSheet().show(String.format("回复: %s", comment.getAuthor()));

//
//        mCommentAuthorId = comment.getAuthor().getId();
//        mDelegation.setCommentHint(String.format("%s %s", getResources().getString(R.string.reply_hint), comment.getAuthor().getName()));
//
//        mDelegation.getBottomSheet().show(String.format("%s %s", getResources().getString(R.string.reply_hint), comment.getAuthor().getName()));


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
