package net.oschina.app.improve.user.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.adapter.UserSearchFriendsAdapter;
import net.oschina.app.improve.user.adapter.UserSelectFriendsAdapter;
import net.oschina.app.improve.user.bean.UserFansOrFollows;
import net.oschina.app.improve.user.bean.UserFriend;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.app.widget.IndexView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

import static net.oschina.app.api.remote.OSChinaApi.TYPE_USER_FOLOWS;

/**
 * Created by fei
 * on 2016/12/22.
 * desc:
 */

public class UserSelectFriendsActivity extends BaseBackActivity implements IndexView.OnIndexTouchListener {

    private static final String TAG = "UserSelectFriendsActivity";

    private PageBean<UserFansOrFollows> mPageBean;

    @Bind(R.id.searcher_friends)
    SearchView mSearchView;
    @Bind(R.id.recycler_friends_icon)
    HorizontalScrollView mRecyclerFriendsIcon;

    @Bind(R.id.tv_label)
    TextView mTvLabel;

    @Bind(R.id.recycler_friends)
    RecyclerView mRecyclerFriends;
    @Bind(R.id.tv_index_show)
    TextView mTvIndexShow;

    @Bind(R.id.lay_index)
    IndexView mIndex;

    @Bind(R.id.lay_error)
    EmptyLayout mEmptyLayout;

    //网络初始化的adapter
    private UserSelectFriendsAdapter mLocalAdapter;

    //网络初始化的朋友数据
    private ArrayList<UserFriend> mCacheFriends;

    private UserSearchFriendsAdapter mSearchAdapter;


