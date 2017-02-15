package net.oschina.app.improve.user.adapter;

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
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.user.bean.UserFriend;
import net.oschina.app.improve.user.helper.ContactsCacheManager;
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


public class UserSearchFriendsAdapter extends RecyclerView.Adapter
        implements ContactsCacheManager.SelectedTrigger<ContactsCacheManager.Friend> {
    private static final int TYPE_TITLE = 0x1111;
    private static final int TYPE_NONE = 0x0000;
    private static final int TYPE_FOOTER = 0x0001;

    private final ContactsCacheManager.OnSelectedChangeListener listener;
    private String mSearchContent;

    public UserSearchFriendsAdapter(ContactsCacheManager.OnSelectedChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        ContactsCacheManager.Friend friend = getItem(position);
        if (friend.author == null) {
            if (TextUtils.isEmpty(friend.firstChar)) {
                return TYPE_FOOTER;
            } else {
                return TYPE_TITLE;
            }
        } else
            return TYPE_NONE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_TITLE:
                return new TitleViewHolder(inflater.inflate(R.layout.activity_item_select_friend_label, parent, false));
            case TYPE_FOOTER:
                return new SearchViewHolder(inflater.inflate(R.layout.activity_item_search_friend_bottom, parent, false), this);
            default:
                ViewHolder viewHolder = new ViewHolder(inflater.inflate(R.layout.activity_item_select_friend, parent, false));
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getTag() != null && v.getTag() instanceof ContactsCacheManager.Friend) {
                            onViewHolderClick((ContactsCacheManager.Friend) v.getTag());
                        }
                    }
                });
                return viewHolder;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ContactsCacheManager.Friend friend = getItem(position);
        if (holder instanceof TitleViewHolder) {
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            titleViewHolder.onBindView(friend);
        } else if (holder instanceof SearchViewHolder) {
            //SearchViewHolder searchViewHolder = (SearchViewHolder) holder;
        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.onBindView(friend);
        }
    }

    private ContactsCacheManager.Friend getItem(int pos) {
        if (pos < mSearchFriends.size()) {
            return mSearchFriends.get(pos);
        } else {
            pos = pos - (mSearchFriends.size() - 1);
            return mNetFriends.get(pos);
        }
    }

    @Override
    public int getItemCount() {
        return mSearchFriends.size() + mNetFriends.size();
    }

    private void onViewHolderClick(ContactsCacheManager.Friend friend) {
        if (listener != null)
            listener.tryTriggerSelected(friend, this);
    }

    private List<ContactsCacheManager.Friend> mCacheFriends = new ArrayList<>();

    public void initBaseItems(List<ContactsCacheManager.Friend> friends) {
        this.mCacheFriends.addAll(friends);
    }

    public void onSearchTextChanged(String searchContent) {
        this.mSearchContent = searchContent;
        query(searchContent);
    }

    private String getSearchContent() {
        return mSearchContent;
    }

    private final List<ContactsCacheManager.Friend> mSearchFriends = new ArrayList<>();
    private final List<ContactsCacheManager.Friend> mNetFriends = new ArrayList<>();


    /**
     * 查询文字
     */
    private void query(String queryText) {
        mSearchFriends.clear();
        mNetFriends.clear();

        if (TextUtils.isEmpty(queryText))
            return;

        ContactsCacheManager.Friend friend = new ContactsCacheManager.Friend(null);
        friend.firstChar = "R.string.local_search_label";
        mSearchFriends.add(friend);

        // R.string.search_net_label
        mNetFriends.add(new ContactsCacheManager.Friend(null));

        for (ContactsCacheManager.Friend mCacheFriend : mCacheFriends) {
            if (mCacheFriend.author == null)
                continue;
            Author author = mCacheFriend.author;
            String name = author.getName();
            if (TextUtils.isEmpty(name)) continue;

            boolean isZH = AssimilateUtils.checkIsZH(queryText);

            boolean isMatch;
            if (isZH) {
                isMatch = name.contains(queryText);
            } else {
                String pg = mCacheFriend.pinyin;
                String pinyin = AssimilateUtils.convertToPinyin(queryText, ContactsCacheManager.SPLIT_HEAD);
                isMatch = pg.startsWith(pinyin) || pg.contains(pinyin);
            }

            if (isMatch) {
                mSearchFriends.add(mCacheFriend);
            }
        }
        notifyDataSetChanged();
    }

    private int indexOfItem(ContactsCacheManager.Friend friend) {
        int pos = mSearchFriends.indexOf(friend);
        if (pos >= 0)
            return pos;
        else {
            pos = mNetFriends.indexOf(friend);
            if (pos >= 0) {
                return pos + mSearchFriends.size();
            }
        }
        return -1;
    }

    @Override
    public void trigger(ContactsCacheManager.Friend friend, boolean selected) {
        friend.isSelected = selected;
        int pos = indexOfItem(friend);
        if (pos >= 0) {
            notifyItemChanged(pos);
        }
    }

    @Override
    public void trigger(Author author, boolean selected) {
        if (author == null)
            return;
        for (ContactsCacheManager.Friend friend : mSearchFriends) {
            if (friend.author != null &&
                    friend.author.getId() == author.getId()) {
                trigger(friend, selected);
                return;
            }
        }

        for (ContactsCacheManager.Friend friend : mNetFriends) {
            if (friend.author != null &&
                    friend.author.getId() == author.getId()) {
                trigger(friend, selected);
                return;
            }
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_index_label)
        TextView mLabel;

        TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(ContactsCacheManager.Friend friend) {
            if (TextUtils.isEmpty(friend.firstChar))
                itemView.setVisibility(View.GONE);
            else {
                mLabel.setText(friend.firstChar);
                itemView.setVisibility(View.VISIBLE);
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_portrait)
        CircleImageView mCirclePortrait;
        @Bind(R.id.tv_name)
        TextView mtvName;
        @Bind(R.id.iv_select)
        ImageView mViewSelect;
        @Bind(R.id.line)
        View mLine;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(final ContactsCacheManager.Friend item) {
            if (item == null || item.author == null) {
                itemView.setVisibility(View.GONE);
                return;
            } else
                itemView.setVisibility(View.VISIBLE);
            final Author author = item.author;
            ImageLoader.loadImage(Glide.with(itemView.getContext()), mCirclePortrait, author.getPortrait(), R.mipmap.widget_default_face);
            mCirclePortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherUserHomeActivity.show(v.getContext(), author.getId());
                }
            });
            mtvName.setText(author.getName());

            if (item.isSelected) {
                mViewSelect.setVisibility(View.VISIBLE);
            } else {
                mViewSelect.setVisibility(View.GONE);
            }
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

}
