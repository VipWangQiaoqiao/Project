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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.app.ParentLinkedHolder;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
import net.oschina.app.improve.user.adapter.UserSearchFriendsAdapter;
import net.oschina.app.improve.user.adapter.UserSelectFriendsAdapter;
import net.oschina.app.improve.user.helper.ContactsCacheManager;
import net.oschina.app.improve.widget.RecentContactsView;
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
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 *          <p>
 *          用户联系人列表
 */
public class UserSelectFriendsActivity extends BaseBackActivity
        implements RecentContactsView.OnSelectedChangeListener, ContactsCacheManager.OnSelectedChangeListener,
        IndexView.OnIndexTouchListener, SearchView.OnQueryTextListener {

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

    //选中数据
    private final LinkedList<Author> mSelectedFriends = new LinkedList<>();
    private final ArrayList<ContactsCacheManager.Friend> mLocalFriends = new ArrayList<>();

    // 最近联系人
    private RecentContactsView mRecentView;
    //网络初始化的adapter
    private UserSelectFriendsAdapter mLocalAdapter;
    private UserSearchFriendsAdapter mSearchAdapter;

    private static ParentLinkedHolder<RichEditText> textParentLinkedHolder;

    public static void show(Object starter, RichEditText editText) {
        if (editText != null && (starter instanceof Activity || starter instanceof Fragment || starter instanceof android.app.Fragment)) {
            synchronized (UserSelectFriendsActivity.class) {
                ParentLinkedHolder<RichEditText> holder = new ParentLinkedHolder<>(editText);
                textParentLinkedHolder = holder.addParent(textParentLinkedHolder);
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

        // 初始化最近联系人
        mRecentView = new RecentContactsView(this);
        mRecentView.setListener(this);

        //初始化searchView的搜索icon
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSearchIcon.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        mSearchIcon.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) mLayoutEditFrame.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        mLayoutEditFrame.setLayoutParams(params);
        mSearchView.setOnQueryTextListener(this);

        mEmptyLayout.setNoDataContent(getText(R.string.no_friend_hint).toString());
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyLayout emptyLayout = mEmptyLayout;
                if (emptyLayout != null && emptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    initDataFromCacheOrNet();
                }
            }
        });

        mRecyclerFriends.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerFriends.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                TDevice.hideSoftKeyboard(mSearchView);
                return false;
            }
        });

        mLocalAdapter = new UserSelectFriendsAdapter(mRecentView, this);
        mSearchAdapter = new UserSearchFriendsAdapter(this, this, mSelectedFriends, mLocalFriends);

        mRecyclerFriends.setAdapter(mLocalAdapter);
        mIndex.setOnIndexTouchListener(this);
        mTvLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSelectData(true);
            }
        });

        // 初始化trigger
        recentSelectedTrigger = mRecentView;
        adapterSelectedTrigger = mLocalAdapter;

        //noinspection RestrictedApi
        mSearchView.clearFocus();
        TDevice.hideSoftKeyboard(mSearchView);
    }

    @Override
    protected void initData() {
        super.initData();
        initDataFromCacheOrNet();
    }

    /**
     * 初始化数据
     */
    private void initDataFromCacheOrNet() {
        ContactsCacheManager.sync(new Runnable() {
            @Override
            public void run() {
                List<Author> authors = ContactsCacheManager.getContacts();
                final List<ContactsCacheManager.Friend> friends = ContactsCacheManager.sortToFriendModel(authors);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayFirstView(friends);
                    }
                });
            }
        });
    }

    private void displayFirstView(List<ContactsCacheManager.Friend> friends) {
        // 先进行清理
        mLocalFriends.clear();
        mLocalAdapter.getItems().clear();
        // 没有拉取到用户，但是有最近联系人也显示界面
        if ((friends != null && friends.size() > 0) || mRecentView.hasData()) {
            // 有数据时
            if (friends != null) {
                mLocalFriends.addAll(friends);
            }
            mLocalFriends.trimToSize();
            mLocalAdapter.initItems(friends);
        }

        refreshLocalView();
    }

    private void refreshLocalView() {
        if (mLocalAdapter.getItemCount() == 0) {
            // 无数据时
            if (checkNetIsAvailable()) {
                showError(EmptyLayout.NODATA);
            } else {
                showError(EmptyLayout.NETWORK_ERROR);
            }
            mIndex.setVisibility(View.GONE);
        } else {
            mIndex.setVisibility(View.VISIBLE);
            showError(EmptyLayout.HIDE_LAYOUT);
        }
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

    @SuppressWarnings("RestrictedApi")
    @Override
    protected void onStop() {
        super.onStop();
        mSearchView.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        synchronized (UserSelectFriendsActivity.class) {
            if (textParentLinkedHolder != null) {
                textParentLinkedHolder = textParentLinkedHolder.putParent();
            }
        }
    }

    @Override
    public void onIndexTouchUp() {
        mTvIndexShow.setVisibility(View.GONE);
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Override
    public void onIndexTouchMove(char indexLetter) {
        String str = Character.toString(indexLetter);
        List<ContactsCacheManager.Friend> friends = mLocalAdapter.getItems();
        int position = -1;
        int size = friends.size();
        for (int i = 0; i < size; i++) {
            ContactsCacheManager.Friend friend = friends.get(i);
            if (friend.firstChar.startsWith(str)) {
                position = i;
                break;
            }
        }

        if (position >= 0) {
            RecyclerView.LayoutManager layoutManager = mRecyclerFriends.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
            } else {
                mRecyclerFriends.smoothScrollToPosition(position);
            }

            mTvIndexShow.setText(str);
            mTvIndexShow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    // 搜索文字改变时
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            transLabelX(false);
            refreshLocalView();

            if (mRecyclerFriends.getAdapter() != mLocalAdapter) {
                // 设置本地Trigger
                adapterSelectedTrigger = mLocalAdapter;
                mRecyclerFriends.setAdapter(mLocalAdapter);
            }
            // 关闭键盘
            TDevice.hideSoftKeyboard(mSearchView);
        } else {
            mIndex.setVisibility(View.GONE);

            mTvLabel.setText("@" + newText);
            transLabelX(true);

            mSearchAdapter.onSearchTextChanged(newText);

            if (mRecyclerFriends.getAdapter() != mSearchAdapter) {
                // 设置本地trigger为搜索适配器
                adapterSelectedTrigger = mSearchAdapter;
                mRecyclerFriends.setAdapter(mSearchAdapter);
            }

            // 有搜索时隐藏
            showError(EmptyLayout.HIDE_LAYOUT);
        }
        return true;
    }

    private float labelHideTransX = -1;
    private float labelBeforeTransX = -1;

    private void transLabelX(boolean show) {
        if (labelHideTransX == -1)
            labelHideTransX = TDevice.dipToPx(getResources(), 52);

        if (show) {
            if (labelBeforeTransX == 0)
                return;
            else
                labelBeforeTransX = 0;
        } else {
            if (labelBeforeTransX == labelHideTransX)
                return;
            else
                labelBeforeTransX = labelHideTransX;
        }

        if (mTvLabel.getTag() == null) {
            mTvLabel.animate()
                    .setInterpolator(new AnticipateOvershootInterpolator(2.5f))
                    .setDuration(260);
            mTvLabel.setTag(mTvLabel.animate());
        }

        mTvLabel.animate()
                .translationXBy(mTvLabel.getTranslationX())
                .translationX(labelBeforeTransX)
                .start();
    }

    /**
     * 结束时发送选中的文字
     * <p>
     * isLabel 表示是否从点击Label触发，如果是则发送Label的文字
     */
    private void sendSelectData(boolean isLabel) {
        List<String> friendNames = new ArrayList<>();
        if (isLabel) {
            String queryLabel = mTvLabel.getText().toString().replace("@", "");
            if (!TextUtils.isEmpty(queryLabel)) {
                friendNames.add(queryLabel);
            }
        }

        if (mSelectedFriends.size() > 0) {
            for (Author author : mSelectedFriends) {
                friendNames.add(author.getName());
            }

            // 回调前进行最近联系人存储
            ContactsCacheManager.addRecentCache(CollectionUtil.toArray(mSelectedFriends, Author.class));
        }

        // 回送@列表
        final String[] names = CollectionUtil.toArray(friendNames, String.class);
        synchronized (UserSelectFriendsActivity.class) {
            if (textParentLinkedHolder != null) {
                RichEditText editText = textParentLinkedHolder.item;
                if (editText != null)
                    editText.appendMention(names);
            }
        }

        Intent result = new Intent();
        result.putExtra("data", names);
        setResult(RESULT_OK, result);

        finish();
    }

    private boolean checkNetIsAvailable() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(getString(R.string.tip_network_error));
            return false;
        }
        return true;
    }

    private void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
        }
    }


    /**
     * 刷新选中的布局
     */
    private void updateSelectView() {
        mSelectContainer.removeAllViews();
        if (mSelectedFriends.size() == 0)
            mHorizontalScrollView.setVisibility(View.GONE);
        else {
            mHorizontalScrollView.setVisibility(View.VISIBLE);
            for (final Author author : mSelectedFriends) {
                ImageView ivIcon = (ImageView) LayoutInflater.from(this)
                        .inflate(R.layout.activity_main_select_friend_label_container_item, mSelectContainer, false);

                ivIcon.setTag(R.id.iv_show_icon, author);
                ivIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Author tag = (Author) v.getTag(R.id.iv_show_icon);
                        onSelectIconClick(tag);
                        mSelectContainer.removeView(v);
                    }
                });
                mSelectContainer.addView(ivIcon);
                Glide.with(this).load(author.getPortrait()).error(R.mipmap.widget_default_face).into(ivIcon);
            }
        }
    }

    private void onSelectIconClick(Author author) {
        mSelectedFriends.remove(author);
        // 通知适配器
        adapterSelectedTrigger.trigger(author, false);
        // 通知最近联系人
        recentSelectedTrigger.trigger(author, false);
    }

    private ContactsCacheManager.SelectedTrigger<RecentContactsView.Model> recentSelectedTrigger;
    private ContactsCacheManager.SelectedTrigger<ContactsCacheManager.Friend> adapterSelectedTrigger;

    /**
     * 尝试插入一个选中，如果不允许则不插入，并返回false
     */
    private boolean tryInsertSelected(Author author) {
        boolean allow = mSelectedFriends.size() < 10;
        if (allow) {
            mSelectedFriends.add(author);
            updateSelectView();
        } else
            AppContext.showToastShort(getString(R.string.check_count_hint));
        return allow;
    }

    /**
     * 移除选中
     */
    private void removeSelected(Author author) {
        int index = ContactsCacheManager.indexOfContacts(mSelectedFriends, author);
        if (index >= 0) {
            mSelectedFriends.remove(index);
            updateSelectView();
        }
    }

    /**
     * 最近联系人触发
     */
    @Override
    public void tryTriggerSelected(RecentContactsView.Model model, ContactsCacheManager.SelectedTrigger<RecentContactsView.Model> trigger) {
        if (ContactsCacheManager.checkInContacts(mSelectedFriends, model.author)) {
            removeSelected(model.author);
            trigger.trigger(model, false);
            // 通知适配器
            adapterSelectedTrigger.trigger(model.author, false);
        } else {
            if (tryInsertSelected(model.author)) {
                trigger.trigger(model, true);
                // 通知适配器
                adapterSelectedTrigger.trigger(model.author, true);
            }
        }
    }

    /**
     * 适配器触发
     */
    @Override
    public void tryTriggerSelected(ContactsCacheManager.Friend friend, ContactsCacheManager.SelectedTrigger<ContactsCacheManager.Friend> trigger) {
        if (ContactsCacheManager.checkInContacts(mSelectedFriends, friend.author)) {
            removeSelected(friend.author);
            trigger.trigger(friend, false);
            // 通知最近联系人
            recentSelectedTrigger.trigger(friend.author, false);
        } else {
            if (tryInsertSelected(friend.author)) {
                trigger.trigger(friend, true);
                // 通知最近联系人
                recentSelectedTrigger.trigger(friend.author, true);
            }
        }
    }
}
