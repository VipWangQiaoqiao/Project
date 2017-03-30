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
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.user.helper.ContactsCacheManager;
import net.oschina.app.improve.utils.parser.RichTextParser;
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
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class UserSearchFriendsAdapter extends RecyclerView.Adapter
        implements ContactsCacheManager.SelectedTrigger<ContactsCacheManager.Friend> {
    private static final int TYPE_TITLE = 0x1111;
    private static final int TYPE_NONE = 0x0000;
    private static final int TYPE_FOOTER = 0x0001;

    private final ContactsCacheManager.OnSelectedChangeListener listener;
    private String mSearchContent;
    private Context mContext;

    public UserSearchFriendsAdapter(Context context, ContactsCacheManager.OnSelectedChangeListener listener,
                                    List<Author> selectPointer, List<ContactsCacheManager.Friend> localFriendPointer) {
        this.mContext = context;
        this.listener = listener;
        this.mSelectFriendsPointer = selectPointer;
        this.mLocalFriendPointer = localFriendPointer;
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
                return new SearchViewHolder(inflater.inflate(R.layout.activity_item_search_friend_bottom, parent, false));
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

        } else {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.onBindView(friend);
        }
    }

    private ContactsCacheManager.Friend getItem(int pos) {
        if (pos < mSearchFriends.size()) {
            return mSearchFriends.get(pos);
        } else {
            pos = pos - (mSearchFriends.size());
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

    private final List<ContactsCacheManager.Friend> mLocalFriendPointer;
    private final List<Author> mSelectFriendsPointer;

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

        ContactsCacheManager.Friend friend = new ContactsCacheManager.Friend(null,
                mContext.getText(R.string.local_search_label).toString());
        mSearchFriends.add(friend);

        // R.string.search_net_label
        mNetFriends.add(new ContactsCacheManager.Friend(null, null));

        for (ContactsCacheManager.Friend mCacheFriend : mLocalFriendPointer) {
            if (mCacheFriend.author == null)
                continue;
            Author author = mCacheFriend.author;
            String name = author.getName();
            if (TextUtils.isEmpty(name)) continue;

            boolean isZH = RichTextParser.checkIsZH(queryText);

            boolean isMatch;
            if (isZH) {
                isMatch = name.contains(queryText);
            } else {
                String pg = mCacheFriend.pinyin;
                String pinyin = RichTextParser.convertToPinyin(queryText, ContactsCacheManager.SPLIT_HEAD);
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
        if (author == null || TextUtils.isEmpty(mSearchContent))
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

    private void addNetFriends(List<ContactsCacheManager.Friend> friends) {
        //R.string.net_search_label
        int lastPos = mNetFriends.size() - 1;
        ContactsCacheManager.Friend last = mNetFriends.get(lastPos);
        mNetFriends.remove(last);
        if (mNetFriends.size() == 0) {
            ContactsCacheManager.Friend first = new ContactsCacheManager.Friend(null,
                    mContext.getText(R.string.net_search_label).toString());
            mNetFriends.add(first);
        }
        mNetFriends.addAll(friends);
        mNetFriends.add(last);
        notifyDataSetChanged();
    }

    // 判断是否是本地的或者已被选中的数据
    private boolean isLocalOrSelectedData(User user) {
        for (ContactsCacheManager.Friend mCacheFriend : mLocalFriendPointer) {
            if (mCacheFriend == null || mCacheFriend.author == null)
                continue;
            if (mCacheFriend.author.getId() == user.getId()) {
                return true;
            }
        }
        return ContactsCacheManager.checkInContacts(mSelectFriendsPointer, user);
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
            // Set Tag
            itemView.setTag(item);

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

    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.pb_footer)
        ProgressBar mProgressBar;
        @Bind(R.id.tv_footer)
        TextView mTvSearch;
        private String mNextPageToken;
        private String mOldSearchText;

        private SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mProgressBar.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
            mTvSearch.setText(mTvSearch.getResources().getString(R.string.search_net_label));
        }

        @Override
        public void onClick(final View v) {
            requestData(v);
        }

        private void requestData(final View v) {
            String searchContent = getSearchContent();
            if (TextUtils.isEmpty(searchContent)) {
                mNextPageToken = null;
                AppContext.showToastShort(v.getResources().getString(R.string.search_null_hint));
                return;
            }

            if (!TDevice.hasInternet()) {
                AppContext.showToastShort(R.string.error_view_network_error_click_to_refresh);
                return;
            }

            // 关键词改变时清理Token
            if (!searchContent.equals(mOldSearchText)) {
                mNextPageToken = null;
                mOldSearchText = searchContent;
            }

            OSChinaApi.search(News.TYPE_FIND_PERSON, searchContent,
                    mNextPageToken == null ? null : mNextPageToken,
                    new TextHttpResponseHandler() {
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
                                PageBean<User> pageBean = resultBean.getResult();
                                mNextPageToken = pageBean.getNextPageToken();
                                mTvSearch.setText(mTvSearch.getResources().getString(R.string.search_load_more_hint));

                                List<User> users = pageBean.getItems();
                                List<Author> authors = new ArrayList<>();

                                for (User user : users) {
                                    if (user == null || user.getId() <= 0
                                            || isLocalOrSelectedData(user))
                                        continue;
                                    authors.add(user);
                                }

                                addNetFriends(ContactsCacheManager.sortToFriendModel(authors));
                            } else {
                                mTvSearch.setText(mTvSearch.getResources().getString(R.string.state_not_more));
                            }
                        }
                    });
        }
    }
}
