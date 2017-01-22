package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import java.util.LinkedList;
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

    private LayoutInflater mInflater;
    private List<UserFriend> mItems = new ArrayList<>();
    private String mSearchContent;

    private OnFriendSelector mOnFriendSelector;
    private int selectCount = 0;

    //最大可选择好友的数量
    private static final int MAX_SELECTED_SIZE = 10;
    private LinkedList<UserFriend> mSelectIcons = new LinkedList<>();

    private onKeyboardListener mOnKeyboardListener;

    public UserSearchFriendsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = this.mInflater;
        switch (viewType) {
            case UserSelectFriendsAdapter.INDEX_TYPE:
                return new IndexViewHolder(inflater.inflate(R.layout.activity_item_select_friend_label, parent, false));
            case UserSelectFriendsAdapter.SEARCH_TYPE:
                SearchViewHolder searchViewHolder = new SearchViewHolder(inflater.inflate(R.layout.activity_item_search_friend_bottom, parent, false), this);
                searchViewHolder.setOnKeyboardListener(mOnKeyboardListener);
                return searchViewHolder;
            case UserSelectFriendsAdapter.USER_TYPE:
            default:
                UserInfoViewHolder userInfoViewHolder = new UserInfoViewHolder(inflater.inflate(R.layout.activity_item_select_friend, parent, false));
                userInfoViewHolder.itemView.setTag(userInfoViewHolder);
                userInfoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mOnFriendSelector == null) return;

                        List<UserFriend> items = mItems;
                        UserInfoViewHolder holder = (UserInfoViewHolder) v.getTag();
                        int position = holder.getAdapterPosition();
                        UserFriend userFriend = items.get(position);
                        if (selectCount <= MAX_SELECTED_SIZE) {
                            if (userFriend.isSelected()) {
                                if (selectCount != 0) {
                                    items.get(position).setSelected(false);
                                    selectCount--;
                                    notifyItemChanged(position);
                                    mOnFriendSelector.unSelect(v, userFriend, position);
                                }
                            } else {
                                if (selectCount == MAX_SELECTED_SIZE) {
                                    mOnFriendSelector.selectFull(selectCount);
                                } else {
                                    items.get(position).setSelected(true);
                                    selectCount++;
                                    notifyItemChanged(position);
                                    mOnFriendSelector.select(v, userFriend, position);
                                }
                            }
                        }
                    }
                });
                if (viewType == UserSelectFriendsAdapter.USER_TYPE_UN_LINE)
                    userInfoViewHolder.mLine.setVisibility(View.GONE);
                return userInfoViewHolder;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserFriend item = mItems.get(position);
        switch (item.getShowViewType()) {
            case UserSelectFriendsAdapter.USER_TYPE:
                ((UserInfoViewHolder) holder).onBindView(item, position);
                break;
            case UserSelectFriendsAdapter.INDEX_TYPE:
                ((IndexViewHolder) holder).onBindView(item, position);
                break;
            case UserSelectFriendsAdapter.SEARCH_TYPE:
                ((SearchViewHolder) (holder)).onBindView(item, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        List<UserFriend> items = this.mItems;
        UserFriend item = items.get(position);
        switch (item.getShowViewType()) {
            case UserSelectFriendsAdapter.INDEX_TYPE:
                return UserSelectFriendsAdapter.INDEX_TYPE;
            case UserSelectFriendsAdapter.SEARCH_TYPE:
                return UserSelectFriendsAdapter.SEARCH_TYPE;
            default: {
                int maxPos = getItemCount() - 1;
                if ((position == maxPos)
                        || (position < maxPos && items.get(position + 1).getShowViewType() == UserSelectFriendsAdapter.INDEX_TYPE)) {
                    return UserSelectFriendsAdapter.USER_TYPE_UN_LINE;
                } else {
                    return UserSelectFriendsAdapter.USER_TYPE;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        List<UserFriend> item = this.mItems;
        return item.size();
    }

    public void addItems(List<UserFriend> items) {
        this.mItems.addAll(items);
        this.selectCount = items.size();
        notifyDataSetChanged();
    }

    public void addItem(int index, UserFriend userFriend) {
        this.mItems.add(index, userFriend);
        notifyDataSetChanged();
    }

    public List<UserFriend> getItems() {
        return mItems;
    }

    public LinkedList<UserFriend> getSelectIcons() {
        return mSelectIcons;
    }

    public void updateSelectStatus(UserFriend userFriend, boolean isSelected) {
        List<UserFriend> items = this.mItems;
        for (int i = 0, len = items.size(); i < len; i++) {
            UserFriend tempUserFriend = items.get(i);
            if (tempUserFriend.getId() == userFriend.getId()) {
                items.get(i).setSelected(isSelected);
                items.get(i).setSelectPosition(i);
                notifyItemChanged(i);
            }

        }
        if (!isSelected)
            if (selectCount > 0) {
                selectCount--;
            }
    }

    public void clear() {
        List<UserFriend> items = this.mItems;
        items.clear();
        notifyDataSetChanged();
    }

    public void setOnFriendSelector(OnFriendSelector OnFriendSelector) {
        mOnFriendSelector = OnFriendSelector;
    }

    public void setOnKeyboardListener(onKeyboardListener onKeyboardListener) {
        this.mOnKeyboardListener = onKeyboardListener;
    }

    public void setSearchContent(String searchContent) {
        this.mSearchContent = searchContent;
    }

    private String getSearchContent() {
        return mSearchContent;
    }

    public void setSelectIcons(LinkedList<UserFriend> selectIcons) {
        mSelectIcons = selectIcons;
        this.selectCount = selectIcons.size();
    }

    public void updateSelectCount(LinkedList<UserFriend> cacheIconFriends) {
        this.selectCount = cacheIconFriends.size();
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
        @Bind(R.id.iv_select)
        ImageView mViewSelect;
        @Bind(R.id.line)
        View mLine;

        UserInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(final UserFriend item, int position) {

            setImageFromNet(mCirclePortrait, item.getPortrait(), R.mipmap.widget_default_face);
            mCirclePortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherUserHomeActivity.show(v.getContext(), item.getId());
                }
            });
            mtvName.setText(item.getName());

            if (item.isSelected()) {
                mViewSelect.setVisibility(View.VISIBLE);
            } else {
                mViewSelect.setVisibility(View.INVISIBLE);
            }

            if (item.isGoneLine()) {
                mLine.setVisibility(View.GONE);
            }

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

        private UserSearchFriendsAdapter.onKeyboardListener mOnKeyboardListener;


        private SearchViewHolder(View itemView, UserSearchFriendsAdapter searchFriendsAdapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.mUserSearchFriendsAdapter = searchFriendsAdapter;
            mProgressBar.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        void onBindView(UserFriend item, int position) {
            mTvSearch.setText(mTvSearch.getResources().getString(R.string.search_net_label));
        }

        public void setOnKeyboardListener(onKeyboardListener mOnKeyBoardListener) {
            this.mOnKeyboardListener = mOnKeyBoardListener;
        }

        @Override
        public void onClick(final View v) {
            requestData(v);
        }

        private void requestData(final View v) {

            String searchContent = mUserSearchFriendsAdapter.getSearchContent();

            if (TextUtils.isEmpty(searchContent)) {
                mNextPageToken = null;
                mStatus = 0x00;
                AppContext.showToastShort(v.getResources().getString(R.string.search_null_hint));
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
                    mProgressBar.setVisibility(View.VISIBLE);
                    mTvSearch.setText(mTvSearch.getResources().getString(R.string.footer_type_loading));
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    mTvSearch.setText(mTvSearch.getResources().getString(R.string.search_error_hint));
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {

                    Type type = new TypeToken<ResultBean<PageBean<User>>>() {
                    }.getType();

                    ResultBean<PageBean<User>> resultBean = AppOperator.createGson().fromJson(responseString, type);

                    if (resultBean.isSuccess()) {

                        if (mOnKeyboardListener != null) {
                            mOnKeyboardListener.hideKeyboard();
                        }

                        PageBean<User> pageBean = resultBean.getResult();

                        List<User> users = pageBean.getItems();

                        UserSearchFriendsAdapter searchAdapter = mUserSearchFriendsAdapter;

                        //未变化搜索内容
                        if (mStatus == 0x00) {
                            //为网络请求的数据加入label
                            UserFriend netFriend = new UserFriend();

                            netFriend.setName("");
                            netFriend.setShowViewType(UserSelectFriendsAdapter.INDEX_TYPE);
                            netFriend.setShowLabel(v.getResources().getString(R.string.net_search_label));

                            searchAdapter.addItem(searchAdapter.getItemCount() - 1, netFriend);
                        }

                        mTvSearch.setText(mTvSearch.getResources().getString(R.string.search_load_more_hint));

                        for (User user : users) {

                            long userId = user.getId();
                            //如果是本地数据，那么就跳过
                            if (isLocalData(userId, searchAdapter)) {
                                continue;
                            }

                            UserFriend friend = new UserFriend();
                            friend.setId(userId);
                            friend.setPortrait(user.getPortrait());
                            friend.setName(user.getName());
                            //判断是否是已经被选中的数据
                            if (isContainsIconFriend(userId, searchAdapter)) {
                                friend.setSelected(true);
                            }
                            friend.setShowLabel(AssimilateUtils.returnPinyin(user.getName(), true));
                            friend.setShowViewType(UserSelectFriendsAdapter.USER_TYPE);

                            searchAdapter.addItem(searchAdapter.getItemCount() - 1, friend);
                        }

                        mNextPageToken = pageBean.getNextPageToken();

                        mStatus = 0x01;

                    } else {
                        mTvSearch.setText(mTvSearch.getResources().getString(R.string.state_not_more));
                    }

                }
            });
        }

        /**
         * @param id            friend id
         * @param searchAdapter searchAdapter
         * @return is localData?true:false
         */
        private boolean isLocalData(long id, UserSearchFriendsAdapter searchAdapter) {
            List<UserFriend> items = searchAdapter.getItems();
            for (UserFriend f : items) {
                if (f.getId() == id) {
                    return true;
                }
            }
            return false;
        }

        /**
         * verify isSelected status
         *
         * @param id            friend id
         * @param searchAdapter searchAdapter
         * @return isSelected status true/false
         */
        private boolean isContainsIconFriend(long id, UserSearchFriendsAdapter searchAdapter) {
            LinkedList<UserFriend> cacheIconFriends = searchAdapter.getSelectIcons();
            for (UserFriend iconFriend : cacheIconFriends) {
                if (iconFriend.getId() == id && iconFriend.isSelected())
                    return true;
            }
            return false;
        }

    }

    public interface onKeyboardListener {

        void hideKeyboard();
    }

}
