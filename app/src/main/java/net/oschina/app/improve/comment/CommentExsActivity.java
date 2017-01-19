package net.oschina.app.improve.comment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.CommentEX;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapter;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.TweetTextView;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

public class CommentExsActivity extends BaseBackActivity {
    private long mId;
    private int mType;

    private PageBean<Comment> mPageBean;

    @Bind(R.id.lay_refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;

    @Bind(R.id.lay_blog_detail_comment)
    RecyclerView mLayComments;

    @Bind(R.id.activity_comments)
    CoordinatorLayout mCoorLayout;

    private Adapter mAdapter;
    private Comment reply;
    private CommentBar mDelegation;
    private View.OnClickListener onReplyBtnClickListener;
    private ProgressDialog mDialog;

    public static void show(Context context, long id, int type) {
        Intent intent = new Intent(context, CommentExsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_comments;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mId = bundle.getLong("id");
        mType = bundle.getInt("type");
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mLayComments.setLayoutManager(manager);

        mAdapter = new Adapter(this);
        mLayComments.setAdapter(mAdapter);

        mDelegation = CommentBar.delegation(this, mCoorLayout);
        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendComment(mId, reply == null ? 0 : reply.getId(), reply == null ? 0 : reply.getAuthor().getId(), mDelegation.getBottomSheet().getCommentText());
            }
        });

