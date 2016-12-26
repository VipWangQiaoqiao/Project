package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.oschina.app.R;
import net.oschina.app.improve.user.bean.UserFriends;
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
    private List<UserFriends> mItems = new ArrayList<>();

    public UserSelectFriendsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.e(TAG, "onCreateViewHolder: ---->" + viewType);
        LayoutInflater inflater = this.mInflater;
        switch (viewType) {
            case INDEX_TYPE:
                return new IndexViewHolder(inflater.inflate(R.layout.activity_item_select_friend_label, parent, false));
            case USER_TYPE:
                return new UserInfoViewHolder(inflater.inflate(R.layout.activity_item_select_friend, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserFriends item = mItems.get(position);

        if (holder instanceof IndexViewHolder) {
            ((IndexViewHolder) holder).onBindView(item, position);
        } else {
            ((UserInfoViewHolder) holder).onBindView(item, position);
        }

    }

    @Override
    public int getItemViewType(int position) {
        List<UserFriends> item = this.mItems;
        return item.get(position).getShowViewType();
    }

    @Override
    public int getItemCount() {
        List<UserFriends> item = this.mItems;
        return item.size();
    }

    public void addItems(List<UserFriends> items) {
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

        void onBindView(UserFriends item, int position) {
            mTvIndexLabel.setText(item.getShowLabel());
        }
    }

    static class UserInfoViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        @Bind(R.id.iv_portrait)
        CircleImageView mCirclePortrait;
        @Bind(R.id.tv_name)
        TextView mtvName;
        @Bind(R.id.cb_check)
        CheckBox mCheckBox;

        UserInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(UserFriends item, int position) {
            setImageFromNet(mCirclePortrait, item.getPortrait(), R.mipmap.widget_dface);
            mtvName.setText(item.getName());
            mCheckBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }

        private void setImageFromNet(ImageView imageView, String imageUrl, int placeholder) {
            ImageLoader.loadImage(Glide.with(imageView.getContext()), imageView, imageUrl, placeholder);
        }
    }
}
