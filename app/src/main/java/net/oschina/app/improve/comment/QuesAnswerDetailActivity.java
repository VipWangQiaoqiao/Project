package net.oschina.app.improve.comment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.comment.Reply;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.tweet.adapter.TweetCommentAdapter;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapter;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by thanatos
 * on 16/6/16.
 * change by fie
 * on 16/11/17
 * desc:问答,活动的评论详情（相当于帖子）,可以对评论进行顶踩操作
 */
public class QuesAnswerDetailActivity extends BaseBackActivity {

    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String BUNDLE_ARTICLE_KEY = "BUNDLE_ARTICLE_KEY";
    public static final String BUNDLE_TYPE = "bundle_comment_type";

    @Bind(R.id.iv_portrait)
    CircleImageView ivPortrait;
    @Bind(R.id.tv_nick)
    TextView tvNick;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.iv_vote_up)
    ImageView ivVoteUp;
    @Bind(R.id.iv_vote_down)
    ImageView ivVoteDown;
    @Bind(R.id.tv_up_count)
    TextView tvVoteCount;
    @Bind(R.id.webview)
    OWebView mWebView;
    @Bind(R.id.tv_comment_count)
    TextView tvCmnCount;
    @Bind(R.id.layout_container)
    LinearLayout mLayoutContainer;
    @Bind(R.id.layout_coordinator)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.layout_scroll)
    NestedScrollView mScrollView;

    private long sid;
    private Comment comment;
    private int mType;

    private Dialog mVoteDialog;
    private Dialog mWaitingDialog;
    private Reply reply;
    private View mVoteDialogView;
    private List<Reply> replies = new ArrayList<>();

    private CommentBar mDelegation;
    private TextHttpResponseHandler onSendCommentHandler;
    private View.OnClickListener onReplyButtonClickListener;

    /**
     * @param context context
     * @param comment comment
     * @param sid     文章id
     */
    public static void show(Context context, Comment comment, long sid, int type) {
        Intent intent = new Intent(context, QuesAnswerDetailActivity.class);
        intent.putExtra(BUNDLE_KEY, comment);
        intent.putExtra(BUNDLE_ARTICLE_KEY, sid);
        intent.putExtra(BUNDLE_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        comment = (Comment) getIntent().getSerializableExtra(BUNDLE_KEY);
        sid = getIntent().getLongExtra(BUNDLE_ARTICLE_KEY, 0);
        mType = getIntent().getIntExtra(BUNDLE_TYPE, OSChinaApi.COMMENT_QUESTION);
        return !(comment == null || comment.getId() <= 0) && super.initBundle(bundle);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_post_answer_detail;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.back_hint);
        }
    }

    @SuppressWarnings("deprecation")
    protected void initWidget() {
        // portrait
        if (TextUtils.isEmpty(comment.getAuthor().getPortrait())) {
            ivPortrait.setImageResource(R.mipmap.widget_default_face);
        } else {
            getImageLoader()
                    .load(comment.getAuthor().getPortrait())
                    .asBitmap()
                    .placeholder(getResources().getDrawable(R.mipmap.widget_default_face))
                    .error(getResources().getDrawable(R.mipmap.widget_default_face))
                    .into(ivPortrait);
        }

        // nick
        tvNick.setText(comment.getAuthor().getName());

        // publish time
        if (!TextUtils.isEmpty(comment.getPubDate()))
            tvTime.setText(StringUtils.formatSomeAgo(comment.getPubDate()));

        // vote state
        switch (comment.getVoteState()) {
            case Comment.VOTE_STATE_UP:
                ivVoteUp.setSelected(true);
                break;
            case Comment.VOTE_STATE_DOWN:
                ivVoteDown.setSelected(true);
        }

        // vote count
        tvVoteCount.setText(String.valueOf(comment.getVote()));

        tvCmnCount.setText("评论 (" + (comment.getReply() == null ? 0 : comment.getReply().length) + ")");

        mDelegation = CommentBar.delegation(this, mCoordinatorLayout);

        mDelegation.setCommentHint("我要回答");
        mDelegation.getBottomSheet().getEditText().setHint("我要回答");
        mDelegation.getBottomSheet().getEditText().setText("");

        mDelegation.hideFav();
        mDelegation.hideShare();

        mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapter(this));

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin()) {
                    Intent intent = new Intent(QuesAnswerDetailActivity.this, UserSelectFriendsActivity.class);
                    startActivityForResult(intent, TweetPublishFragment.REQUEST_CODE_SELECT_FRIENDS);
                } else
                    LoginActivity.show(QuesAnswerDetailActivity.this);
            }
        });

        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mDelegation.getBottomSheet().getCommentText();
                if (TextUtils.isEmpty(content.replaceAll("[ \\s\\n]+", ""))) {
                    Toast.makeText(QuesAnswerDetailActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AccountHelper.isLogin()) {
                    UIHelper.showLoginActivity(QuesAnswerDetailActivity.this);
                    return;
                }

                mWaitingDialog = DialogHelper.getProgressDialog(QuesAnswerDetailActivity.this, "正在发表评论...", false);
                mWaitingDialog.show();

                OSChinaApi.publishComment(sid, -1, comment.getId(), comment.getAuthor().getId(), 2, content, onSendCommentHandler);
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (reply == null) return false;
                    reply = null;
                    mDelegation.setCommentHint("我要回答");
                    mDelegation.getBottomSheet().getEditText().setHint("我要回答");
                }
                return false;
            }
        });

        Reply[] reply = comment.getReply();
        if (reply != null) {
            mLayoutContainer.removeAllViews();
            replies.clear();
            Collections.addAll(replies, comment.getReply());
            Collections.reverse(replies); // 反转集合, 最新的评论在集合后面
            for (int i = 0; i < replies.size(); i++) {
                appendComment(i, replies.get(i));
            }
        }

        fillWebView();
    }

    private void fillWebView() {
        if (TextUtils.isEmpty(comment.getContent())) return;
        if (mWebView != null)
            mWebView.loadDetailDataAsync(comment.getContent(), new Runnable() {
                @Override
                public void run() {

                }
            });
    }

    @SuppressWarnings("deprecation")
    private void appendComment(int i, Reply reply) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_tweet_comment, mLayoutContainer, false);
        TweetCommentAdapter.TweetCommentHolderView holder = new TweetCommentAdapter.TweetCommentHolderView(view);
        holder.tvName.setText(reply.getAuthor().getName());
        if (TextUtils.isEmpty(reply.getAuthor().getPortrait())) {
            holder.ivPortrait.setImageResource(R.mipmap.widget_default_face);
        } else {
            getImageLoader()
                    .load(reply.getAuthor().getPortrait())
                    .asBitmap()
                    .placeholder(getResources().getDrawable(R.mipmap.widget_default_face))
                    .error(getResources().getDrawable(R.mipmap.widget_default_face))
                    .into(holder.ivPortrait);
        }
        holder.tvTime.setText(String.format("%s楼  %s", i + 1, StringUtils.formatSomeAgo(reply.getPubDate())));
        CommentsUtil.formatHtml(getResources(), holder.tvContent, reply.getContent());
        holder.btnReply.setTag(reply);
        holder.btnReply.setOnClickListener(getOnReplyButtonClickListener());
        mLayoutContainer.addView(view, 0);
    }

    private View.OnClickListener getOnReplyButtonClickListener() {
        if (onReplyButtonClickListener == null) {
            onReplyButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Reply reply = (Reply) v.getTag();

                    mDelegation.setCommentHint("回复 @" + reply.getAuthor().getName() + " : ");

                    QuesAnswerDetailActivity.this.reply = reply;
                }
            };
        }
        return onReplyButtonClickListener;
    }

    protected void initData() {
        onSendCommentHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(QuesAnswerDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                    mWaitingDialog = null;
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<Reply> result = AppOperator.createGson().fromJson(
                        responseString,
                        new TypeToken<ResultBean<Reply>>() {
                        }.getType()
                );
                if (result.isSuccess()) {
                    replies.add(result.getResult());
                    tvCmnCount.setText("评论 (" + replies.size() + ")");
                    reply = null;
                    mDelegation.setCommentHint("我要回答");
                    mDelegation.getBottomSheet().getEditText().setHint("我要回答");
                    mDelegation.getBottomSheet().getEditText().setText("");
                    mDelegation.getBottomSheet().getBtnCommit().setTag(null);
                    appendComment(replies.size() - 1, result.getResult());
                    boolean syncToTweet = mDelegation.getBottomSheet().isSyncToTweet();
                    if (syncToTweet) {
                        TweetPublishService.startActionPublish(QuesAnswerDetailActivity.this,
                                mDelegation.getBottomSheet().getCommentText(), null,
                                About.buildShare(sid, mType));
                    }
                } else {
                    Toast.makeText(QuesAnswerDetailActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mDelegation.getBottomSheet().dismiss();
                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                    mWaitingDialog = null;
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mDelegation.getBottomSheet().dismiss();
            }
        };

        OSChinaApi.getCommentDetail(comment.getId(), comment.getAuthor().getId(), mType, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String respStr, Throwable throwable) {
                Toast.makeText(QuesAnswerDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String respStr) {
                ResultBean<Comment> result = AppOperator.createGson().fromJson(respStr,
                        new TypeToken<ResultBean<Comment>>() {
                        }.getType());
                if (result.isSuccess()) {
                    Comment cmn = result.getResult();
                    if (cmn != null && cmn.getId() > 0) {
                        comment = cmn;
                        initWidget();
                        return;
                    }
                }
                Toast.makeText(QuesAnswerDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("InflateParams")
    private View getVoteDialogView() {
        if (mVoteDialogView == null) {
            mVoteDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_question_comment_detail_vote, null, false);
            final VoteViewHolder holder = new VoteViewHolder(mVoteDialogView);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (!AccountHelper.isLogin()) {
                        UIHelper.showLoginActivity(QuesAnswerDetailActivity.this);
                        return;
                    }
                    final int opt = (int) v.getTag();
                    switch (opt) {
                        case Comment.VOTE_STATE_UP:
                            if (ivVoteDown.isSelected()) {
                                Toast.makeText(QuesAnswerDetailActivity.this, "你已经踩过了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            holder.mVoteUp.setVisibility(View.GONE);
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                            break;
                        case Comment.VOTE_STATE_DOWN:
                            if (ivVoteUp.isSelected()) {
                                Toast.makeText(QuesAnswerDetailActivity.this, "你已经顶过了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            holder.mVoteDown.setVisibility(View.GONE);
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                            break;
                    }
                    OSChinaApi.questionVote(sid, comment.getId(), opt, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(QuesAnswerDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                            if (mVoteDialog != null && mVoteDialog.isShowing()) {
                                switch (opt) {
                                    case Comment.VOTE_STATE_UP:
                                        holder.mVoteUp.setVisibility(View.VISIBLE);
                                        holder.mProgressBar.setVisibility(View.GONE);
                                        break;
                                    case Comment.VOTE_STATE_DOWN:
                                        holder.mVoteDown.setVisibility(View.VISIBLE);
                                        holder.mProgressBar.setVisibility(View.GONE);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            ResultBean<Comment> result = AppOperator.createGson().fromJson(
                                    responseString, new TypeToken<ResultBean<Comment>>() {
                                    }.getType());
                            if (result.isSuccess()) {
                                comment.setVoteState(result.getResult().getVoteState());
                                comment.setVote((int) result.getResult().getVote());
                                tvVoteCount.setText(String.valueOf(result.getResult().getVote()));
                                v.setSelected(!v.isSelected());
                                switch (opt) {
                                    case Comment.VOTE_STATE_UP:
                                        ivVoteUp.setSelected(!ivVoteUp.isSelected());
                                        break;
                                    case Comment.VOTE_STATE_DOWN:
                                        ivVoteDown.setSelected(!ivVoteDown.isSelected());
                                        break;
                                }
                                Toast.makeText(QuesAnswerDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(QuesAnswerDetailActivity.this, TextUtils.isEmpty(result.getMessage())
                                        ? "操作失败" : result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (mVoteDialog != null) mVoteDialog.dismiss();
                        }
                    });
                }
            };
            holder.mVoteUp.setTag(Comment.VOTE_STATE_UP);
            holder.mVoteDown.setTag(Comment.VOTE_STATE_DOWN);
            holder.mVoteUp.setOnClickListener(listener);
            holder.mVoteDown.setOnClickListener(listener);
            mVoteDialogView.setTag(holder);
        } else {
            ViewGroup view = (ViewGroup) mVoteDialogView.getParent();
            view.removeView(mVoteDialogView);
        }
        VoteViewHolder holder = (VoteViewHolder) mVoteDialogView.getTag();
        holder.mVoteDown.setVisibility(View.VISIBLE);
        holder.mVoteUp.setVisibility(View.VISIBLE);
        holder.mProgressBar.setVisibility(View.GONE);
        switch (comment.getVoteState()) {
            default:
                holder.mVoteUp.setSelected(false);
                holder.mVoteDown.setSelected(false);
                holder.mVoteUp.setText("顶");
                holder.mVoteDown.setText("踩");
                break;
            case Comment.VOTE_STATE_UP:
                holder.mVoteUp.setSelected(true);
                holder.mVoteDown.setSelected(false);
                holder.mVoteUp.setText("已顶");
                holder.mVoteDown.setText("踩");
                break;
            case Comment.VOTE_STATE_DOWN:
                holder.mVoteUp.setSelected(false);
                holder.mVoteDown.setSelected(true);
                holder.mVoteUp.setText("顶");
                holder.mVoteDown.setText("已踩");
                break;
        }
        return mVoteDialogView;
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.layout_vote)
    void onClickVote() {
        mVoteDialog = DialogHelper.getDialog(this)
                .setView(getVoteDialogView())
                .create();
        mVoteDialog.show();
        WindowManager.LayoutParams params = mVoteDialog.getWindow().getAttributes();
        params.width = (int) TDevice.dp2px(260f);
        mVoteDialog.getWindow().setAttributes(params);
    }

    public static class VoteViewHolder {
        @Bind(R.id.btn_vote_up)
        TextView mVoteUp;
        @Bind(R.id.btn_vote_down)
        TextView mVoteDown;
        @Bind(R.id.progress)
        ProgressBar mProgressBar;

        public VoteViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    protected void onDestroy() {
        final OWebView webView = mWebView;
        if (webView != null) {
            mWebView = null;
            webView.destroy();
        }
        super.onDestroy();
    }
}
