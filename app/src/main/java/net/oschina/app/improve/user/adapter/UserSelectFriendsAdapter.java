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
import net.oschina.app.improve.user.OnFriendSelector;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.user.bean.UserFriend;
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

public class UserSelectFriendsAdapter extends RecyclerView.Adapter {

    public static final int INDEX_TYPE = 0x01;
    public static final int USER_TYPE = 0x02;
    public static final int SEARCH_TYPE = 0x03;
    public static final int USER_TYPE_UN_LINE = 0x04;

    private LayoutInflater mInflater;
    private List<UserFriend> mItems = new ArrayList<>();
    private int selectCount = 0;
    //最大可选择好友的数量
    private static final int MAX_SELECTED_SIZE = 10;

    private OnFriendSelector mOnFriendSelector;

    public UserSelectFriendsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                    if (selectCount <= MAX_SELECTED_SIZE) {
                        if (userFriend.isSelected()) {
                            if (selectCount != 0) {
                                items.get(position).setSelected(false);
                                items.get(position).setSelectPosition(position);
                                selectCount--;
                                notifyItemChanged(position);
                                mOnFriendSelector.unSelect(v, userFriend, position);
                            }
                        } else {
                            if (selectCount == MAX_SELECTED_SIZE) {
                                mOnFriendSelector.selectFull(selectCount);
                            } else {
                                items.get(position).setSelected(true);
                                items.get(position).setSelectPosition(position);
                                selectCount++;
                                notifyItemChanged(position);
                                mOnFriendSelector.select(v, userFriend, position);
                            }
                        }
                    }
                }
            });

            if (viewType == USER_TYPE_UN_LINE)
                userInfoViewHolder.mLine.setVisibility(View.GONE);
            return userInfoViewHolder;

        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserFriend item = mItems.get(position);

        if (holder instanceof IndexViewHolder) {
            ((IndexViewHolder) holder).onBindView(item, position);
        } else {
            ((UserInfoViewHolder) holder).onBindView(item, position);
        }

    }

    @Override
    public int getItemViewType(int position) {
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

    public void setOnFriendSelector(OnFriendSelector onFriendSelector) {
        mOnFriendSelector = onFriendSelector;
    }

    public void addItems(List<UserFriend> items) {
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }

    public List<UserFriend> getItems() {
        return mItems;
    }

    public void clear() {
        this.mItems.clear();
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

            if (item.isGoneLine())
                mLine.setVisibility(View.GONE);
        }

        private void setImageFromNet(ImageView imageView, String imageUrl, int placeholder) {
            ImageLoader.loadImage(Glide.with(imageView.getContext()), imageView, imageUrl, placeholder);
        }

    }
}
