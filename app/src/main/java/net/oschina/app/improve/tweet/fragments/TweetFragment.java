package net.oschina.app.improve.tweet.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Constants;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.account.base.AccountBaseActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseGeneralRecyclerFragment;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.user.adapter.UserTweetAdapter;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;

import org.json.JSONObject;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹列表
 * Created by huanghaibin_dev
 * Updated by thanatosx
 * on 2016/7/18.
 */
public class TweetFragment extends BaseGeneralRecyclerFragment<Tweet> {

    public static final int CATALOG_NEW = 0X0001;
    public static final int CATALOG_HOT = 0X0002;
    public static final int CATALOG_MYSELF = 0X0003;
    public static final int CATALOG_FRIENDS = 0X0004;

    public static final String CACHE_NEW_TWEET = "cache_new_tweet";
    public static final String CACHE_HOT_TWEET = "cache_hot_tweet";
    public static final String CACHE_USER_TWEET = "cache_user_tweet";
    public static final String CACHE_USER_FRIEND = "cache_user_friend";

    public static final String BUNDLE_KEY_LOGIN_USER_ID = "BUNDLE_KEY_LOGIN_USER_ID";
    public static final String BUNDLE_KEY_REQUEST_CATALOG = "BUNDLE_KEY_REQUEST_CATALOG";

    public int mReqCatalog;//请求类型
    public long mLoginUserId;
    private LoginReceiver mReceiver;

