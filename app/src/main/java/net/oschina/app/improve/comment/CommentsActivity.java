package net.oschina.app.improve.comment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.comment.adapter.CommentAdapter;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.user.activities.UserSelectFriendsActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.adapter.OnKeyArrivedListenerAdapter;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

import static net.oschina.app.R.id.tv_back_label;

/**
 * Created by  fei
 * on  16/11/17
 * desc:详情评论列表ui
 */
public class CommentsActivity extends BaseBackActivity implements BaseRecyclerAdapter.OnItemLongClickListener {

    @Bind(R.id.lay_refreshLayout)
    RecyclerRefreshLayout mRefreshLayout;

    @Bind(R.id.lay_blog_detail_comment)
    RecyclerView mLayComments;

    @Bind(R.id.activity_comments)
    CoordinatorLayout mCoordinatorLayout;

    @Bind(tv_back_label)
    TextView mBack_label;
    @Bind(R.id.tv_title)
    TextView mTitle;
    private CommentAdapter mCommentAdapter;

    private CommentBar mDelegation;
    private ProgressDialog mDialog;
    private boolean mInputDoubleEmpty = true;

    private TextHttpResponseHandler mHandler = new TextHttpResponseHandler() {
        @Override
        public void onStart() {
            super.onStart();
            showWaitDialog(R.string.progress_submit);
        }

        @Override
        public void onFinish() {
            super.onFinish();
            hideWaitDialog();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            AppContext.showToast(getResources().getString(R.string.pub_comment_failed));
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
                        handleSyncTweet();
                        mDelegation.setCommentHint(getString(mSourceId));
                        mDelegation.getBottomSheet().getEditText().setHint(getString(mSourceId));
                        Toast.makeText(CommentsActivity.this, getString(R.string.pub_comment_success), Toast.LENGTH_SHORT).show();
                        mDelegation.getBottomSheet().getEditText().setText("");
                        mDelegation.getBottomSheet().getBtnCommit().setTag(null);
                        mDelegation.getBottomSheet().dismiss();
                        getData(true, null);
                    }
                } else {
                    AppContext.showToastShort(resultBean.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    };

    private int mOrder;
    private int mSourceId;

    private long mId;
    private int mType;

    private PageBean<Comment> mPageBean;

    public static void show(Context context, long id, int type, int order) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("type", type);
        intent.putExtra("order", order);
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
        mOrder = bundle.getInt("order");
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mBack_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDelegation = CommentBar.delegation(this, mCoordinatorLayout);
        mSourceId = R.string.pub_comment_hint;
        if (mType == OSChinaApi.COMMENT_QUESTION) {
            mSourceId = R.string.answer_hint;
        }
        if (mType == OSChinaApi.COMMENT_EVENT) {
            mSourceId = R.string.comment_hint;
        }
        mDelegation.getBottomSheet().getEditText().setHint(mSourceId);
        mDelegation.hideFav();
        mDelegation.hideShare();

        if (mType == OSChinaApi.COMMENT_SOFT) {
            mDelegation.getBottomSheet().hideSyncAction();
        }

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin()) {
                    UserSelectFriendsActivity.show(CommentsActivity.this, mDelegation.getBottomSheet().getEditText());
                } else {
                    LoginActivity.show(CommentsActivity.this);
                }
            }
        });

        mDelegation.getBottomSheet().getEditText().setOnKeyArrivedListener(new OnKeyArrivedListenerAdapter(this));

        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText view = (EditText) v;
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Button mBtnView = mDelegation.getBottomSheet().getBtnCommit();
                    Object o = mBtnView.getTag();
                    if (o == null) return false;
                    if (!TextUtils.isEmpty(view.getText().toString())) {
                        mInputDoubleEmpty = false;
                        return false;
                    }
                    if (TextUtils.isEmpty(view.getText().toString()) && !mInputDoubleEmpty) {
                        mInputDoubleEmpty = true;
                        return false;
                    }
                    mBtnView.setTag(null);
                    view.setHint(mSourceId);
                    return true;
                }
                return false;
            }
        });

        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(mType, mId, (Comment) v.getTag(), mDelegation.getBottomSheet().getCommentText());
            }
        });

        mRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mLayComments.setLayoutManager(manager);

        mCommentAdapter = new CommentAdapter(this, getImageLoader(), BaseRecyclerAdapter.ONLY_FOOTER);
        mCommentAdapter.setSourceId(mId);
        mCommentAdapter.setCommentType(mType);
        mCommentAdapter.setDelegation(mDelegation);
        mCommentAdapter.setOnItemLongClickListener(this);
        mCommentAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {

                Comment comment = mCommentAdapter.getItem(position);

                if (mType == OSChinaApi.COMMENT_QUESTION) {
                    QuesAnswerDetailActivity.show(CommentsActivity.this, comment, mId, mType);
                }
            }
        });
        mLayComments.setAdapter(mCommentAdapter);
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
                //第一次请求初始化数据
                getData(true, null);

            }
        });

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
     * sync the tweet
     */
    private void handleSyncTweet() {
        if (mDelegation.getBottomSheet().isSyncToTweet()) {
            TweetPublishService.startActionPublish(CommentsActivity.this,
                    mDelegation.getBottomSheet().getCommentText(), null,
                    About.buildShare(mId, mType));
        }
    }

    /**
     * handle send comment
     */
    private void sendComment(int type, long id, Comment comment, String content) {
        if (requestCheck() == 0)
            return;

        if (TextUtils.isEmpty(content)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }

        long uid = comment == null ? 0 : comment.getAuthor().getId();
        long cid = comment == null ? 0 : comment.getId();

        switch (type) {
            case OSChinaApi.COMMENT_QUESTION:
                OSChinaApi.pubQuestionComment(id, cid, uid, content, mHandler);
                break;
            case OSChinaApi.COMMENT_BLOG:
                OSChinaApi.pubBlogComment(id, cid, uid, content, mHandler);
                break;
            case OSChinaApi.COMMENT_TRANSLATION:
                OSChinaApi.pubTranslateComment(id, cid, uid, content, mHandler);
                break;
            case OSChinaApi.COMMENT_EVENT:
                if (comment != null) {
                    content = "回复@" + comment.getAuthor().getName() + " : " + content;
                }
                OSChinaApi.pubEventComment(id, 0, 0, content, mHandler);
                break;
            case OSChinaApi.COMMENT_NEWS:
                OSChinaApi.pubNewsComment(id, cid, uid, content, mHandler);
                break;
            case OSChinaApi.COMMENT_SOFT:
                OSChinaApi.pubSoftComment(id, 0, 0, content, mHandler);
                break;
            default:
                break;
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
        OSChinaApi.getComments(mId, mType, "refer,reply", mOrder, token, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mCommentAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRefreshLayout.onComplete();
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {

                    ResultBean<PageBean<Comment>> resultBean = AppOperator.createGson().fromJson(responseString, getCommentType());

                    if (resultBean.isSuccess()) {
                        mPageBean = resultBean.getResult();
                        int titleHintId = R.string.comment_title_hint;
                        if (mType == OSChinaApi.COMMENT_EVENT || mType == OSChinaApi.COMMENT_QUESTION) {
                            titleHintId = R.string.answer_hint;
                        }
                        mTitle.setText(String.format("%d%s%s", mPageBean.getTotalResults(), getString(R.string.item_hint), getString(titleHintId)));
                        handleData(mPageBean.getItems(), clearData);
                    }

                    mCommentAdapter.setState(
                            mPageBean == null || mPageBean.getItems() == null || mPageBean.getItems().size() < 20 ?
                                    BaseRecyclerAdapter.STATE_NO_MORE : BaseRecyclerAdapter.STATE_LOAD_MORE, true);
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
        mCommentAdapter.addAll(comments);
    }

    @Override
    public void onLongClick(int position, long itemId) {

        final Comment comment = mCommentAdapter.getItem(position);
        if (comment == null) return;

        String[] items;
        // if (AccountHelper.getUserId() == (int) comment.getAuthor().getId()) {
        //   items = new String[]{getString(R.string.copy), getString(R.string.delete)};
        //} else {
        items = new String[]{getString(R.string.copy)};
        // }

        DialogHelper.getSelectDialog(this, items, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:
                        TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(comment.getContent()));
                        break;
                    case 1:
                        // TODO: 2016/11/30 delete comment
                        break;
                    default:
                        break;
                }
            }
        }).show();

    }
}
