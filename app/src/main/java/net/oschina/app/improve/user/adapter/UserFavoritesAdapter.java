package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.user.bean.UserFavorites;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by fei on 2016/8/30.
 * desc:
 */

public class UserFavoritesAdapter extends BaseRecyclerAdapter<UserFavorites> {

    public UserFavoritesAdapter(Context context, int mode) {
        super(context, mode);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        View view = mInflater.inflate(R.layout.list_cell_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, UserFavorites item, int position) {
        ViewHolder vh = (ViewHolder) holder;
        vh.tvTitle.setText(item.getTitle());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_favorite_title)
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