    public static Fragment instantiate(long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_LOGIN_USER_ID, uid);
        bundle.putInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_MYSELF);
        Fragment fragment = new TweetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment instantiate(int catalog) {
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_REQUEST_CATALOG, catalog);
        Fragment fragment = new TweetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mReqCatalog = bundle.getInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_NEW);
        mLoginUserId = bundle.getLong(BUNDLE_KEY_LOGIN_USER_ID, AccountHelper.getUserId());
    }

    /**
     * fragment被销毁的时候重新调用，初始化保存的数据
     *
     * @param bundle onSaveInstanceState
     */
    @Override
    protected void onRestartInstance(Bundle bundle) {
        super.onRestartInstance(bundle);
        mReqCatalog = bundle.getInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_NEW);
        mLoginUserId = bundle.getLong(BUNDLE_KEY_LOGIN_USER_ID, AccountHelper.getUserId());
    }

    @Override
    public void initData() {
        switch (mReqCatalog) {
            case CATALOG_NEW:
                CACHE_NAME = CACHE_NEW_TWEET;
                break;
            case CATALOG_HOT:
                CACHE_NAME = CACHE_HOT_TWEET;
                break;
            case CATALOG_MYSELF:
            case CATALOG_FRIENDS:
                CACHE_NAME = mReqCatalog == CATALOG_MYSELF ? CACHE_USER_TWEET : CACHE_USER_FRIEND;
                if (mReceiver == null) {
                    mReceiver = new LoginReceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(AccountBaseActivity.ACTION_ACCOUNT_FINISH_ALL);
                    filter.addAction(Constants.INTENT_ACTION_LOGOUT);
                    LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
                }
                break;
        }

        super.initData();

        mAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position, long itemId) {
                Tweet tweet = mAdapter.getItem(position);
                if (tweet != null) {
                    handleLongClick(tweet, position);
                }
            }
        });

        // 某用户的动弹 or 登录用户的好友动弹
        if (mLoginUserId == 0 && mReqCatalog == CATALOG_MYSELF ||
                (!AccountHelper.isLogin() && mReqCatalog == CATALOG_FRIENDS)) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            mErrorLayout.setErrorMessage("未登录");
        }
    }

    private class LoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AccountHelper.isLogin()) {
                mLoginUserId = AccountHelper.getUserId();
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                onRefreshing();
            } else {
                mLoginUserId = 0;
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                mErrorLayout.setErrorMessage("未登录");
            }
        }
    }

    @Override
    protected void requestData() {
        super.requestData();
        String pageToken = mIsRefresh ? null : mBean.getNextPageToken();
        switch (mReqCatalog) {
            case CATALOG_NEW:
                OSChinaApi.getTweetList(null, null, 1, 1, pageToken, mHandler);
                break;
            case CATALOG_HOT:
                OSChinaApi.getTweetList(null, null, 1, 2, pageToken, mHandler);
                break;
            case CATALOG_MYSELF:
                if (mLoginUserId == 0) break;
                OSChinaApi.getTweetList(mLoginUserId, null, null, null, pageToken, mHandler);
                break;
            case CATALOG_FRIENDS:
                OSChinaApi.getTweetList(null, null, 2, 1, pageToken, mHandler);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemClick(int position, long itemId) {
        Tweet tweet = mAdapter.getItem(position);
        if (tweet == null) return;
        TweetDetailActivity.show(getContext(), tweet);
    }

    @Override
    public void onClick(View v) {
        if ((mReqCatalog == CATALOG_MYSELF || mReqCatalog == CATALOG_FRIENDS)
                && !AccountHelper.isLogin()) {
            //UIHelper.showLoginActivity(getActivity());
            LoginActivity.show(this, 1);
        } else {
            super.onClick(v);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setListData(ResultBean<PageBean<Tweet>> resultBean) {
        mBean.setNextPageToken((resultBean == null ? null : resultBean.getResult().getNextPageToken()));
        if (mIsRefresh) {
            //cache the time
            mBean.setItems(resultBean.getResult().getItems());
            mAdapter.clear();

            ((BaseGeneralRecyclerAdapter) mAdapter).clearPreItems();
            ((BaseGeneralRecyclerAdapter) mAdapter).addItems(mBean.getItems());

            mBean.setPrevPageToken((resultBean == null ? null : resultBean.getResult().getPrevPageToken()));
            mRefreshLayout.setCanLoadMore(true);
            if (isNeedCache()) {
                AppOperator.runOnThread(new Runnable() {
                    @Override
                    public void run() {
                        CacheManager.saveToJson(getActivity(), CACHE_NAME, mBean.getItems());
                    }
                });
            }
        } else {
            ((BaseGeneralRecyclerAdapter) mAdapter).addItems(resultBean.getResult().getItems());
        }

        mAdapter.setState(resultBean.getResult().getItems() == null || resultBean.getResult().getItems().size() < 20 ? BaseRecyclerAdapter.STATE_NO_MORE : BaseRecyclerAdapter.STATE_LOADING, true);

        if (mAdapter.getItems().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(isNeedEmptyView() ? EmptyLayout.NODATA : EmptyLayout.HIDE_LAYOUT);
        }
    }

    private void handleLongClick(final Tweet tweet, final int position) {
        String[] items;
        if (AccountHelper.getUserId() == (int) tweet.getAuthor().getId()) {
            items = new String[]{getString(R.string.copy), getString(R.string.delete)};
        } else {
            items = new String[]{getString(R.string.copy)};
        }

        DialogHelper.getSelectDialog(getActivity(), items, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:
                        TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(tweet.getContent()));
                        break;
                    case 1:
                        DialogHelper.getConfirmDialog(getActivity(), "是否删除该动弹?", new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                OSChinaApi.deleteTweet(tweet.getId(), new DeleteHandler(position));
                            }
                        }).show();
                        break;
                }
            }
        }).show();
    }


    @Override
    protected BaseRecyclerAdapter<Tweet> getRecyclerAdapter() {
        return new UserTweetAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Tweet>>>() {
        }.getType();
    }

    @Override
    protected Class<Tweet> getCacheClass() {
        return Tweet.class;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(BUNDLE_KEY_REQUEST_CATALOG, mReqCatalog);
        outState.putLong(BUNDLE_KEY_LOGIN_USER_ID, mLoginUserId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 注销广播
     */
    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 1) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            mLoginUserId = AccountHelper.getUserId();
            onRefreshing();
        }
    }

    class DeleteHandler extends TextHttpResponseHandler {
        private int position;

        DeleteHandler(int position) {
            this.position = position;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.optInt("code") == 1) {
                    mAdapter.removeItem(position);
                    Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
