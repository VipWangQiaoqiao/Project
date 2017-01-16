package net.oschina.app.improve.comment.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.comment.Vote;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.CommentReferView;
import net.oschina.app.improve.comment.CommentsUtil;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.widget.TweetTextView;

import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by fei
 * on 2016/11/21.
 * desc:
 */
public class CommentAdapter extends BaseRecyclerAdapter<Comment> {

    private static final int VIEW_TYPE_DATA_FOOTER = 2000;
    private long mSourceId;

    private int mType;
    private CommentBar delegation;
    private RequestManager mRequestManager;

    public CommentAdapter(final Context context, RequestManager requestManager, int mode) {
        super(context, mode);
        this.mRequestManager = requestManager;
    }

    @Override
    protected CommentHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lay_comment_refer_item, parent, false);
        return new CommentHolder(view, delegation);
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
        if (holder instanceof CommentHolder) {
            ((CommentHolder) holder).addComment(mSourceId, mType, item, mRequestManager);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = super.getItemViewType(position);
        if (type == VIEW_TYPE_NORMAL && isRealDataFooter(position)) {
            return VIEW_TYPE_DATA_FOOTER;
        }
        return type;
    }

    public void setSourceId(long sourceId) {
        this.mSourceId = sourceId;
    }

    public void setCommentType(int Type) {
        this.mType = Type;
    }

    public void setDelegation(CommentBar delegation) {
        this.delegation = delegation;
    }

    private boolean isRealDataFooter(int position) {
        return getIndex(position) == getCount() - 1;
    }

    static class CommentHolder extends RecyclerView.ViewHolder {

        private ProgressDialog mDialog;

        @Bind(R.id.iv_avatar)
        CircleImageView mIvAvatar;

        @Bind(R.id.tv_name)
        TextView mName;
        @Bind(R.id.tv_pub_date)
        TextView mPubDate;
        @Bind(R.id.tv_vote_count)
        TextView mVoteCount;
        @Bind(R.id.btn_vote)
        ImageView mVote;
        @Bind(R.id.btn_comment)
        ImageView mComment;

        @Bind(R.id.lay_refer)
        CommentReferView mCommentReferView;

        @Bind(R.id.tv_content)
        TweetTextView mTweetTextView;
        @Bind(R.id.line)
        View mLine;

        private CommentBar commentBar;

        CommentHolder(View itemView, CommentBar commentBar) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.commentBar = commentBar;
        }

        /**
         * add comment
         *
         * @param comment comment
         */
        @SuppressLint("DefaultLocale")
        void addComment(final long sourceId, final int commentType, final Comment comment, RequestManager requestManager) {

            requestManager.load(comment.getAuthor().getPortrait()).error(R.mipmap.widget_default_face).into(mIvAvatar);
            mIvAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherUserHomeActivity.show(mIvAvatar.getContext(), comment.getAuthor().getId());
                }
            });
            String name = comment.getAuthor().getName();
            if (TextUtils.isEmpty(name))
                name = mName.getResources().getString(R.string.martian_hint);
            mName.setText(name);
            mPubDate.setText(String.format("%s", StringUtils.formatSomeAgo(comment.getPubDate())));

            mComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!AccountHelper.isLogin()) {
                        LoginActivity.show((Activity) mComment.getContext(), 1);
                        return;
                    }
                    commentBar.getBottomSheet().getBtnCommit().setTag(comment);

                    commentBar.getBottomSheet().show(String.format("%s %s",
                            mComment.getResources().getString(R.string.reply_hint), comment.getAuthor().getName()));
                }
            });

            if (commentType == OSChinaApi.COMMENT_QUESTION || commentType == OSChinaApi.COMMENT_EVENT
                    || commentType == OSChinaApi.COMMENT_BLOG || commentType == OSChinaApi.COMMENT_TRANSLATION
                    || commentType == OSChinaApi.COMMENT_SOFT) {
                mVoteCount.setVisibility(View.GONE);
                mVote.setVisibility(View.GONE);

                if (commentType == OSChinaApi.COMMENT_SOFT) {
                    mComment.setVisibility(View.GONE);
                } else {
                    if (comment.isBest()) {
                        mComment.setImageResource(R.mipmap.label_best_answer);
                        mComment.setEnabled(false);
                    } else {
                        mComment.setEnabled(true);
                        mComment.setImageResource(R.mipmap.ic_comment_30);
                    }
                }
            } else {
                mVoteCount.setText(String.valueOf(comment.getVote()));
                mVoteCount.setVisibility(View.VISIBLE);
                mVote.setVisibility(View.VISIBLE);
                mComment.setEnabled(true);
                if (comment.getVoteState() == 1) {
                    mVote.setImageResource(R.mipmap.ic_thumbup_actived);
                    mVote.setTag(true);
                } else if (comment.getVoteState() == 0) {
                    mVote.setImageResource(R.mipmap.ic_thumb_normal);
                    mVote.setTag(null);
                }
                mVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handVote();
                    }

                    private void handVote() {
                        if (!AccountHelper.isLogin()) {
                            LoginActivity.show(mVote.getContext());
                            return;
                        }
                        if (!TDevice.hasInternet()) {
                            AppContext.showToast(mVote.getResources().getString(R.string.state_network_error), Toast.LENGTH_SHORT);
                            return;
                        }
                        OSChinaApi.voteComment(commentType, comment.getId(), comment.getAuthor().getId(), mVote.getTag() != null ? 0 : 1, new TextHttpResponseHandler() {

                            @Override
                            public void onStart() {
                                super.onStart();
                                showWaitDialog(R.string.progress_submit);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                requestFailureHint(throwable);
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                                ResultBean<Vote> resultBean = AppOperator.createGson().fromJson(responseString, getVoteType());
                                if (resultBean.isSuccess()) {
                                    Vote vote = resultBean.getResult();
                                    if (vote != null) {
                                        if (vote.getVoteState() == 1) {
                                            comment.setVoteState(1);
                                            mVote.setTag(true);
                                            mVote.setImageResource(R.mipmap.ic_thumbup_actived);
                                        } else if (vote.getVoteState() == 0) {
                                            comment.setVoteState(0);
                                            mVote.setTag(null);
                                            mVote.setImageResource(R.mipmap.ic_thumb_normal);
                                        }
                                        long voteVoteCount = vote.getVote();
                                        comment.setVote(voteVoteCount);
                                        mVoteCount.setText(String.valueOf(voteVoteCount));
                                    }
                                } else {
                                    AppContext.showToast(resultBean.getMessage(), Toast.LENGTH_SHORT);
                                }
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();
                                hideWaitDialog();
                            }
                        });
                    }
                });
            }

            mCommentReferView.addComment(comment);

            CommentsUtil.formatHtml(mTweetTextView.getResources(), mTweetTextView, comment.getContent());
        }

        /**
         * show WaitDialog
         *
         * @return progressDialog
         */
        private ProgressDialog showWaitDialog(@StringRes int messageId) {

            if (mDialog == null) {
                if (messageId <= 0) {
                    mDialog = DialogHelper.getProgressDialog(mVote.getContext(), true);
                } else {
                    String message = mVote.getContext().getResources().getString(messageId);
                    mDialog = DialogHelper.getProgressDialog(mVote.getContext(), message, true);
                }
            }
            mDialog.show();

            return mDialog;
        }

        /**
         * hide waitDialog
         */
        private void hideWaitDialog() {
            ProgressDialog dialog = mDialog;
            if (dialog != null) {
                mDialog = null;
                try {
                    dialog.cancel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         * request network error
         *
         * @param throwable throwable
         */
        private void requestFailureHint(Throwable throwable) {
            AppContext.showToast(R.string.request_error_hint);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }

        /**
         * @return TypeToken
         */
        Type getVoteType() {
            return new TypeToken<ResultBean<Vote>>() {
            }.getType();
        }

    }


}
