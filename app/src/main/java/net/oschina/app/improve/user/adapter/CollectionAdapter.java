package net.oschina.app.improve.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.Collection;

/**
 * Created by haibin
 * on 2016/10/18.
 */

public class CollectionAdapter extends BaseRecyclerAdapter<Collection> {
    public CollectionAdapter(Context context) {
        super(context, BOTH_HEADER_FOOTER);
        mState = STATE_HIDE;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return null;
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Collection item, int position) {

    }
}
