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
import net.oschina.app.improve.bean.comment.Refer;
import net.oschina.app.improve.bean.comment.Reply;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.ui.SelectFriendsActivity;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * Created by  fei
 * on  16/11/17
 * desc:详情评论列表ui
 */
public class CommentsActivity extends BaseBackActivity {

    private long mId;
    private int mType;

    private PageBean<Comment> mPageBean;

    @Bind(R.id.lay_refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;

    @Bind(R.id.lay_blog_detail_comment)
    RecyclerView mLayComments;

    @Bind(R.id.activity_comments)
    CoordinatorLayout mCoordLayout;

    private CommentAdapter mCommentAdapter;
    private Comment reply;
    private CommentBar mDelegation;
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

                ResultBean<Comment> resultBean = AppOperator.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    Comment respComment = resultBean.getResult();
                    if (respComment != null) {

                        Toast.makeText(CommentsActivity.this, "评论成功", Toast.LENGTH_LONG).show();
                        mDelegation.getBottomSheet().getEditText().setText("");
                        mDelegation.setCommentHint("发表评论");
                        mDelegation.getBottomSheet().getEditText().setHint("发表评论");
                        getData(true, null);
                    }
                }
                hideWaitDialog();
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
            hideWaitDialog();
            mDelegation.getBottomSheet().dismiss();
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

        mCommentAdapter = new CommentAdapter(this);
        mLayComments.setAdapter(mCommentAdapter);

        mDelegation = CommentBar.delegation(this, mCoordLayout);
        mDelegation.hideFav();
        mDelegation.hideShare();

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin())
                    SelectFriendsActivity.show(CommentsActivity.this);
                else
                    LoginActivity.show(CommentsActivity.this);
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (reply == null) return false;
                    reply = null;

                    handleKeyDel();
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
        getData(true, null);
    }

    Type getCommentType() {
        return new TypeToken<ResultBean<PageBean<Comment>>>() {
        }.getType();
    }

    /**
     * 检查当前数据,并检查网络状况
     *
     * @return 返回当前登录用户, 未登录或者未通过检查返回0
     */
    private long requestCheck() {
        if (mId == 0) {
            AppContext.showToast(getResources().getString(R.string.state_loading_error));
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
    private void handleSendComment(int type, long id, final long commentId, long commentAuthorId, String content) {
        long uid = requestCheck();
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
            if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
                if (mInputDoubleEmpty) {
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

    /**
     * show waittDialog
     *
     * @param messageId messageId
     * @return progressDialog
     */
    @SuppressWarnings("deprecation")
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
                mCommentAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRefreshLayout.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = getCommentType();


                    Comment[] comments = new Comment[30];
                    for (int i = 0, len = comments.length; i < len; i++) {

                        Comment comment = new Comment();
                        comment.setId((i + 100));
                        Author author = new Author();
                        author.setId((i + 20));
                        author.setName("大神" + i);
                        author.setPortrait("https://static.oschina.net/uploads/user/1133/2267007_50.jpg?t=1415270116000");
                        comment.setAuthor(author);
                        comment.setContent("这是第一条评论" + i);
                        comment.setPubDate("2013-09-17 16:49:34");
                        comment.setAppClient(2);
                        comment.setVote(200);
                        comment.setVoteState((int) (Math.random() * 1));
                        comment.setBest(true);

                        Refer[] refers = new Refer[(int) (Math.random() * 10 + 1)];

                        for (int j = 0; j < refers.length; j++) {
                            Refer refer = new Refer();
                            refer.setAuthor("引用的人的名字" + j);
                            refer.setContent("引用的内容" + j);
                            refer.setPubDate("2013-09-18 16:49:34");
                            refers[j] = refer;
                        }
                        comment.setRefer(refers);

                        Reply[] reply = new Reply[2];
                        for (int j = 0; j < reply.length; j++) {
                            Reply reply1 = new Reply();
                            reply1.setId((j + 50));
                            Author author1 = new Author();
                            author1.setId((j + 90));
                            author1.setName(("这是评论的人的名字" + j));
                            author1.setPortrait("https://static.oschina.net/uploads/user/1133/2267007_50.jpg?t=1415270116000");
                            reply1.setAuthor(author1);
                            reply[j] = reply1;
                        }
                        comment.setReply(reply);
                        comments[i] = comment;
                    }
                    //  ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    //if (resultBean != null && resultBean.isSuccess()) {
                    // if (resultBean.getResult() != null && resultBean.getResult().getItems() != null
                    // && resultBean.getResult().getItems().size() > 0) {
                    PageBean<Comment> pageBean = new PageBean<>();
                    List<Comment> commentList = Arrays.asList(comments);
                    pageBean.setItems(commentList);
                    mPageBean = pageBean;// resultBean.getResult();
                    handleData(mPageBean.getItems(), clearData);
                    // return;
                    //  }
                    // }
                    mCommentAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    private void handleData(List<Comment> comments, boolean clearData) {
        if (clearData)
            mCommentAdapter.clear();

        mCommentAdapter.setState(BaseRecyclerAdapter.STATE_LOADING, false);
        mCommentAdapter.addAll(comments);
        mCommentAdapter.notifyDataSetChanged();
    }

    public View.OnClickListener getReplyBtnClickListener() {
        if (onReplyBtnClickListener == null) {
            onReplyBtnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comment comment = (Comment) v.getTag();
                    mDelegation.setCommentHint("@" + comment.getAuthor() + " :");
                    reply = comment;
                }
            };
        }
        return onReplyBtnClickListener;
    }


    private class CommentHolder extends RecyclerView.ViewHolder implements OnCommentClickListener {

        private CommentView mCommentView;

        CommentHolder(View itemView) {
            super(itemView);
            CommentView commentView = (CommentView) itemView.findViewById(R.id.comment);
            commentView.init(mId, mType, getImageLoader(), this);
            this.mCommentView = commentView;
        }

        /**
         * add comment
         *
         * @param comment comment
         */
        public void addComment(Comment comment) {
            CommentView commentView = this.mCommentView;
            commentView.addComment(comment, getImageLoader(), this);
        }

        @Override
        public void onClick(View view, Comment comment) {

        }
    }


    private class CommentAdapter extends BaseRecyclerAdapter<Comment> {

        private CommentHolder commentHolder;

        CommentAdapter(Context context) {
            super(context, ONLY_FOOTER);
            mState = STATE_HIDE;
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position, long itemId) {
                    //  CommentsActivity.this.onItemClick(getItem(position));
                }
            });
        }

        @Override
        protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.activity_comment_item, parent, false);
            return new CommentHolder(view);
        }

        @Override
        protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Comment item, int position) {
            if (holder instanceof CommentHolder) {
                commentHolder = (CommentHolder) holder;
                RequestManager requestManager = getImageLoader();
                // if (requestManager != null)
                //  commentHolder.addComment(item, requestManager, getReplyBtnClickListener());
            }
        }
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
