package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.Mention;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMentionAdapter extends BaseGeneralRecyclerAdapter<Mention> {
    public UserMentionAdapter(Callback callback) {
        super(callback, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new UserMentionAdapter.MentionViewHolder(mInflater.inflate(R.layout.item_list_mention, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Mention item, int position) {

    }

    private static class MentionViewHolder extends RecyclerView.ViewHolder{
        public MentionViewHolder(View itemView) {
            super(itemView);
        }
    }
}