    public static void show(Context context) {
        Intent intent = new Intent(context, UserSelectFriendsActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_main_user_select_friends;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mRecyclerFriends.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerFriends.setAdapter(mLocalAdapter = new UserSelectFriendsAdapter(this));

        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyLayout emptyLayout = mEmptyLayout;
                if (emptyLayout != null && emptyLayout.getErrorState() != EmptyLayout.HIDE_LAYOUT) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    requestData();
                }
            }
        });

        mIndex.setOnIndexTouchListener(this);

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.e(TAG, "onClose: ---->");
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "onQueryTextSubmit: ---->" + query);
                return true;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextChange(String newText) {

                Log.e(TAG, "onQueryTextChange: -------->" + newText);

                if (TextUtils.isEmpty(newText)) {
                    mTvLabel.setVisibility(View.GONE);
                    mIndex.setVisibility(View.VISIBLE);

                    mRecyclerFriends.setAdapter(mLocalAdapter);

                    if (mSearchAdapter == null) {
                        mSearchAdapter = new UserSearchFriendsAdapter(UserSelectFriendsActivity.this);
                    }
                    mSearchAdapter.notifyDataSetChanged();
                    mSearchAdapter.setSearchContent(newText);
                    return false;
                } else {
                    if (mIndex.getVisibility() == View.VISIBLE) {
                        mIndex.setVisibility(View.GONE);
                    }
                    mTvLabel.setText("@" + newText);
                    mTvLabel.setVisibility(View.VISIBLE);

                    if (mRecyclerFriends.getAdapter() instanceof UserSelectFriendsAdapter) {
                        if (mSearchAdapter == null) {
                            mSearchAdapter = new UserSearchFriendsAdapter(UserSelectFriendsActivity.this);
                        }
                        mRecyclerFriends.setAdapter(mSearchAdapter);
                    }
                    queryUpdateView(newText);

                }

                mSearchAdapter.setSearchContent(newText);
                return true;

            }
        });

        mSearchView.post(new Runnable() {
            @SuppressWarnings("RestrictedApi")
            @Override
            public void run() {
                mSearchView.clearFocus();

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        requestData();
    }

    private boolean checkNetIsAvailable() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(getString(R.string.tip_network_error));
            showError(EmptyLayout.NETWORK_ERROR);
            return false;
        }
        return true;
    }

    private void hideLoading() {
        final EmptyLayout emptyLayout = mEmptyLayout;
        if (emptyLayout == null)
            return;
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_to_hide);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        emptyLayout.startAnimation(animation);
    }

    private void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
        }
    }

    private void requestData() {

        //检查网络
        if (!checkNetIsAvailable()) {
            showError(EmptyLayout.NETWORK_ERROR);
            return;
        }

        OSChinaApi.getUserFansOrFlows(TYPE_USER_FOLOWS, AccountHelper.getUserId(), mPageBean == null ?
                null : mPageBean.getNextPageToken(), new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                showError(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Type type = new TypeToken<ResultBean<PageBean<UserFansOrFollows>>>() {
                }.getType();

                ResultBean<PageBean<UserFansOrFollows>> resultBean = AppOperator.createGson().fromJson(responseString, type);

                if (resultBean.isSuccess()) {

                    List<UserFansOrFollows> fansOrFollows = resultBean.getResult().getItems();

                    if (fansOrFollows.size() > 0) {

                        updateView(fansOrFollows);

                        hideLoading();

                        mPageBean = resultBean.getResult();
                    } else {
                        showError(EmptyLayout.NODATA);
                    }


                } else {
                    showError(EmptyLayout.NODATA);
                }

            }
        });

    }

    private void queryUpdateView(String queryText) {

        String pinyinQueryText = AssimilateUtils.returnPinyin(queryText, false);

        //缓存的搜索好友列表
        ArrayList<UserFriend> searchFriends = new ArrayList<>();

        UserFriend LocalUserFriend = new UserFriend();

        LocalUserFriend.setName(getString(R.string.local_search_label));
        LocalUserFriend.setShowLabel(getString(R.string.local_search_label));
        LocalUserFriend.setShowViewType(UserSearchFriendsAdapter.INDEX_TYPE);
        searchFriends.add(LocalUserFriend);
        //Log.e(TAG, "初始化本地数据集label: ---->");

        //Log.e(TAG, "初始化点击label ----->");
        UserFriend NetUserFriend = new UserFriend();

        NetUserFriend.setName(getString(R.string.net_search_label));
        NetUserFriend.setShowLabel(getString(R.string.search_net_label));
        NetUserFriend.setShowViewType(UserSearchFriendsAdapter.SEARCH_TYPE);
        searchFriends.add(NetUserFriend);


        //缓存的本地好友列表
        ArrayList<UserFriend> cacheFriends = this.mCacheFriends;

        for (UserFriend friend : cacheFriends) {

            String name = friend.getName();

            if (TextUtils.isEmpty(name)) continue;

            //搜索列表当中没有该条数据，进行添加

            if (AssimilateUtils.returnPinyin(name, false).startsWith(pinyinQueryText)) {
                friend.setShowLabel(name);
                friend.setShowViewType(UserSearchFriendsAdapter.USER_TYPE);
                searchFriends.add(1, friend);
            }

        }
        mSearchAdapter.clear();
        mSearchAdapter.addItems(searchFriends);
        Log.e(TAG, "queryUpdateView: ----->size=" + searchFriends.size() + "  \r");
    }

    private void updateView(List<UserFansOrFollows> fansOrFollows) {

        if (mCacheFriends == null)
            mCacheFriends = new ArrayList<>();

        ArrayList<String> holdIndexes = new ArrayList<>();


        for (int i = fansOrFollows.size() - 1; i > 0; i--) {
            UserFansOrFollows fansOrFollow = fansOrFollows.get(i);

            //获得字符串
            String name = fansOrFollow.getName().trim();

            if (TextUtils.isEmpty(name)) continue;

            //返回小写拼音
            String pinyin = AssimilateUtils.returnPinyin(name, true);
            String label = pinyin.substring(0, 1);

            //判断是否hold住相同字母开头的数据
            if (!holdIndexes.contains(label)) {

                UserFriend userFriend = new UserFriend();
                userFriend.setShowLabel(label.matches("[a-zA-Z_]+") ? label : "#");
                userFriend.setShowViewType(UserSelectFriendsAdapter.INDEX_TYPE);

                mCacheFriends.add(userFriend);

                //加入hold
                holdIndexes.add(label);
            }

            UserFriend userFriend = new UserFriend();
            userFriend.setId(fansOrFollow.getId());
            userFriend.setShowLabel(pinyin);
            userFriend.setName(fansOrFollow.getName());
            userFriend.setShowViewType(UserSelectFriendsAdapter.USER_TYPE);
            userFriend.setPortrait(fansOrFollow.getPortrait());

            mCacheFriends.add(userFriend);
        }

        holdIndexes.clear();

        Log.e(TAG, "User Friends Size: ------->" + mCacheFriends.size());
        //自然排序
        Collections.sort(mCacheFriends);

        mLocalAdapter.addItems(mCacheFriends);
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public void onIndexTouchMove(char indexLetter) {
        Log.e(TAG, "onIndexTouchMove: ------>" + indexLetter);

        ArrayList<UserFriend> userFriends = this.mCacheFriends;
        userFriends.trimToSize();
        int position = 0;
        for (int i = userFriends.size() - 1; i > 0; i--) {
            UserFriend friend = userFriends.get(i);
            if (friend.equals(Character.toString(indexLetter))) {
                position = i;
                break;
            }

        }

        mRecyclerFriends.smoothScrollToPosition(position);

        mTvIndexShow.setText(Character.toString(indexLetter));
        mTvIndexShow.setVisibility(View.VISIBLE);
    }

    @Override
    public void onIndexTouchUp() {
        mTvIndexShow.setVisibility(View.GONE);
    }

    @SuppressWarnings("RestrictedApi")
    @Override
    protected void onStop() {
        super.onStop();
        mSearchView.clearFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "onBackPressed: --->");
    }
}
