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
                return new UserInfoViewHolder(inflater.inflate(R.layout.activity_item_select_friend, parent, false));
            case SEARCH_TYPE:
                return new SearchViewHolder(inflater.inflate(R.layout.recycler_footer_view, parent, false), this);
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
        View mline;

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
                mline.setVisibility(View.GONE);
        }

        private void setImageFromNet(ImageView imageView, String imageUrl, int placeholder) {
            ImageLoader.loadImage(Glide.with(imageView.getContext()), imageView, imageUrl, placeholder);
        }

    }

    static class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.pb_footer)
        ProgressBar mProgressBar;
        @Bind(R.id.bt_search)
        TextView mBtSearch;
        private int position;
        private UserFriend mNetLabelFriend;
        private UserSearchFriendsAdapter mUserSearchFriendsAdapter;

        private String mNextPageToken;
        private String mSearchContent;

        SearchViewHolder(View itemView, UserSearchFriendsAdapter searchFriendsAdapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mUserSearchFriendsAdapter = searchFriendsAdapter;
            mProgressBar.setVisibility(View.GONE);
        }

        void onBindView(UserFriend item, int position) {
            mBtSearch.setText("在网络上搜索");
            mBtSearch.setOnClickListener(this);
            this.position = position;
            Log.e(TAG, "onBindView: ---->" + position);
        }

        @Override
        public void onClick(final View v) {

            Log.e(TAG, "onClick: ------>进行网络搜索 " + mNextPageToken);
            mSearchContent = mUserSearchFriendsAdapter.getSearchContent();

            if (TextUtils.isEmpty(mSearchContent)) {
                AppContext.showToastShort("搜索内容不能为空！！！");
                return;
            }

            if (!TDevice.hasInternet()) {
                AppContext.showToastShort(R.string.error_view_network_error_click_to_refresh);
                return;
            }

            OSChinaApi.search(News.TYPE_FIND_PERSON, mSearchContent, TextUtils.isEmpty(mNextPageToken)
                    ? null : mNextPageToken, new TextHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    Log.e(TAG, "onStart: ---->");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    Log.e(TAG, "onFinish: ----->");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    Type type = new TypeToken<ResultBean<PageBean<User>>>() {
                    }.getType();

                    ResultBean<PageBean<User>> resultBean = AppOperator.createGson().fromJson(responseString, type);


                    if (resultBean.isSuccess()) {
                        Log.e(TAG, "onSuccess: ---->");

                        PageBean<User> pageBean = resultBean.getResult();

                        List<User> users = pageBean.getItems();


                        if (mNetLabelFriend == null) {
                            //为网络请求的数据加入label
                            UserFriend netFriend = new UserFriend();
                            netFriend.setName("");
                            netFriend.setShowViewType(INDEX_TYPE);
                            netFriend.setShowLabel(v.getResources().getString(R.string.net_search_label));

                            mUserSearchFriendsAdapter.clearNetSearchData(position);
                            mUserSearchFriendsAdapter.addItem(position, netFriend);

                            mNetLabelFriend = netFriend;
                        }

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
                        Log.e(TAG, "onSuccess: ------>" + mNextPageToken + " " + pageBean.getTotalResults());
                    }

                }
            });

        }
    }
}
