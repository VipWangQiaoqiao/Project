package net.oschina.app.improve.git.branch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.git.bean.Branch;

/**
 * Created by haibin
 * on 2017/3/13.
 */

class BranchAdapter extends BaseRecyclerAdapter<Branch> {
    BranchAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new BranchViewHolder(mInflater.inflate(R.layout.item_list_branch, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Branch item, int position) {
        ((BranchViewHolder) holder).mTextBranch.setText(item.getName());
    }

    private static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView mTextBranch;

        BranchViewHolder(View itemView) {
            super(itemView);
            mTextBranch = (TextView) itemView.findViewById(R.id.tv_branch);
        }
    }
}
