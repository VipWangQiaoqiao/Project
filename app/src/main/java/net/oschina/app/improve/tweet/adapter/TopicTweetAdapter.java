package net.oschina.app.improve.tweet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;

/**
 * Created by thanatosx on 2016/11/7.
 */

public class TopicTweetAdapter extends BaseRecyclerAdapter {

    public TopicTweetAdapter(Context context) {
        super(context, ONLY_FOOTER);
        for (int i = 0; i < 10; i++){
            addItem("");
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.list_item_topic_tweet, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Object item, int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
