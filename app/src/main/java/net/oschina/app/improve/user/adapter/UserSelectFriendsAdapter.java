package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.oschina.app.R;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.user.activities.OnSelectFriendListener;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.user.bean.UserFriend;
import net.oschina.app.improve.user.helper.ContactsCacheManager;
import net.oschina.app.util.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by fei
 * on 2016/12/23.
 * desc:
 */

public class UserSelectFriendsAdapter extends RecyclerView.Adapter implements ContactsCacheManager.SelectedTrigger<ContactsCacheManager.Friend> {
    private static final int INDEX_FIRST_TYPE = 0x11111111;
    public static final int INDEX_TYPE = 0x01;
    public static final int USER_TYPE = 0x02;
    public static final int SEARCH_TYPE = 0x03;
    public static final int USER_TYPE_UN_LINE = 0x04;

    private LayoutInflater mInflater;
    private List<UserFriend> mItems = new ArrayList<>();
    private int selectCount = 0;
    //最大可选择好友的数量
    private static final int MAX_SELECTED_SIZE = 10;


    ContactsCacheManager.OnSelectedChangeListener listener;
    private OnSelectFriendListener mOnFriendSelector;
    private View mFirstView;

    public UserSelectFriendsAdapter(Context context, View firstView, ContactsCacheManager.OnSelectedChangeListener listener) {
        mInflater = LayoutInflater.from(context);
        mFirstView = firstView;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == INDEX_FIRST_TYPE) {
            return new RecyclerView.ViewHolder(mFirstView) {
            };
        }

        LayoutInflater inflater = this.mInflater;
        if (viewType == INDEX_TYPE) {
            return new IndexViewHolder(inflater.inflate(R.layout.activity_item_select_friend_label, parent, false));
        } else {
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
                    onSelectClick(userFriend, position);
                }
            });

            if (viewType == USER_TYPE_UN_LINE)
                userInfoViewHolder.mLine.setVisibility(View.GONE);
            return userInfoViewHolder;

        }
    }

    private void onSelectClick(UserFriend userFriend, int position) {
        if (selectCount <= MAX_SELECTED_SIZE) {
            if (userFriend.isSelected()) {
                if (selectCount != 0) {
                    userFriend.setSelected(false);
                    userFriend.setSelectPosition(position);
                    selectCount--;
                    notifyItemChanged(position);
                    mOnFriendSelector.unSelect(userFriend);
                }
            } else {
                if (selectCount == MAX_SELECTED_SIZE) {
                    mOnFriendSelector.selectFull(selectCount);
                } else {
                    userFriend.setSelected(true);
                    userFriend.setSelectPosition(position);
                    selectCount++;
                    notifyItemChanged(position);
                    mOnFriendSelector.select(userFriend);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 第一位不做处理，预留为顶部最近联系人
        if (position == 0)
            return;

        UserFriend item = mItems.get(position);

        if (holder instanceof IndexViewHolder) {
            ((IndexViewHolder) holder).onBindView(item, position);
        } else {
            ((UserInfoViewHolder) holder).onBindView(item, position);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return INDEX_FIRST_TYPE;
        UserFriend item = this.mItems.get(position);
        switch (item.getShowViewType()) {
            case INDEX_TYPE:
                return INDEX_TYPE;
            default: {
                int maxPos = getItemCount() - 1;
                if ((position == maxPos)
                        || (position < maxPos && mItems.get(position + 1).getShowViewType() == INDEX_TYPE)) {
                    return USER_TYPE_UN_LINE;
                } else {
                    return USER_TYPE;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        List<UserFriend> item = this.mItems;
        return item.size();
    }

    public void setOnFriendSelector(OnSelectFriendListener onFriendSelector) {
        mOnFriendSelector = onFriendSelector;
    }

    public void initItems(List<UserFriend> items) {
        this.mItems.clear();

        // 添加一个空的UserFriend 用于最近联系人占位
        this.mItems.add(new UserFriend());
        if (items != null && items.size() > 0)
            this.mItems.addAll(items);
        notifyDataSetChanged();
    }

    public List<UserFriend> getItems() {
        return mItems;
    }

    public void updateSelectStatus(UserFriend userFriend, boolean isSelected) {
        if (!isSelected)
            for (int i = 0, len = mItems.size(); i < len; i++) {
                if (userFriend.getId() == mItems.get(i).getId()) {
                    mItems.get(i).setSelected(false);
                    mItems.get(i).setSelectPosition(i);
                    notifyItemChanged(i);
                }
            }

        if (!isSelected) {
            if (selectCount != 0) {
                selectCount--;
            }
        }
    }

    public void updateAllSelectStatus(boolean isSelected) {
        List<UserFriend> items = this.mItems;
        for (int i = 0, len = items.size(); i < len; i++) {
            if (items.get(i).isSelected()) {
                items.get(i).setSelected(isSelected);
                items.get(i).setSelectPosition(i);
                notifyItemChanged(i);
            }
        }
        selectCount = 0;
    }

    public void updateSelectCount(LinkedList<UserFriend> cacheIcons) {
        this.selectCount = cacheIcons.size();

        for (UserFriend cacheIcon : cacheIcons) {
            for (int i = 0; i < mItems.size(); i++) {
                if (mItems.get(i).getId() == cacheIcon.getId()) {
                    mItems.get(i).setSelected(cacheIcon.isSelected());
                    mItems.get(i).setSelectPosition(i);
                    notifyItemChanged(i);
                }
            }
        }
    }


    @Override
    public void trigger(ContactsCacheManager.Friend friend, boolean selected) {

    }

    @Override
    public void trigger(Author author, boolean selected) {
        if (mItems.size() == 0 || author == null)
            return;
        for (UserFriend mItem : mItems) {
            if (mItem.getId() == author.getId()) {

            }
        }
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
            ImageLoader.loadImage(Glide.with(itemView.getContext()),
                    mCirclePortrait, item.getPortrait(), R.mipmap.widget_default_face);
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

            if (item.isGoneLine())
                mLine.setVisibility(View.GONE);
        }
    }
}
