package net.oschina.app.improve.comment;

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
import net.oschina.app.improve.bean.simple.CommentEX;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.tweet.adapter.TweetCommentAdapter;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.ui.SelectFriendsActivity;
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
 * 问答的评论详情
 * Created by thanatos on 16/6/16.
 */
public class QuestionAnswerDetailActivity extends BaseBackActivity {

    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String BUNDLE_ARTICLE_KEY = "BUNDLE_ARTICLE_KEY";

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
    CoordinatorLayout mCoorLayout;
    @Bind(R.id.layout_scroll)
    NestedScrollView mScrollView;

    private long sid;
    private Dialog mVoteDialog;
    private Dialog mWaitingDialog;
    private CommentEX comment;
    private CommentEX.Reply reply;
    private View mVoteDialogView;
    private List<CommentEX.Reply> replies = new ArrayList<>();
    private CommentBar mDelegation;
    private TextHttpResponseHandler onSendCommentHandler;
    private View.OnClickListener onReplyButtonClickListener;

    /**
     * @param context context
     * @param comment comment
     * @param sid     文章id
     */
    public static void show(Context context, CommentEX comment, long sid) {
        Intent intent = new Intent(context, QuestionAnswerDetailActivity.class);
        intent.putExtra(BUNDLE_KEY, comment);
        intent.putExtra(BUNDLE_ARTICLE_KEY, sid);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        comment = (CommentEX) getIntent().getSerializableExtra(BUNDLE_KEY);
        sid = getIntent().getLongExtra(BUNDLE_ARTICLE_KEY, 0);
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
            actionBar.setTitle("返回");
        }
    }

    @SuppressWarnings("deprecation")
    protected void initWidget() {
        // portrait
        if (TextUtils.isEmpty(comment.getAuthorPortrait())) {
            ivPortrait.setImageResource(R.mipmap.widget_dface);
        } else {
            getImageLoader()
                    .load(comment.getAuthorPortrait())
                    .asBitmap()
                    .placeholder(getResources().getDrawable(R.mipmap.widget_dface))
                    .error(getResources().getDrawable(R.mipmap.widget_dface))
                    .into(ivPortrait);
        }

        // nick
        tvNick.setText(comment.getAuthor());

        // publish time
        if (!TextUtils.isEmpty(comment.getPubDate()))
            tvTime.setText(StringUtils.formatSomeAgo(comment.getPubDate()));

        // vote state
        switch (comment.getVoteState()) {
            case CommentEX.VOTE_STATE_UP:
                ivVoteUp.setSelected(true);
                break;
            case CommentEX.VOTE_STATE_DOWN:
                ivVoteDown.setSelected(true);
        }

        // vote count
        tvVoteCount.setText(String.valueOf(comment.getVoteCount()));

        tvCmnCount.setText("评论 (" + (comment.getReply() == null ? 0 : comment.getReply().length) + ")");

        mDelegation = CommentBar.delegation(this, mCoorLayout);

        mDelegation.hideFav();
        mDelegation.hideShare();

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin())
                    SelectFriendsActivity.show(QuestionAnswerDetailActivity.this);
                else
                    LoginActivity.show(QuestionAnswerDetailActivity.this);
            }
        });

        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mDelegation.getBottomSheet().getCommentText();
                if (TextUtils.isEmpty(content.replaceAll("[ \\s\\n]+", ""))) {
                    Toast.makeText(QuestionAnswerDetailActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AccountHelper.isLogin()) {
                    UIHelper.showLoginActivity(QuestionAnswerDetailActivity.this);
                    return;
                }

                mWaitingDialog = DialogHelper.getProgressDialog(QuestionAnswerDetailActivity.this, "正在发表评论...", false);
                mWaitingDialog.show();

                OSChinaApi.publishComment(sid, -1, comment.getId(), comment.getAuthorId(), 2, content, onSendCommentHandler);
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (reply == null) return false;
                    reply = null;
                    mDelegation.setCommentHint("发表评论");
                    mDelegation.getBottomSheet().getEditText().setHint("发表评论");
                }
                return false;
            }
        });

        if (comment.getReply() != null) {
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
        mWebView.loadDetailDataAsync(comment.getContent(), null);
    }

    @SuppressWarnings("deprecation")
    private void appendComment(int i, CommentEX.Reply reply) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_tweet_comment, mLayoutContainer, false);
        TweetCommentAdapter.TweetCommentHolderView holder = new TweetCommentAdapter.TweetCommentHolderView(view);
        holder.tvName.setText(reply.getAuthor());
        if (TextUtils.isEmpty(reply.getAuthorPortrait())) {
            holder.ivPortrait.setImageResource(R.mipmap.widget_dface);
        } else {
            getImageLoader()
                    .load(reply.getAuthorPortrait())
                    .asBitmap()
                    .placeholder(getResources().getDrawable(R.mipmap.widget_dface))
                    .error(getResources().getDrawable(R.mipmap.widget_dface))
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
                    CommentEX.Reply reply = (CommentEX.Reply) v.getTag();
                    mDelegation.getBottomSheet().getEditText().setText("回复 @" + reply.getAuthor() + " : ");
                    mDelegation.setCommentHint("回复 @" + reply.getAuthor() + " : ");
                    QuestionAnswerDetailActivity.this.reply = reply;
                }
            };
        }
        return onReplyButtonClickListener;
    }

    protected void initData() {
        onSendCommentHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(QuestionAnswerDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                if (mWaitingDialog != null) {
                    mWaitingDialog.dismiss();
                    mWaitingDialog = null;
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<CommentEX.Reply> result = AppOperator.createGson().fromJson(
                        responseString,
                        new TypeToken<ResultBean<CommentEX.Reply>>() {
                        }.getType()
                );
                if (result.isSuccess()) {
                    replies.add(result.getResult());
                    tvCmnCount.setText("评论 (" + replies.size() + ")");
                    reply = null;
                    mDelegation.setCommentHint("发表评论");
                    mDelegation.getBottomSheet().getEditText().setHint("发表评论");
                    appendComment(replies.size() - 1, result.getResult());
                } else {
                    Toast.makeText(QuestionAnswerDetailActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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

        OSChinaApi.getComment(comment.getId(), comment.getAuthorId(), 2, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String respStr, Throwable throwable) {
                Toast.makeText(QuestionAnswerDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String respStr) {
                ResultBean<CommentEX> result = AppOperator.createGson().fromJson(respStr,
                        new TypeToken<ResultBean<CommentEX>>() {
                        }.getType());
                if (result.isSuccess()) {
                    CommentEX cmn = result.getResult();
                    if (cmn != null && cmn.getId() > 0) {
                        comment = cmn;
                        initWidget();
                        return;
                    }
                }
                Toast.makeText(QuestionAnswerDetailActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View getVoteDialogView() {
        if (mVoteDialogView == null) {
            mVoteDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_question_comment_detail_vote, null, false);
            final VoteViewHolder holder = new VoteViewHolder(mVoteDialogView);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (!AccountHelper.isLogin()) {
                        UIHelper.showLoginActivity(QuestionAnswerDetailActivity.this);
                        return;
                    }
                    final int opt = (int) v.getTag();
                    switch (opt) {
                        case CommentEX.VOTE_STATE_UP:
                            if (ivVoteDown.isSelected()) {
                                Toast.makeText(QuestionAnswerDetailActivity.this, "你已经踩过了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            holder.mVoteUp.setVisibility(View.GONE);
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                            break;
                        case CommentEX.VOTE_STATE_DOWN:
                            if (ivVoteUp.isSelected()) {
                                Toast.makeText(QuestionAnswerDetailActivity.this, "你已经顶过了", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            holder.mVoteDown.setVisibility(View.GONE);
                            holder.mProgressBar.setVisibility(View.VISIBLE);
                            break;
                    }
                    OSChinaApi.questionVote(sid, comment.getId(), opt, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(QuestionAnswerDetailActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                            if (mVoteDialog != null && mVoteDialog.isShowing()) {
                                switch (opt) {
                                    case CommentEX.VOTE_STATE_UP:
                                        holder.mVoteUp.setVisibility(View.VISIBLE);
                                        holder.mProgressBar.setVisibility(View.GONE);
                                        break;
                                    case CommentEX.VOTE_STATE_DOWN:
                                        holder.mVoteDown.setVisibility(View.VISIBLE);
                                        holder.mProgressBar.setVisibility(View.GONE);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            ResultBean<CommentEX> result = AppOperator.createGson().fromJson(
                                    responseString, new TypeToken<ResultBean<CommentEX>>() {
                                    }.getType());
                            if (result.isSuccess()) {
                                comment.setVoteState(result.getResult().getVoteState());
                                comment.setVoteCount(result.getResult().getVoteCount());
                                tvVoteCount.setText(String.valueOf(result.getResult().getVoteCount()));
                                v.setSelected(!v.isSelected());
                                switch (opt) {
                                    case CommentEX.VOTE_STATE_UP:
                                        ivVoteUp.setSelected(!ivVoteUp.isSelected());
                                        break;
                                    case CommentEX.VOTE_STATE_DOWN:
                                        ivVoteDown.setSelected(!ivVoteDown.isSelected());
                                        break;
                                }
                                Toast.makeText(QuestionAnswerDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(QuestionAnswerDetailActivity.this, TextUtils.isEmpty(result.getMessage())
                                        ? "操作失败" : result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (mVoteDialog != null) mVoteDialog.dismiss();
                        }
                    });
                }
            };
            holder.mVoteUp.setTag(CommentEX.VOTE_STATE_UP);
            holder.mVoteDown.setTag(CommentEX.VOTE_STATE_DOWN);
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
            case CommentEX.VOTE_STATE_UP:
                holder.mVoteUp.setSelected(true);
                holder.mVoteDown.setSelected(false);
                holder.mVoteUp.setText("已顶");
                holder.mVoteDown.setText("踩");
                break;
            case CommentEX.VOTE_STATE_DOWN:
                holder.mVoteUp.setSelected(false);
                holder.mVoteDown.setSelected(true);
                holder.mVoteUp.setText("顶");
                holder.mVoteDown.setText("已踩");
                break;
        }
        return mVoteDialogView;
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            mDelegation.getBottomSheet().handleSelectFriendsResult(data);
            mDelegation.setCommentHint(mDelegation.getBottomSheet().getEditText().getHint().toString());
        }
    }
}
