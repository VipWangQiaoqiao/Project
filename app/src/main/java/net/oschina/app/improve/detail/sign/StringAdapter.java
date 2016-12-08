package net.oschina.app.improve.detail.sign;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

/**
 * Created by haibin
 * on 2016/12/7.
 */

class StringAdapter extends BaseRecyclerAdapter<String> {
    StringAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new SelectViewHolder(mInflater.inflate(R.layout.item_list_select, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, String item, int position) {
        SelectViewHolder h = (SelectViewHolder) holder;
        h.mTextSelect.setText(item);
    }

    private static class SelectViewHolder extends RecyclerView.ViewHolder {
        TextView mTextSelect;

        SelectViewHolder(View itemView) {
            super(itemView);
            mTextSelect = (TextView) itemView.findViewById(R.id.tv_select);
        }
    }
}
