package net.oschina.app.improve.git.gist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Gist;

/**
 * 代码片段适配器
 * Created by haibin on 2017/5/10.
 */

class GistAdapter extends BaseRecyclerAdapter<Gist> {
    GistAdapter(Context context) {
        super(context, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new GistViewHolder(mInflater.inflate(R.layout.item_list_gist, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Gist item, int position) {
        GistViewHolder h = (GistViewHolder) holder;
    }

    private static class GistViewHolder extends RecyclerView.ViewHolder {
        GistViewHolder(View itemView) {
            super(itemView);
        }
    }
}
