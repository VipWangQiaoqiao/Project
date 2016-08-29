package net.oschina.app.improve.comment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.Comment;
import net.oschina.app.improve.behavior.FloatingAutoHideDownBehavior;
import net.oschina.app.improve.behavior.KeyboardInputDelegation;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.util.DialogHelp;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.TweetTextView;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

public class CommentsActivity extends BaseBackActivity {

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
    private KeyboardInputDelegation mDelegation;
    private View.OnClickListener onReplyBtnClickListener;
    private ProgressDialog mDialog;
    private boolean mInputDoubleEmpty;
    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {
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
                Type type = new TypeToken<ResultBean<Comment>>() {
                }.getType();

                ResultBean<Comment> resultBean = AppContext.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    Comment respComment = resultBean.getResult();
                    if (respComment != null) {

                        Toast.makeText(CommentsActivity.this, "评论成功", Toast.LENGTH_LONG).show();
                        mDelegation.getInputView().setText("");
                        getData(true, null);
                        TDevice.hideSoftKeyboard(mDelegation.getInputView());
                    }
                }
                hideWaitDialog();
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
            hideWaitDialog();
        }
    };

    public static void show(Context context, long id, int type) {
        Intent intent = new Intent(context, CommentsActivity.class);
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

        mDelegation = KeyboardInputDelegation.delegation(this, mCoorLayout, mRefreshLayout);
        mDelegation.setBehavior(new FloatingAutoHideDownBehavior());
        mDelegation.setAdapter(new KeyboardInputDelegation.KeyboardInputAdapter() {
            @Override
            public void onSubmit(TextView v, String content) {

                handleSendComment(mType, mId, reply == null ? 0 : reply.getId(), reply == null ? 0 : reply.getAuthorId(), content);

            }

            @Override
            public void onFinalBackSpace(View v) {
                if (reply == null) return;
                reply = null;
                mDelegation.getInputView().setHint("发表评论");
                handleKeyDel();
            }
        });
        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    /**
     * 检查当前数据,并检查网络状况
     *
     * @return 返回当前登录用户, 未登录或者未通过检查返回0
     */
    private int requestCheck() {
        if (mId == 0) {
            AppContext.showToast("数据加载中...");
            return 0;
        }
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return 0;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            return 0;
        }
        // 返回当前登录用户ID
        return AppContext.getInstance().getLoginUid();
    }

    /**
     * handle send comment
     */
    private void handleSendComment(int type, long id, final long commentId, long commentAuthorId, String content) {
        int uid = requestCheck();
        if (uid == 0)
            return;

        if (TextUtils.isEmpty(content)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }

        switch (type) {
            case 2:
                OSChinaApi.pubQuestionComment(id, commentId, commentAuthorId, content, mHandler);
                break;
            case 3:
                OSChinaApi.pubBlogComment(id, commentId, commentAuthorId, content, mHandler);
                break;
            case 4:
                OSChinaApi.pubTranslateComment(id, commentId, commentAuthorId, content, mHandler);
                break;
            case 6:
                OSChinaApi.pubNewsComment(id, commentId, commentAuthorId, content, mHandler);
                break;
            default:
                break;
        }

    }


    /**
     * handle key del content
     */
    private void handleKeyDel() {
        if (reply.getId() != mId) {
            if (TextUtils.isEmpty(mDelegation.getInputView().getText())) {
                if (mInputDoubleEmpty) {
                    mDelegation.getInputView().setHint("发表评论");
                } else {
                    mInputDoubleEmpty = true;
                }
            } else {
                mInputDoubleEmpty = false;
            }
        }
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
            mDialog = DialogHelp.getWaitDialog(this, message);
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

    private void getData(final boolean clearData, String token) {
        OSChinaApi.getComments(mId, mType, "refer", token, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
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

                    ResultBean<PageBean<Comment>> resultBean = AppContext.createGson().fromJson(responseString, type);
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
        mAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener getReplyBtnClickListener() {
        if (onReplyBtnClickListener == null) {
            onReplyBtnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comment comment = (Comment) v.getTag();
                    mDelegation.getInputView().setHint("@" + comment.getAuthor() + " :");
                    reply = comment;
                    mDelegation.notifyWrapper();
                    TDevice.showSoftKeyboard(mDelegation.getInputView());
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
        private ImageView btnReply;

        CommentHolder(View itemView) {
            super(itemView);

            mAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mDate = (TextView) itemView.findViewById(R.id.tv_pub_date);
            btnReply = (ImageView) itemView.findViewById(R.id.btn_comment);

            mContent = ((TweetTextView) itemView.findViewById(R.id.tv_content));
            mRefers = ((LinearLayout) itemView.findViewById(R.id.lay_refer));
        }

        void setData(Comment comment, RequestManager imageLoader, View.OnClickListener l) {
            if (comment.getAuthorPortrait() != null)
                imageLoader.load(comment.getAuthorPortrait()).error(R.mipmap.widget_dface)
                        .into((mAvatar));
            else
                mAvatar.setImageResource(R.mipmap.widget_dface);

            mName.setText(comment.getAuthor());
            mDate.setText(comment.getPubDate());
            CommentsUtil.formatHtml(mContent.getResources(), mContent, comment.getContent());

            mRefers.removeAllViews();
            if (comment.getRefer() != null) {
                // 最多5层
                View view = CommentsUtil.getReferLayout(LayoutInflater.from(mRefers.getContext()), comment.getRefer(), 5);
                mRefers.addView(view);
            }
            btnReply.setTag(comment);
            btnReply.setOnClickListener(l);
        }
    }

    private void onItemClick(Comment comment) {


    }

    private class Adapter extends BaseRecyclerAdapter<Comment> {

        private CommentHolder commentHolder;

        Adapter(Context context) {
            super(context, ONLY_FOOTER);
            mState = STATE_HIDE;
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, long itemId) {
                    CommentsActivity.this.onItemClick(getItem(position));
                }
            });
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.lay_comment_item, parent, false);
            return new CommentHolder(view);
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
            if (holder instanceof CommentHolder) {
                commentHolder = (CommentHolder) holder;
                RequestManager requestManager = getImageLoader();
                if (requestManager != null)
                    commentHolder.setData(item, requestManager, getReplyBtnClickListener());
            }
        }

    }
}
