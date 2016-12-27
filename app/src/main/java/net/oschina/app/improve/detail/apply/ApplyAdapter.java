package net.oschina.app.improve.detail.apply;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.ApplyUser;

/**
 * Created by haibin
 * on 2016/12/27.
 */

public class ApplyAdapter extends BaseRecyclerAdapter<ApplyUser> {
    public ApplyAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return null;
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, ApplyUser item, int position) {

    }
}