        mDelegation.hideFav();
        mDelegation.hideShare();

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin()) {
                    UserSelectFriendsActivity.show(CommentExsActivity.this, mDelegation.getBottomSheet().getEditText());
                } else {
                    LoginActivity.show(CommentExsActivity.this);
                }
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapter(this));
        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (reply == null) return false;
                    reply = null;
                    mDelegation.getCommentText().setHint("发表评论");
                    mDelegation.getBottomSheet().getEditText().setHint("发表评论");
                }
                return false;
            }
        });
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    @Override
    protected void initData() {
        super.initData();
        mRefreshLayout.setSuperRefreshLayoutListener(new RecyclerRefreshLayout.SuperRefreshLayoutListener() {
            @Override
            public void onRefreshing() {
                getData(true, null);
            }

            @Override
            public void onLoadMore() {
                String token = null;
                if (mPageBean != null)
                    token = mPageBean.getNextPageToken();
                getData(false, token);
            }
        });

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mRefreshLayout.onRefresh();
            }
        });
    }

    /**
     * 检查当前数据,并检查网络状况
     *
     * @return 返回当前登录用户, 未登录或者未通过检查返回0
     */
    private long requestCheck() {
        if (mId == 0) {
            AppContext.showToast("数据加载中...");
            return 0;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return 0;
        }
        if (!AccountHelper.isLogin()) {
            UIHelper.showLoginActivity(this);
            return 0;
        }
        // 返回当前登录用户ID
        return AccountHelper.getUserId();
    }


    /**
     * handle send comment
     */
    private void handleSendComment(long id, final long commentId, long commentAuthorId, String content) {
        long uid = requestCheck();
        if (uid == 0)
            return;

        if (TextUtils.isEmpty(content)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }
        OSChinaApi.pubQuestionComment(id, commentId, commentAuthorId, content, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showWaitDialog(R.string.progress_submit);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                AppContext.showToast("评论失败!");
                hideWaitDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<CommentEX>>() {
                    }.getType();

                    ResultBean<CommentEX> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        CommentEX respComment = resultBean.getResult();
                        if (respComment != null) {

                            Toast.makeText(CommentExsActivity.this, "评论成功", Toast.LENGTH_LONG).show();
                            mDelegation.setCommentHint("发表评论");
                            mDelegation.getBottomSheet().getEditText().setHint("发表评论");
                            getData(true, null);
                            mDelegation.getBottomSheet().dismiss();
                        }
                    } else {
                        Toast.makeText(CommentExsActivity.this, resultBean.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    hideWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
                hideWaitDialog();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mDelegation.getBottomSheet().dismiss();
            }
        });

    }

    /**
     * show waittDialog
     *
     * @param messageId messageId
     * @return progressDialog
     */
    private ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(this, message);
        }

        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

    /**
     * hideWaitDialog
     */
    public void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void getData(final boolean clearData, String token) {
        OSChinaApi.getComments(mId, mType, "refer", token, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRefreshLayout.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PageBean<Comment>>>() {
                    }.getType();

                    ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        if (resultBean.getResult() != null
                                && resultBean.getResult().getItems() != null
                                && resultBean.getResult().getItems().size() > 0) {
                            mPageBean = resultBean.getResult();
                            handleData(mPageBean.getItems(), clearData);
                            return;
                        }
                    }
                    mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void handleData(List<Comment> comments, boolean clearData) {
        if (clearData)
            mAdapter.clear();

        mAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, false);
        mAdapter.addAll(comments);
        if (mAdapter.getItems().size() < 20)
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
    }

    public View.OnClickListener getReplyBtnClickListener() {
        if (onReplyBtnClickListener == null) {
            onReplyBtnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comment comment = (Comment) v.getTag();
                    mDelegation.setCommentHint("@" + comment.getAuthor() + " :");
                    mDelegation.getBottomSheet().getEditText().setHint("@" + comment.getAuthor() + " :");
                    reply = comment;
                }
            };
        }
        return onReplyBtnClickListener;
    }

    private static class CommentHolder extends RecyclerView.ViewHolder {
        private ImageView mAvatar;
        private TextView mName;
        private TextView mDate;
        private TweetTextView mContent;
        private LinearLayout mRefers;
        private ImageView btn_comment, iv_best_comment;

        CommentHolder(View itemView) {
            super(itemView);

            mAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mDate = (TextView) itemView.findViewById(R.id.tv_pub_date);
            btn_comment = (ImageView) itemView.findViewById(R.id.btn_comment);
            iv_best_comment = (ImageView) itemView.findViewById(R.id.iv_best_answer);
            mContent = ((TweetTextView) itemView.findViewById(R.id.tv_content));
            mRefers = ((LinearLayout) itemView.findViewById(R.id.lay_refer));
        }

        void setData(Comment comment, RequestManager imageLoader, View.OnClickListener l) {
            if (comment.getAuthor().getPortrait() != null)
                imageLoader.load(comment.getAuthor().getPortrait()).error(R.mipmap.widget_default_face)
                        .into((mAvatar));
            else
                mAvatar.setImageResource(R.mipmap.widget_default_face);

            mName.setText(comment.getAuthor().getName());
            mDate.setText(comment.getPubDate());
            CommentsUtil.formatHtml(mContent.getResources(), mContent, comment.getContent());

            mRefers.removeAllViews();
            if (comment.getRefer() != null) {
                // 最多5层
                View view = CommentsUtil.getReplyLayout(LayoutInflater.from(mRefers.getContext()), comment.getReply(), 0);
                mRefers.addView(view);
            }

            btn_comment.setTag(comment);
            if (l != null)
                btn_comment.setOnClickListener(l);
        }
    }

    private class Adapter extends BaseRecyclerAdapter<Comment> {

        Adapter(Context context) {
            super(context, ONLY_FOOTER);
            mState = STATE_LOADING;
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, long itemId) {
                    CommentExsActivity.this.onItemClick(getItem(position));
                }
            });
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.lay_comment_item_ex, parent, false);
            return new CommentHolder(view);
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
            if (holder instanceof CommentHolder) {
                CommentHolder commentHolder = (CommentHolder) holder;
                RequestManager requestManager = getImageLoader();
                if (requestManager != null)
                    commentHolder.setData(item, requestManager, null);
                if (item.isBest()) {
                    commentHolder.btn_comment.setVisibility(View.GONE);
                    commentHolder.iv_best_comment.setVisibility(View.VISIBLE);
                } else {
                    commentHolder.btn_comment.setVisibility(View.VISIBLE);
                    commentHolder.iv_best_comment.setVisibility(View.GONE);
                }
            }
        }

    }

    private void onItemClick(Comment comment) {
        QuesAnswerDetailActivity.show(this, comment, mId, mType);
    }

}
