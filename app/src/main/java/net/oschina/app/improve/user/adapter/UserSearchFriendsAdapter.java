package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.OnFriendSelector;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.user.bean.UserFriend;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.util.ImageLoader;
import net.oschina.app.util.TDevice;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by fei
 * on 2016/12/27.
 * desc:
 */

public class UserSearchFriendsAdapter extends RecyclerView.Adapter {

    public static final String TAG = "UserSelectFriendsAdapter";

    public static final int INDEX_TYPE = 0x01;
    public static final int USER_TYPE = 0x02;
    public static final int SEARCH_TYPE = 0x03;

    private LayoutInflater mInflater;
    private List<UserFriend> mItems = new ArrayList<>();
    private String mSearchContent;

    private OnFriendSelector mOnFriendSelector;

    public UserSearchFriendsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = this.mInflater;
        switch (viewType) {
            case INDEX_TYPE:
                return new IndexViewHolder(inflater.inflate(R.layout.activity_item_select_friend_label, parent, false));
            case USER_TYPE:
                UserInfoViewHolder userInfoViewHolder = new UserInfoViewHolder(inflater.inflate(R.layout.activity_item_select_friend, parent, false));

                userInfoViewHolder.itemView.setTag(userInfoViewHolder);
                userInfoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnFriendSelector == null) return;
                        UserInfoViewHolder holder = (UserInfoViewHolder) v.getTag();
                        mOnFriendSelector.select(v, mItems.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                    }
                });

                return userInfoViewHolder;
            case SEARCH_TYPE:
                return new SearchViewHolder(inflater.inflate(R.layout.activity_item_search_friend_bottom, parent, false), this);
            default:
                return null;
        }

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserFriend item = mItems.get(position);

        switch (item.getShowViewType()) {
            case USER_TYPE:
                ((UserInfoViewHolder) holder).onBindView(item, position);
                break;
            case INDEX_TYPE:
                ((IndexViewHolder) holder).onBindView(item, position);
                break;
            case SEARCH_TYPE:
                ((SearchViewHolder) (holder)).onBindView(item, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        List<UserFriend> item = this.mItems;
        return item.get(position).getShowViewType();
    }

    @Override
    public int getItemCount() {
        List<UserFriend> item = this.mItems;
        return item.size();
    }

    public void addItems(List<UserFriend> items) {
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        List<UserFriend> items = this.mItems;
        items.clear();
    }

    private void clearNetSearchData(int position) {
        List<UserFriend> items = this.mItems;
        items.subList(0, position);
    }

    public void setOnOnFriendSelecter(OnFriendSelector OnFriendSelector) {
        mOnFriendSelector = OnFriendSelector;
    }

    public void setSearchContent(String searchContent) {
        this.mSearchContent = searchContent;
    }

    public String getSearchContent() {
        return mSearchContent;
    }

    public void addItem(int index, UserFriend userFriend) {
        this.mItems.add(index, userFriend);
        notifyDataSetChanged();
    }


    static class IndexViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_index_label)
        TextView mTvIndexLabel;

        IndexViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(UserFriend item, int position) {
            mTvIndexLabel.setText(item.getShowLabel());
        }
    }

    static class UserInfoViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_portrait)
        CircleImageView mCirclePortrait;
        @Bind(R.id.tv_name)
        TextView mtvName;
        @Bind(R.id.line)
        View mLine;

        UserInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(final UserFriend item, int position) {

            setImageFromNet(mCirclePortrait, item.getPortrait(), R.mipmap.widget_dface);
            mCirclePortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherUserHomeActivity.show(v.getContext(), item.getId());
                }
            });
            mtvName.setText(item.getName());

            if (item.isGoneLine())
                mLine.setVisibility(View.GONE);
        }

        private void setImageFromNet(ImageView imageView, String imageUrl, int placeholder) {
            ImageLoader.loadImage(Glide.with(imageView.getContext()), imageView, imageUrl, placeholder);
        }
    }


    static class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.pb_footer)
        ProgressBar mProgressBar;
        @Bind(R.id.tv_footer)
        TextView mTvSearch;
        private UserSearchFriendsAdapter mUserSearchFriendsAdapter;

        private String mNextPageToken;
        private String mSearchContent;

        private int mStatus = 0x00;

        SearchViewHolder(View itemView, UserSearchFriendsAdapter searchFriendsAdapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mUserSearchFriendsAdapter = searchFriendsAdapter;
            mProgressBar.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        void onBindView(UserFriend item, int position) {
            mTvSearch.setText("在网络上搜索");
            Log.e(TAG, "onBindView: ---->" + position);
            if (mStatus == 0x01) {
                requestData(mTvSearch);
            }
        }

        @Override
        public void onClick(final View v) {

            Log.e(TAG, "onClick: ------>进行网络搜索 " + mNextPageToken);

            requestData(v);
        }

        private void requestData(final View v) {

            String searchContent = mUserSearchFriendsAdapter.getSearchContent();

            Log.e(TAG, "requestData: ------> " + searchContent);

            if (TextUtils.isEmpty(searchContent)) {
                mNextPageToken = null;
                mStatus = 0x00;
                AppContext.showToastShort("搜索内容不能为空！！！");
                return;
            } else {
                if (!searchContent.equals(mSearchContent)) {
                    mNextPageToken = null;
                    mStatus = 0x00;
                }
            }

            mSearchContent = searchContent;

            if (!TDevice.hasInternet()) {
                AppContext.showToastShort(R.string.error_view_network_error_click_to_refresh);
                return;
            }

            OSChinaApi.search(News.TYPE_FIND_PERSON, searchContent, TextUtils.isEmpty(mNextPageToken)
                    ? null : mNextPageToken, new TextHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    Log.e(TAG, "onStart: ---->");
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTvSearch.setText("数据正在加载中...");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    Log.e(TAG, "onFinish: ----->");
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    mTvSearch.setText("数据加载失败,请重试...");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    Type type = new TypeToken<ResultBean<PageBean<User>>>() {
                    }.getType();

                    ResultBean<PageBean<User>> resultBean = AppOperator.createGson().fromJson(responseString, type);

                    if (resultBean.isSuccess()) {

                        Log.e(TAG, "onSuccess: ---->");

                        PageBean<User> pageBean = resultBean.getResult();

                        List<User> users = pageBean.getItems();

                        //为网络请求的数据加入label
                        UserFriend netFriend = new UserFriend();
                        netFriend.setName("");
                        netFriend.setShowViewType(INDEX_TYPE);
                        netFriend.setShowLabel(v.getResources().getString(R.string.net_search_label));

                        mUserSearchFriendsAdapter.addItem(mUserSearchFriendsAdapter.getItemCount() - 1, netFriend);

                        mTvSearch.setText("点击加载更多数据...");

                        for (User user : users) {

                            UserFriend friend = new UserFriend();

                            friend.setId(user.getId());
                            friend.setPortrait(user.getPortrait());
                            friend.setName(user.getName());
                            friend.setShowLabel(AssimilateUtils.returnPinyin(user.getName(), true));
                            friend.setShowViewType(UserSearchFriendsAdapter.USER_TYPE);

                            mUserSearchFriendsAdapter.addItem(mUserSearchFriendsAdapter.getItemCount() - 1, friend);
                        }

                        mNextPageToken = pageBean.getNextPageToken();

                        int totalResults = pageBean.getTotalResults();

                        mStatus = 0x01;

                        Log.e(TAG, "onSuccess: ------>" + mNextPageToken + " " + totalResults);
                    } else {
                        mTvSearch.setText("没有更多数据...");
                    }

                }
            });
        }
    }
}
