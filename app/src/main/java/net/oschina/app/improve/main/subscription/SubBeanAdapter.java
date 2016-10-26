package net.oschina.app.improve.main.subscription;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.SubBean;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class SubBeanAdapter extends BaseRecyclerAdapter<SubBean> {
    public SubBeanAdapter(Context context, int mode) {
        super(context, mode);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new SubViewHolder(mInflater.inflate(R.layout.item_list_sub, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, SubBean item, int position) {

    }

    private static class SubViewHolder extends RecyclerView.ViewHolder {
        public SubViewHolder(View itemView) {
            super(itemView);
        }
    }
}
