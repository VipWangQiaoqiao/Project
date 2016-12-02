package net.oschina.app.improve.detail.general;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.simple.About;

/**
 * Created by haibin
 * on 2016/12/2.
 */

public class AboutAdapter extends BaseRecyclerAdapter<About> {
    public AboutAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new AboutViewHolder(mInflater.inflate(R.layout.item_list_about, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, About item, int position) {
        AboutViewHolder h = (AboutViewHolder) holder;
        h.mTextTitle.setText(item.getTitle());
        ((AboutViewHolder) holder).mTextComCount.setText(String.valueOf(item.getCommentCount()));
    }

    private static class AboutViewHolder extends RecyclerView.ViewHolder {
        TextView mTextTitle, mTextComCount;

        public AboutViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTextComCount = (TextView) itemView.findViewById(R.id.tv_com_count);
        }
    }
}
