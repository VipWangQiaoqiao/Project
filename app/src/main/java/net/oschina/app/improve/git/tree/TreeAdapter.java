package net.oschina.app.improve.git.tree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        TreeViewHolder h = (TreeViewHolder) holder;
        h.mImageType.setImageResource(item.isFile() ? R.mipmap.ic_file : R.mipmap.ic_folder);
        h.mTextName.setText(item.getName());
    }

    private static class TreeViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageType;
        TextView mTextName;

        TreeViewHolder(View itemView) {
            super(itemView);
            mImageType = (ImageView) itemView.findViewById(R.id.iv_tree_type);
            mTextName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
