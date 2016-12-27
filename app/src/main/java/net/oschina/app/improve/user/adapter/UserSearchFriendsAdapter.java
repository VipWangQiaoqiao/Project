package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.oschina.app.R;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.user.bean.UserFriends;
import net.oschina.app.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    private List<UserFriends> mItems = new ArrayList<>();

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
                return new SearchViewHolder(inflater.inflate(R.layout.activity_item_search_friend_bottom, parent, false));
            default:
                return null;
        }

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        UserFriends item = mItems.get(position);

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

        void onBindView(final UserFriends item, int position) {

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

    static class SearchViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.bt_search)
        Button mBtSearch;

        SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBindView(UserFriends item, int position) {
            mBtSearch.setText("在网络上搜索");
            mBtSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
