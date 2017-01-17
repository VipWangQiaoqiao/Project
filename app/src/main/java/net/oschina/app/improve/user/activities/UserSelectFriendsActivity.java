package net.oschina.app.improve.user.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.app.ParentLinkedHolder;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
import net.oschina.app.improve.user.OnFriendSelector;
import net.oschina.app.improve.user.adapter.UserSearchFriendsAdapter;
import net.oschina.app.improve.user.adapter.UserSelectFriendsAdapter;
import net.oschina.app.improve.user.bean.UserFriend;
import net.oschina.app.improve.user.helper.SyncFriendHelper;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.app.widget.IndexView;
import net.oschina.common.utils.CollectionUtil;
import net.oschina.common.widget.RichEditText;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by fei
 * on 2016/12/22.
 * desc:用户联系人列表
 */

public class UserSelectFriendsActivity extends BaseBackActivity implements IndexView.OnIndexTouchListener,
        SearchView.OnQueryTextListener {

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

    public static final String CACHE_NAME = "userFriends";

    //网络初始化的adapter
    private UserSelectFriendsAdapter mLocalAdapter = null;

    //网络初始化的朋友数据
    private ArrayList<UserFriend> mCacheFriends;

    //选中icon缓存朋友数据
    private LinkedList<UserFriend> mCacheIconFriends = new LinkedList<>();

    private UserSearchFriendsAdapter mSearchAdapter;

    private static ParentLinkedHolder<RichEditText> textParentLinkedHolder;

    public static void show(Object starter, RichEditText editText) {
        if (editText != null && (starter instanceof Activity || starter instanceof Fragment || starter instanceof android.app.Fragment)) {
            synchronized (UserSelectFriendsActivity.class) {
                ParentLinkedHolder<RichEditText> holder = new ParentLinkedHolder<>(editText);
                if (textParentLinkedHolder == null)
                    textParentLinkedHolder = holder;
                else
                    textParentLinkedHolder.addParent(holder);
            }

            if (starter instanceof Activity) {
                Activity context = (Activity) starter;
                Intent intent = new Intent(context, UserSelectFriendsActivity.class);
                context.startActivityForResult(intent, TweetPublishFragment.REQUEST_CODE_SELECT_FRIENDS);
            } else if (starter instanceof Fragment) {
                Fragment fragment = (Fragment) starter;
                Context context = fragment.getContext();
                if (context == null)
                    return;
                Intent intent = new Intent(context, UserSelectFriendsActivity.class);
                fragment.startActivityForResult(intent, TweetPublishFragment.REQUEST_CODE_SELECT_FRIENDS);
            } else {
                android.app.Fragment fragment = (android.app.Fragment) starter;
                Context context = fragment.getActivity();
                if (context == null)
                    return;
                Intent intent = new Intent(context, UserSelectFriendsActivity.class);
                fragment.startActivityForResult(intent, TweetPublishFragment.REQUEST_CODE_SELECT_FRIENDS);
            }
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_user_select_friends;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        //初始化searchView的搜索icon
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSearchIcon.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mSearchIcon.setLayoutParams(params);

        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mLayoutEditFrame.getLayoutParams();
        params1.setMargins(0, 0, 0, 0);
        mLayoutEditFrame.setLayoutParams(params1);

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

        mEmptyLayout.setLoadingFriend(true);
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

        mRecyclerFriends.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerFriends.setAdapter(mLocalAdapter = new UserSelectFriendsAdapter(this));

        if (mSearchAdapter == null) {
            mSearchAdapter = new UserSearchFriendsAdapter(UserSelectFriendsActivity.this);
        }

        mLocalAdapter.setOnFriendSelector(new OnFriendSelector() {
            @Override
            public void select(View view, UserFriend userFriend, int position) {
                updateSelectIcon(userFriend);
            }

            @Override
            public void selectFull(int selectCount) {
                AppContext.showToastShort(getString(R.string.check_count_hint));
            }
        });

        mIndex.setOnIndexTouchListener(this);

        mTvLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSelectData(true);
            }
        });


    }

    @Override
    protected void initData() {
        super.initData();

        mEmptyLayout.post(new Runnable() {
            @Override
            public void run() {
                requestData();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_topic, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_submit) {
            sendSelectData(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * request data
     */
    private void requestData() {

        final ArrayList<UserFriend> friends = SyncFriendHelper.getFriends();
        if (friends != null && friends.size() > 0) {
            updateView(friends);
        } else {
            //检查网络
            if (!checkNetIsAvailable()) {
                showError(EmptyLayout.NETWORK_ERROR);
            } else {
                SyncFriendHelper.load(new Runnable() {
                    @Override
                    public void run() {
                        mEmptyLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<UserFriend> friends = SyncFriendHelper.getFriends();
                                updateView(friends);
                            }
                        });

                    }
                });
            }
        }
    }

    /**
     * refresh the friends ui
     *
     * @param friends friends
     */
    private void updateView(ArrayList<UserFriend> friends) {

        if (friends != null && friends.size() > 0) {
            mLocalAdapter.clear();
            mLocalAdapter.addItems(friends);
            hideLoading();
        } else {
            showError(EmptyLayout.NODATA);
        }

        this.mCacheFriends = friends;
    }

    /**
     * refresh the select friends ui
     *
     * @param userFriend friend
     */
    private void updateSelectIcon(UserFriend userFriend) {

        LinkedList<UserFriend> cacheIcons = this.mCacheIconFriends;

        int index = containsUserFriend(userFriend);

        if (index != -1) {
            cacheIcons.remove(index);
        } else {
            cacheIcons.add(userFriend);
        }

        mSelectContainer.removeAllViews();

        for (UserFriend friend : cacheIcons) {

            ImageView ivIcon = (ImageView) LayoutInflater.from(this)
                    .inflate(R.layout.activity_main_select_friend_label_container_item, mSelectContainer, false);

            ivIcon.setTag(R.id.iv_show_icon, friend);
            ivIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserFriend friend = (UserFriend) v.getTag(R.id.iv_show_icon);

                    int selectPosition = friend.getSelectPosition();

                    RecyclerView.Adapter recyclerFriendsAdapter = mRecyclerFriends.getAdapter();

                    if (recyclerFriendsAdapter instanceof UserSelectFriendsAdapter) {
                        ((UserSelectFriendsAdapter) recyclerFriendsAdapter).updateSelectStatus(selectPosition, false);
                    } else {
                        ((UserSearchFriendsAdapter) recyclerFriendsAdapter).updateSelectStatus(selectPosition, false);
                    }

                    mRecyclerFriends.smoothScrollToPosition(selectPosition);

                    //更新icons
                    updateSelectIcon(friend);
                }
            });
            mSelectContainer.addView(ivIcon);
            Glide.with(this).load(friend.getPortrait())
                    .error(R.mipmap.widget_default_face)
                    .into(ivIcon);
        }

    }

    /**
     * refresh the query ui
     *
     * @param queryText query text
     */
    private void queryUpdateView(String queryText) {

        String pinyinQueryText = AssimilateUtils.returnPinyin(queryText, false);

        //初始化缓存本地搜索好友列表
        ArrayList<UserFriend> searchFriends = new ArrayList<>();

        UserFriend LocalUserFriend = new UserFriend();

        LocalUserFriend.setName(getString(R.string.local_search_label));
        LocalUserFriend.setShowLabel(getString(R.string.local_search_label));
        LocalUserFriend.setShowViewType(UserSelectFriendsAdapter.INDEX_TYPE);
        searchFriends.add(LocalUserFriend);

        UserFriend NetUserFriend = new UserFriend();

        NetUserFriend.setName(getString(R.string.net_search_label));
        NetUserFriend.setShowLabel(getString(R.string.search_net_label));
        NetUserFriend.setShowViewType(UserSelectFriendsAdapter.SEARCH_TYPE);
        searchFriends.add(NetUserFriend);


        //缓存的本地好友列表
        ArrayList<UserFriend> cacheFriends = this.mCacheFriends;

        for (UserFriend friend : cacheFriends) {
            String name = friend.getName();
            if (TextUtils.isEmpty(name)) continue;

            //搜索列表当中没有该条数据，进行添加
            if (AssimilateUtils.returnPinyin(name, false).contains(pinyinQueryText)) {

                friend.setShowLabel(name);
                friend.setShowViewType(UserSelectFriendsAdapter.USER_TYPE);
                searchFriends.add(1, friend);
            }

        }
        mSearchAdapter.clear();
        mSearchAdapter.addItems(searchFriends);
        mSearchAdapter.setSelectIcons(mCacheIconFriends);
    }

    /**
     * send select data to  publish tweet
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

        for (UserFriend friend : mCacheIconFriends) {
            friendNames.add(friend.getName());
        }

        String[] names = CollectionUtil.toArray(friendNames, String.class);

        synchronized (UserSelectFriendsActivity.class) {
            if (textParentLinkedHolder != null) {
                RichEditText editText = textParentLinkedHolder.item;
                textParentLinkedHolder = textParentLinkedHolder.putParent();
                if (editText != null)
                    editText.appendMention(names);
            }
        }

        Intent result = new Intent();
        result.putExtra("data", names);
        setResult(RESULT_OK, result);

        finish();
    }

    /**
     * contains userFriend
     *
     * @param userFriend userFriend
     * @return index
     */
    private int containsUserFriend(UserFriend userFriend) {

        int index = -1;

        LinkedList<UserFriend> cacheIcons = this.mCacheIconFriends;
        for (int i = 0; i < cacheIcons.size(); i++) {
            UserFriend friend = cacheIcons.get(i);
            if (friend.getId() == userFriend.getId()) {
                index = i;
            }
        }
        return index;
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public void onIndexTouchMove(char indexLetter) {

        ArrayList<UserFriend> userFriends = this.mCacheFriends;
        int position = 0;
        for (int i = userFriends.size() - 1; i > 0; i--) {
            UserFriend friend = userFriends.get(i);
            if (friend.getShowLabel().startsWith(Character.toString(indexLetter))) {
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

            mSearchAdapter.notifyDataSetChanged();
            mSearchAdapter.setSearchContent(newText);
            return false;
        } else {

            if (mIndex.getVisibility() == View.VISIBLE) {
                mIndex.setVisibility(View.GONE);
            }
            mTvLabel.setText("@" + newText);
            mTvLabel.setVisibility(View.VISIBLE);

            mSearchAdapter.clear();
            mSearchAdapter.setSearchContent(newText);

            if (mRecyclerFriends.getAdapter() instanceof UserSelectFriendsAdapter) {
                mRecyclerFriends.setAdapter(mSearchAdapter);
            }

            queryUpdateView(newText);
        }

        mSearchAdapter.setOnFriendSelector(new OnFriendSelector() {
            @Override
            public void select(View view, UserFriend userFriend, int position) {
                updateSelectIcon(userFriend);
            }

            @Override
            public void selectFull(int selectCount) {
                AppContext.showToastShort(getString(R.string.check_count_hint));
            }
        });
        return true;

    }

    /**
     * verify network status
     *
     * @return true/false
     */
    private boolean checkNetIsAvailable() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(getString(R.string.tip_network_error));
            showError(EmptyLayout.NETWORK_ERROR);
            return false;
        }
        return true;
    }

    /**
     * hide empty view's loading
     */
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

    /**
     * show empty view's error type
     *
     * @param type type
     */
    private void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
        }
    }
}
