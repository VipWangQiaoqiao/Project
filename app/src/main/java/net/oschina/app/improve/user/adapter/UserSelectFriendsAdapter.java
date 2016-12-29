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

    public static final String TAG = "UserSelectFriendsAdapter";

    public static final int INDEX_TYPE = 0x01;
    public static final int USER_TYPE = 0x02;
    private LayoutInflater mInflater;
    private List<UserFriend> mItems = new ArrayList<>();

    private OnFriendSelector mOnFriendSelector;

    public UserSelectFriendsAdapter(Context context) {
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
            default:
                return null;
        }

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserFriend item = mItems.get(position);

        //        UserFriend nextItem = mItems.get(position < mItems.size() - 1 ? (position + 1) : mItems.size() - 1);
        //
        //        if (USER_TYPE == item.getShowViewType() && INDEX_TYPE == nextItem.getShowViewType()) {
        //            mItems.get(position).setGoneLine(true);
        //        }

        if (holder instanceof IndexViewHolder) {
            ((IndexViewHolder) holder).onBindView(item, position);
        } else {
            ((UserInfoViewHolder) holder).onBindView(item, position);
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

    public void setOnFriendSelector(OnFriendSelector onFriendSelector) {
        mOnFriendSelector = onFriendSelector;
    }

    public void addItems(List<UserFriend> items) {
        this.mItems.addAll(items);
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

            //Log.e(TAG, "onBindView: ---->" + position);

            if (item.isGoneLine())
                mline.setVisibility(View.GONE);
        }

        private void setImageFromNet(ImageView imageView, String imageUrl, int placeholder) {
            ImageLoader.loadImage(Glide.with(imageView.getContext()), imageView, imageUrl, placeholder);
        }

    }
}
