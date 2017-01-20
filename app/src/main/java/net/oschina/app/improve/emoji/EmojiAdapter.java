package net.oschina.app.improve.emoji;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import net.oschina.app.R;
import net.oschina.app.emoji.Emojicon;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

/**
 * Created by haibin
 * on 2017/1/20.
 */

class EmojiAdapter extends BaseRecyclerAdapter<Emojicon> {
    EmojiAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new EmojiViewHolder(new ImageView(mContext));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Emojicon item, int position) {
        int bound = (int) mContext.getResources().getDimension(R.dimen.space_49);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(bound, bound);
        holder.itemView.setLayoutParams(params);
        int padding = (int) mContext.getResources().getDimension(
                R.dimen.space_10);
        holder.itemView.setPadding(padding, padding, padding, padding);
        ((ImageView) holder.itemView).setImageResource(item.getResId());
    }

    private static class EmojiViewHolder extends RecyclerView.ViewHolder {
        EmojiViewHolder(ImageView itemView) {
            super(itemView);
        }
    }
}
