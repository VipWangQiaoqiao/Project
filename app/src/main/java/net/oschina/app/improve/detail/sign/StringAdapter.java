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

class StringAdapter extends BaseRecyclerAdapter<StringAdapter.Select> {
    StringAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new SelectViewHolder(mInflater.inflate(R.layout.item_list_select, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Select item, int position) {
        SelectViewHolder h = (SelectViewHolder) holder;
        h.mTextSelect.setText(item.getLabel());
        h.mTextSelect.setTextColor(item.isEnable() ? 0xff111111 : 0xff9A9A9A);
    }

    private static class SelectViewHolder extends RecyclerView.ViewHolder {
        TextView mTextSelect;

        SelectViewHolder(View itemView) {
            super(itemView);
            mTextSelect = (TextView) itemView.findViewById(R.id.tv_select);
        }
    }

    public static class Select {
        private boolean enable;
        private String label;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}
