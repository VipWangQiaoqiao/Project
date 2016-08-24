package net.oschina.app.improve.tweet.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.activities.BaseRecyclerViewActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.tweet.adapter.SoftwareTweetAdapter;
import net.oschina.app.util.DialogHelp;
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
    //private static final String TAG = "SoftwareTweetActivity";
    private String softwareName;
    private EditText mETInput;
    private ProgressDialog mDialog;
    private boolean mInputDoubleEmpty = false;

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

        mETInput = (EditText) findViewById(R.id.et_input);

        mETInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    String input = mETInput.getText().toString().trim();
                    handleSendComment(input);

                    return true;
                }
                return false;
            }
        });

        mETInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    handleKeyDel();
                }
                return false;
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
    private int requestCheck() {

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


    private void handleSendComment(String content) {

        int uid = requestCheck();
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

                    ResultBean<Tweet> resultBean = AppContext.createGson().fromJson(responseString, type);

                    if (resultBean.isSuccess()) {
                        onRefreshing();
                        TDevice.hideSoftKeyboard(mETInput);
                        mETInput.setText(null);
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

        if (TextUtils.isEmpty(mETInput.getText())) {
            if (mInputDoubleEmpty) {
                mETInput.setHint("发表评论");
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
            mDialog = DialogHelp.getWaitDialog(this, message);
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
                int loginUid = AppContext.getInstance().getLoginUid();
                if (id == loginUid) {

                    DialogHelp.getConfirmDialog(SoftwareTweetActivity.this, "删除该动弹?", new DialogInterface.OnClickListener() {
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
                                        ResultBean resultBean = AppContext.createGson().fromJson(responseString, type);
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
                    }, null).create().show();

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


}
