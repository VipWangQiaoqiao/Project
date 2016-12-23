package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.user.bean.UserFriends;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fei
 * on 2016/12/23.
 * desc:
 */

public class UserSelectFriendsAdapter extends BaseRecyclerAdapter<UserFriends> {

    public static final int INDEX_TYPE = 0x100;


    public UserSelectFriendsAdapter(Context context, int mode) {
        super(context, mode);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        switch (type) {
            case INDEX_TYPE:
                return new IndexViewHolder(mInflater.inflate(R.layout.activity_item_select_friend_label, parent, false));
        }
        return super.onCreateViewHolder(parent, type);
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, UserFriends item, int position) {

        if (holder instanceof IndexViewHolder) {
            IndexViewHolder indexViewHolder = (IndexViewHolder) holder;
            indexViewHolder.mTvIndexLabel.setText(item.getShowLabel());
        } else {

        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).getShowVIewType() == INDEX_TYPE)
            return INDEX_TYPE;
        return super.getItemViewType(position);
    }

    public static class IndexViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_index_label)
        TextView mTvIndexLabel;

        public IndexViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class UserInfo extends RecyclerView.ViewHolder {

      //  @Bind(R.id)

        public UserInfo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
