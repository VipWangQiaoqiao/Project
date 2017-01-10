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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.tweet.activities.TweetPublishActivity;
import net.oschina.app.improve.user.adapter.UserTweetAdapter;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹列表
 * Created by huanghaibin_dev
 * Updated by thanatosx
 * on 2016/7/18.
 */
public class TweetFragment extends BaseGeneralRecyclerFragment<Tweet>
        implements BaseRecyclerAdapter.OnItemLongClickListener {

    public static final int CATALOG_NEW = 0X0001;
    public static final int CATALOG_HOT = 0X0002;
    public static final int CATALOG_MYSELF = 0X0003;
    public static final int CATALOG_FRIENDS = 0X0004;
    public static final int CATALOG_TAG = 0X0005;
    public static final int CATALOG_SOMEONE = 0X0006;

    public static final String CACHE_NEW_TWEET = "cache_new_tweet";
    public static final String CACHE_HOT_TWEET = "cache_hot_tweet";
    public static final String CACHE_USER_TWEET = "cache_user_tweet";
    public static final String CACHE_USER_FRIEND = "cache_user_friend";
    public static final String CACHE_USER_TAG = "cache_user_tag";

    public static final String BUNDLE_KEY_USER_ID = "BUNDLE_KEY_USER_ID";
    public static final String BUNDLE_KEY_TAG = "BUNDLE_KEY_LOGIN_USER_TAG";
    public static final String BUNDLE_KEY_REQUEST_CATALOG = "BUNDLE_KEY_REQUEST_CATALOG";

    public int mReqCatalog;//请求类型
    public long mUserId; // login user or another user
    public String tag;
    private LoginReceiver mReceiver;

    public static Fragment instantiate(long uid) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_USER_ID, uid);
        bundle.putInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_MYSELF);
        Fragment fragment = new TweetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * @param uid  user id
     * @param code 只是为了让方法指纹不一样而已，哈哈
     * @return {@link Fragment}
     */
    public static Fragment instantiate(long uid, int code) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_USER_ID, uid);
        bundle.putInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_SOMEONE);
        Fragment fragment = new TweetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment instantiate(String tag) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_TAG, tag);
        bundle.putInt(BUNDLE_KEY_REQUEST_CATALOG, CATALOG_TAG);
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
        switch (mReqCatalog) {
            case CATALOG_FRIENDS:
            case CATALOG_SOMEONE:
            case CATALOG_MYSELF:
                mUserId = bundle.getLong(BUNDLE_KEY_USER_ID, AccountHelper.getUserId());
                break;
            case CATALOG_TAG:
                tag = bundle.getString(BUNDLE_KEY_TAG);
                setHasOptionsMenu(true);
                break;
        }
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
        mUserId = bundle.getLong(BUNDLE_KEY_USER_ID, AccountHelper.getUserId());
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
            default:
                CACHE_NAME = null;
        }

        super.initData();

        mAdapter.setOnItemLongClickListener(this);
        // 某用户的动弹 or 登录用户的好友动弹
        if (mUserId == 0 && mReqCatalog == CATALOG_MYSELF ||
                (!AccountHelper.isLogin() && mReqCatalog == CATALOG_FRIENDS)) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            mErrorLayout.setErrorMessage("未登录");
        }
    }

    @Override
    public void onLongClick(final int position, long itemId) {
        final Tweet tweet = mAdapter.getItem(position);
        if (tweet == null) return;

        List<String> operators = new ArrayList<>();
        operators.add(getString(R.string.copy));
        if (AccountHelper.getUserId() == (int) tweet.getAuthor().getId()) {
            operators.add(getString(R.string.delete));
        }
        operators.add(getString(R.string.transmit));

        final String[] os = new String[operators.size()];
        operators.toArray(os);

        DialogHelper.getSelectDialog(getContext(), os, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index) {
                        switch (index) {
                            case 0:
                                TDevice.copyTextToBoard(HTMLUtil.delHTMLTag(tweet.getContent()));
                                break;
                            case 1:
                                if (os.length != 2) {
                                    DialogHelper.getConfirmDialog(getActivity(), "是否删除该动弹?",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    OSChinaApi.deleteTweet(tweet.getId(), new DeleteHandler(position));
                                                }
                                            }).show();
                                    break;
                                }
                            case 2:
                                String content = null;
                                About.Share share;
                                if (tweet.getAbout() == null) {
                                    share = About.buildShare(tweet.getId(), OSChinaApi.CATALOG_TWEET);
                                    share.title = tweet.getAuthor().getName();
                                    share.content = tweet.getContent();
                                } else {
                                    share = About.buildShare(tweet.getAbout());
                                    content = "//@" + tweet.getAuthor().getName() + " :" + tweet.getContent();
                                    content = AssimilateUtils.clearHtmlTag(content).toString();
                                }
                                share.commitTweetId = tweet.getId();
                                share.fromTweetId = tweet.getId();
                                TweetPublishActivity.show(getContext(), null, content, share);
                                break;
                        }
                    }
                }).show();
    }

    private class LoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AccountHelper.isLogin()) {
                mUserId = AccountHelper.getUserId();
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                onRefreshing();
            } else {
                mUserId = 0;
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                mErrorLayout.setErrorMessage("未登录");
            }
        }
    }

    @Override
    protected void requestData() {
        super.requestData();
        String pageToken = isRefreshing ? null : mBean.getNextPageToken();
        switch (mReqCatalog) {
            case CATALOG_NEW:
                OSChinaApi.getTweetList(null, null, 1, 1, pageToken, mHandler);
                break;
            case CATALOG_HOT:
                OSChinaApi.getTweetList(null, null, 1, 2, pageToken, mHandler);
                break;
            case CATALOG_SOMEONE:
            case CATALOG_MYSELF:
                if (mUserId <= 0) break;
                OSChinaApi.getTweetList(mUserId, null, null, 1, pageToken, mHandler);
                break;
            case CATALOG_FRIENDS:
                OSChinaApi.getTweetList(null, null, 2, 1, pageToken, mHandler);
                break;
            case CATALOG_TAG:
                OSChinaApi.getTweetList(null, tag, null, 1, pageToken, mHandler);
                break;
        }
    }

    @Override
    protected boolean isNeedEmptyView() {
        return mReqCatalog != CATALOG_TAG && mReqCatalog != CATALOG_SOMEONE;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mReqCatalog == CATALOG_TAG) {
            inflater.inflate(R.menu.pub_topic_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.public_menu_send:
                TweetPublishActivity.show(getContext(), null, "#" + tag + "#");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemClick(int position, long itemId) {
        Tweet tweet = mAdapter.getItem(position);
        if (tweet == null) return;
        TweetDetailActivity.show(getContext(), tweet);
    }

    /**
     * 未登录时显示的图标，点击应该跳到的登录界面
     *
     * @param v {@link View}
     */
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

    @Override
    protected BaseRecyclerAdapter<Tweet> getRecyclerAdapter() {
        return new UserTweetAdapter(this);
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
        outState.putLong(BUNDLE_KEY_USER_ID, mUserId);
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setListData(ResultBean<PageBean<Tweet>> resultBean) {
        mBean.setNextPageToken(resultBean.getResult().getNextPageToken());
        if (isRefreshing) {
            //cache the time
            mBean.setItems(resultBean.getResult().getItems());
            mAdapter.clear();
            // 主要是为了清理重复数据， stupid
            ((BaseGeneralRecyclerAdapter) mAdapter).clearPreItems();
            ((BaseGeneralRecyclerAdapter) mAdapter).addItems(mBean.getItems());

            mBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());
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

        if (resultBean.getResult().getItems() == null
                || resultBean.getResult().getItems().size() < 20)
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
//        mAdapter.setState(resultBean.getResult().getItems() == null
//                || resultBean.getResult().getItems().size() < 20
//                ? BaseRecyclerAdapter.STATE_NO_MORE
//                : BaseRecyclerAdapter.STATE_LOADING, true);

        if (mAdapter.getItems().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(isNeedEmptyView() ? EmptyLayout.NODATA : EmptyLayout.HIDE_LAYOUT);
        }
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
            mUserId = AccountHelper.getUserId();
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
            SimplexToast.show(getContext(), "删除失败");
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
                onFailure(statusCode, headers, responseString, e);
            }
        }
    }
}
