package net.oschina.app.improve.tweet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.tweet.activities.TopicTweetActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thanatosx on 2016/11/7.
 */

public class TopicTweetAdapter extends BaseRecyclerAdapter implements View.OnClickListener {

    private static final int[] images = {
            R.mipmap.bg_topic_1, R.mipmap.bg_topic_2, R.mipmap.bg_topic_3,
            R.mipmap.bg_topic_4, R.mipmap.bg_topic_5
    };

    public TopicTweetAdapter(Context context) {
        super(context, ONLY_FOOTER);
        for (int i = 0; i < 10; i++) {
            addItem("");
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.list_item_topic_tweet, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, Object item, int position) {
        ViewHolder holder = (ViewHolder) h;
        holder.mViewWallpaper.setImageResource(images[position % 5]);
        holder.mLayoutItem1.setVisibility(View.VISIBLE);
        holder.mLayoutItem2.setVisibility(View.VISIBLE);
        holder.mLayoutItem3.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        TopicTweetActivity.show(v.getContext());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_wallpaper)
        ImageView mViewWallpaper;
        @Bind(R.id.layout_item_1)
        LinearLayout mLayoutItem1;
        @Bind(R.id.layout_item_2)
        LinearLayout mLayoutItem2;
        @Bind(R.id.layout_item_3)
        LinearLayout mLayoutItem3;
        @Bind(R.id.layout_bottom)
        RelativeLayout mLayoutBottom;
        @Bind(R.id.tv_count)
        TextView mViewCount;

        @Bind(R.id.layout_wrapper)
        LinearLayout mLayoutWrapper;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mLayoutWrapper.setOnClickListener(TopicTweetAdapter.this);
        }
    }
}
