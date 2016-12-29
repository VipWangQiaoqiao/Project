package net.oschina.app.improve.user.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import net.oschina.app.improve.user.OnFriendSelector;
import net.oschina.app.improve.user.adapter.UserSearchFriendsAdapter;
import net.oschina.app.improve.user.adapter.UserSelectFriendsAdapter;
import net.oschina.app.improve.user.bean.UserFansOrFollows;
import net.oschina.app.improve.user.bean.UserFriend;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.app.widget.IndexView;
import net.oschina.common.utils.CollectionUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static net.oschina.app.api.remote.OSChinaApi.TYPE_USER_FOLOWS;

/**
 * Created by fei
 * on 2016/12/22.
 * desc:
 */

public class UserSelectFriendsActivity extends BaseBackActivity implements IndexView.OnIndexTouchListener, SearchView.OnQueryTextListener {

    private static final String TAG = "UserSelectFriendsActivity";
    @Bind(R.id.tv_back)
    TextView mTvBack;

    @Bind(R.id.bt_select_submit)
    Button mBtSelectSubmit;

    @Bind(R.id.searcher_friends)
    SearchView mSearchView;

    @Bind(R.id.search_mag_icon)
    ImageView mSearchIcon;

    @Bind(R.id.search_edit_frame)
    LinearLayout mLayoutEditFrame;

    @Bind(R.id.recycler_friends_icon)
    HorizontalScrollView mHorizontalScrollView;

    @Bind(R.id.select_container)
    LinearLayout mSelectContainer;

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

    /**
     * 最大可选择好友的数量
     */
    private static final int MAX_SELECTED_SIZE = 10;

    private PageBean<UserFansOrFollows> mPageBean;

    //网络初始化的adapter
    private UserSelectFriendsAdapter mLocalAdapter;

    //网络初始化的朋友数据
    private ArrayList<UserFriend> mCacheFriends;

    //选中icon缓存朋友数据
    private HashMap<String, UserFriend> mCacheIconFriends = new HashMap<>();

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

        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (mCacheIconFriends.size() <= 0) {
            mBtSelectSubmit.setEnabled(false);
        } else {
            mBtSelectSubmit.setEnabled(true);
        }
        mBtSelectSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSelectData(false);
            }
        });

        //初始化searchview的搜索icon
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSearchIcon.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mSearchIcon.setLayoutParams(params);

        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mLayoutEditFrame.getLayoutParams();
        params1.setMargins(0, 0, 0, 0);
        mLayoutEditFrame.setLayoutParams(params1);

        mRecyclerFriends.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerFriends.setAdapter(mLocalAdapter = new UserSelectFriendsAdapter(this));
        mLocalAdapter.setOnFriendSelector(new OnFriendSelector() {
            @Override
            public void select(View view, UserFriend userFriend, int position) {

                updateSelectIcon(userFriend);

            }
        });

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

        mTvLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSelectData(true);
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(this);

        mSearchView.post(new Runnable() {
            @SuppressWarnings("RestrictedApi")
            @Override
            public void run() {
                mSearchView.clearFocus();

            }
        });
    }


    /**
     * send select data
     */
    private void sendSelectData(boolean isLabel) {

        String queryLabel = (String) mTvLabel.getText();

        List<String> friendNames = new ArrayList<>();

        if (isLabel) {
            if (!TextUtils.isEmpty(queryLabel)) {
                queryLabel = queryLabel.substring(1);
                friendNames.add(queryLabel);
            }
        }

        for (Map.Entry<String, UserFriend> friendEntry : mCacheIconFriends.entrySet()) {
            UserFriend friend = friendEntry.getValue();
            friendNames.add(friend.getName());
        }

        String[] names = CollectionUtil.toArray(friendNames, String.class);

        Intent result = new Intent();
        result.putExtra("names", names);

        setResult(RESULT_OK, result);

        finish();
    }

    @Override
    protected void initData() {
        super.initData();

        if (mSearchAdapter == null) {
            mSearchAdapter = new UserSearchFriendsAdapter(UserSelectFriendsActivity.this);
        }

        requestData();
    }

    private void updateSelectIcon(UserFriend userFriend) {

        HashMap<String, UserFriend> cacheIcons = this.mCacheIconFriends;

        final String name = userFriend.getName();
        if (cacheIcons.containsKey(name)) {
            cacheIcons.remove(name);
        } else {
            if (cacheIcons.size() > MAX_SELECTED_SIZE) {
                AppContext.showToastShort(getString(R.string.check_count_hint));
            } else {
                cacheIcons.put(name, userFriend);
            }
        }

        if (cacheIcons.size() > 0) {
            mBtSelectSubmit.setEnabled(true);
            //mHorizontalScrollView.setVisibility(View.VISIBLE);
        } else {
            mBtSelectSubmit.setEnabled(false);
            //mHorizontalScrollView.setVisibility(View.GONE);
        }

        mSelectContainer.removeAllViews();
        Set<Map.Entry<String, UserFriend>> entries = cacheIcons.entrySet();
        for (Map.Entry<String, UserFriend> entry : entries) {

            UserFriend friend = entry.getValue();

            ImageView ivIcon = (ImageView) LayoutInflater.from(this)
                    .inflate(R.layout.activity_main_select_friend_label_container_item, mSelectContainer, false);
            ivIcon.setTag(R.id.iv_show_icon, friend);
            ivIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserFriend friend = (UserFriend) v.getTag(R.id.iv_show_icon);
                    updateSelectIcon(friend);
                }
            });
            mSelectContainer.addView(ivIcon);
            Glide.with(this).load(friend.getPortrait())
                    .error(R.mipmap.widget_dface)
                    .into(ivIcon);
        }

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

        //自然排序
        Collections.sort(mCacheFriends);

        mLocalAdapter.addItems(mCacheFriends);
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public void onIndexTouchMove(char indexLetter) {

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
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onQueryTextChange(String newText) {


        if (TextUtils.isEmpty(newText)) {
            mTvLabel.setText(null);
            mTvLabel.setVisibility(View.GONE);
            mIndex.setVisibility(View.VISIBLE);

            mRecyclerFriends.setAdapter(mLocalAdapter);


            mBtSelectSubmit.setEnabled(false);
            mSearchAdapter.notifyDataSetChanged();
            mSearchAdapter.setSearchContent(newText);
            return false;
        } else {

            mBtSelectSubmit.setEnabled(true);

            if (mIndex.getVisibility() == View.VISIBLE) {
                mIndex.setVisibility(View.GONE);
            }
            mTvLabel.setText("@" + newText);
            mTvLabel.setVisibility(View.VISIBLE);

            if (mRecyclerFriends.getAdapter() instanceof UserSelectFriendsAdapter) {
                mRecyclerFriends.setAdapter(mSearchAdapter);
            }
            queryUpdateView(newText);
        }

        mSearchAdapter.setOnOnFriendSelecter(new OnFriendSelector() {
            @Override
            public void select(View view, UserFriend userFriend, int position) {

                updateSelectIcon(userFriend);

            }
        });
        mSearchAdapter.setSearchContent(newText);
        return true;

    }

}
