package net.oschina.app.improve.git.tree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Tree;

/**
 * Created by haibin
 * on 2017/3/13.
 */

class TreeAdapter extends BaseRecyclerAdapter<Tree> {
    TreeAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new TreeViewHolder(mInflater.inflate(R.layout.item_list_tree, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Tree item, int position) {

    }

    private static class TreeViewHolder extends RecyclerView.ViewHolder {
        TreeViewHolder(View itemView) {
            super(itemView);
        }
    }
}
