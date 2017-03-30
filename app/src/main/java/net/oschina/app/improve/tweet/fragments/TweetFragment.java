package net.oschina.app.improve.tweet.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Constants;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.account.base.AccountBaseActivity;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseGeneralRecyclerFragment;
import net.oschina.app.improve.bean.Tweet;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.tweet.activities.TweetDetailActivity;
import net.oschina.app.improve.tweet.activities.TweetPublishActivity;
import net.oschina.app.improve.tweet.service.TweetNotificationManager;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.user.adapter.UserTweetAdapter;
import net.oschina.app.improve.utils.CacheManager;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.utils.parser.TweetParser;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.TDevice;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;


/**
 * 动弹列表
 * Created by huanghaibin_dev
 * Updated by thanatosx
 * on 2016/7/18.
 * Updated by jzz
 * on 2017/02/15
 */
public class TweetFragment extends BaseGeneralRecyclerFragment<Tweet>
        implements BaseRecyclerAdapter.OnItemLongClickListener, TweetNotificationManager.TweetPubNotify {

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

    public static final String BUNDLE_KEY_USER_ID = "BUNDLE_KEY_USER_ID";
    public static final String BUNDLE_KEY_TAG = "BUNDLE_KEY_LOGIN_USER_TAG";
    public static final String BUNDLE_KEY_REQUEST_CATALOG = "BUNDLE_KEY_REQUEST_CATALOG";

    public int mReqCatalog;//请求类型
    public long mUserId; // login user or another user
    public String tag;
    private LoginReceiver mReceiver;

    @Bind(R.id.lay_notification)
    LinearLayout mLayNotification;
    @Bind(R.id.bt_ignore)
    Button mBtIgnore;
    @Bind(R.id.bt_retry)
    Button mBtRetry;
    @Bind(R.id.notification_baseline)
    View mBaseLine;

    private String[] mPubFailedCacheIds;
    private boolean isShowIdentityView;

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
    @SuppressWarnings("unused")
    public static Fragment instantiate(long uid, int code, boolean isShowIdentityView) {
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_USER_ID, uid);
        bundle.putBoolean("isShowIdentityView", isShowIdentityView);
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
        isShowIdentityView = bundle.getBoolean("isShowIdentityView", true);
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
    public void onResume() {
        super.onResume();
        if (mReqCatalog == 1) {
            //每次进入或回到最新动弹界面先查询是否有本地发送失败的动弹缓存
            if (AccountHelper.isLogin()) {
                TweetPublishService.startActionSearchFailed(AppContext.context());
            } else {
                showDraftsBox(View.GONE);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TweetNotificationManager.setup(getContext());
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TweetNotificationManager.bindNotify(getContext().getApplicationContext(), this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        TweetNotificationManager.unBoundNotify(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
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
                                    content = TweetParser.getInstance().clearHtmlTag(content).toString();
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
                //注册草稿箱查询操作
                TweetPublishService.startActionSearchFailed(AppContext.context());
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
    @OnClick({R.id.bt_ignore, R.id.bt_retry})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ignore:
                if (AccountHelper.isLogin()) {
                    //忽略该动弹
                    showDraftsBox(View.GONE);
                    AppContext.showToastShort(R.string.tweet_ignore_hint);
                    if (mPubFailedCacheIds == null) return;
                    for (String id : mPubFailedCacheIds) {
                        if (TextUtils.isEmpty(id)) continue;
                        TweetPublishService.startActionDelete(getContext(), id);
                    }
                } else {
                    LoginActivity.show(this, 1);
                }
                break;
            case R.id.bt_retry:
                //重新发送动弹

                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(this, 1);
                    return;
                }

                if (!TDevice.hasInternet()) {
                    AppContext.showToastShort(R.string.tip_network_error);
                    return;
                }

                AppContext.showToastShort(R.string.tweet_retry_publishing_hint);

                showDraftsBox(View.GONE);
                if (mPubFailedCacheIds == null) return;
                for (String id : mPubFailedCacheIds) {
                    if (TextUtils.isEmpty(id)) continue;
                    TweetPublishService.startActionContinue(getContext(), id);
                }
                break;
            default:
                if ((mReqCatalog == CATALOG_MYSELF || mReqCatalog == CATALOG_FRIENDS)
                        && !AccountHelper.isLogin()) {
                    LoginActivity.show(this, 1);
                } else {
                    super.onClick(v);
                }
                break;
        }
    }

    @Override
    protected BaseRecyclerAdapter<Tweet> getRecyclerAdapter() {
        UserTweetAdapter adapter = new UserTweetAdapter(this);
        adapter.setShowIdentityView(isShowIdentityView);
        return adapter;
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
        if (resultBean != null) {
            final PageBean<Tweet> pageBean = resultBean.getResult();
            if (pageBean != null) {
                final List<Tweet> items = pageBean.getItems();
                final boolean isEmpty = items == null || items.size() == 0;
                if (!isEmpty)
                    mBean.setNextPageToken(pageBean.getNextPageToken());

                if (isRefreshing) {
                    AppConfig.getAppConfig(getActivity()).set("system_time", resultBean.getTime());
                    mAdapter.clear();
                    ((BaseGeneralRecyclerAdapter) mAdapter).clearPreItems();
                    ((BaseGeneralRecyclerAdapter) mAdapter).addItems(items);

                    mBean.setItems(items);
                    mBean.setPrevPageToken(pageBean.getPrevPageToken());
                    mRefreshLayout.setCanLoadMore(true);
                    if (isNeedCache()) {
                        CacheManager.saveToJson(getActivity(), CACHE_NAME, items);
                    }
                } else {
                    ((BaseGeneralRecyclerAdapter) mAdapter).addItems(items);
                }

                if (isEmpty) {
                    mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
                    mRefreshLayout.setCanLoadMore(false);
                }
            }
        }

        if (mAdapter.getItems().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(isNeedEmptyView() ? EmptyLayout.NODATA : EmptyLayout.HIDE_LAYOUT);
        }
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

    @Override
    public void onTweetPubSuccess() {
        //如果再次发送动弹成功，gone草稿箱view
        showDraftsBox(View.GONE);
    }

    @Override
    public void onTweetPubFailed() {
        //发送动弹失败，显示草稿箱view
        showDraftsBox(View.VISIBLE);
    }

    @Override
    public void onTweetPubProgress(String progressContent) {
    }

    @Override
    public void onTweetPubContinue() {

    }

    @Override
    public void onTweetPubDelete() {
        //忽略该条动弹之后，直接gone草稿箱view
        showDraftsBox(View.GONE);
    }

    @Override
    public void pnTweetReceiverSearchFailed(String[] pubFailedCacheIds) {
        this.mPubFailedCacheIds = pubFailedCacheIds;
        if (mReqCatalog == CATALOG_NEW && pubFailedCacheIds != null && pubFailedCacheIds.length > 0) {
            showDraftsBox(View.VISIBLE);
        } else {
            showDraftsBox(View.GONE);
        }
    }

    private class DeleteHandler extends TextHttpResponseHandler {
        private int position;
        private ProgressDialog dialog;

        DeleteHandler(int position) {
            this.position = position;
            this.dialog = DialogHelper.getProgressDialog(getContext(), "正在删除……", false);
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

        @Override
        public void onStart() {
            super.onStart();
            dialog.show();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dialog.dismiss();
        }
    }

    private void showDraftsBox(int GoneOrVisible) {
        if (mLayNotification != null)
            mLayNotification.setVisibility(GoneOrVisible);
        if (mBtIgnore != null)
            mBtIgnore.setVisibility(GoneOrVisible);
        if (mBtRetry != null)
            mBtRetry.setVisibility(GoneOrVisible);
        if (mBaseLine != null)
            mBaseLine.setVisibility(GoneOrVisible);
    }
}
