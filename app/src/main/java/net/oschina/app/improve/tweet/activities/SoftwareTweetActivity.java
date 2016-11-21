package net.oschina.app.improve.tweet.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.behavior.CommentBar;
import net.oschina.app.improve.tweet.adapter.SoftwareTweetAdapter;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.ui.SelectFriendsActivity;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

import static net.oschina.app.improve.base.adapter.BaseRecyclerAdapter.ONLY_FOOTER;

/**
 * Created by fei on 2016/7/20.
 * desc:
 */

public class SoftwareTweetActivity extends BaseRecyclerViewActivity<Tweet> {
    public static final String BUNDLE_KEY_NAME = "bundle_key_name";
    private String softwareName;
    private ProgressDialog mDialog;
    private boolean mInputDoubleEmpty = false;

    private CommentBar mDelegation;

    public static void show(Context context, String tag) {
        Intent intent = new Intent(context, SoftwareTweetActivity.class);
        intent.putExtra(SoftwareTweetActivity.BUNDLE_KEY_NAME, tag);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_software_tweets;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        softwareName = bundle.getString(BUNDLE_KEY_NAME);
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mDelegation = CommentBar.delegation(this, (CoordinatorLayout) findViewById(R.id.coordinatorLayout));

        mDelegation.getBottomSheet().setCommitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendComment(mDelegation.getBottomSheet().getCommentText());
            }
        });
        mDelegation.getBottomSheet().getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
            }
        });

        mDelegation.getBottomSheet().setMentionListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountHelper.isLogin())
                    SelectFriendsActivity.show(SoftwareTweetActivity.this);
                else
                    LoginActivity.show(SoftwareTweetActivity.this);
            }
        });
    }


    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getSoftwareTweetList(softwareName, mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }


    /**
     * 检查当前数据,并检查网络状况
     *
     * @return 返回当前登录用户, 未登录或者未通过检查返回0
     */
    private long requestCheck() {

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


    private void handleSendComment(String content) {

        long uid = requestCheck();
        if (uid == 0)
            return;

        if (TextUtils.isEmpty(content)) {
            AppContext.showToastShort(R.string.tip_comment_content_empty);
            return;
        }

        OSChinaApi.pubSoftwareTweet(content + " #" + softwareName + "#", new TextHttpResponseHandler() {

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
                    Type type = new TypeToken<ResultBean<Tweet>>() {
                    }.getType();

                    ResultBean<Tweet> resultBean = AppOperator.createGson().fromJson(responseString, type);

                    if (resultBean.isSuccess()) {
                        onRefreshing();
                        mDelegation.getBottomSheet().dismiss();
                        mDelegation.getBottomSheet().getEditText().setText("");
                    }
                    hideWaitDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
                hideWaitDialog();
            }
        });
    }

    private void handleKeyDel() {

        if (TextUtils.isEmpty(mDelegation.getBottomSheet().getCommentText())) {
            if (mInputDoubleEmpty) {
                mDelegation.getCommentText().setHint("发表评论");
                mDelegation.getBottomSheet().getEditText().setHint("发表评论");
            } else {
                mInputDoubleEmpty = true;
            }
        } else {
            mInputDoubleEmpty = false;
        }
    }


    private ProgressDialog showWaitDialog(int messageId) {
        String message = getResources().getString(messageId);
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(this, message);
        }

        mDialog.setMessage(message);
        mDialog.show();

        return mDialog;
    }

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
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Tweet>>>() {
        }.getType();
    }

    @Override
    protected BaseRecyclerAdapter<Tweet> getRecyclerAdapter() {

        SoftwareTweetAdapter tweetAdapter = new SoftwareTweetAdapter(this, ONLY_FOOTER);
        tweetAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position, long itemId) {

                final Tweet tweet = mAdapter.getItem(position);
                final long sourceId = tweet.getId();

                long id = tweet.getAuthor().getId();
                long loginUid = AccountHelper.getUserId();
                if (id == loginUid) {

                    DialogHelper.getConfirmDialog(SoftwareTweetActivity.this, "删除该动弹?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OSChinaApi.delSoftwareTweet(sourceId, new TextHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Toast.makeText(SoftwareTweetActivity.this, "删除失败...", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                    try {
                                        Type type = new TypeToken<ResultBean>() {
                                        }.getType();
                                        ResultBean resultBean = AppOperator.createGson().fromJson(responseString, type);
                                        if (resultBean.getCode() == 1) {
                                            Toast.makeText(SoftwareTweetActivity.this, "删除成功...", Toast.LENGTH_SHORT).show();
                                            onRefreshing();
                                        } else {
                                            Toast.makeText(SoftwareTweetActivity.this, "删除失败...", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onFailure(statusCode, headers, responseString, e);
                                    }
                                }
                            });

                        }
                    }).create().show();

                }

            }
        });
        return tweetAdapter;
    }

    @Override
    protected void onItemClick(Tweet item, int position) {
        super.onItemClick(item, position);
        TweetDetailActivity.show(this, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            mDelegation.getBottomSheet().handleSelectFriendsResult(data);
        }
    }
}
