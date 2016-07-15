package net.oschina.app.improve.tweet.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.tweet.adapter.TweetSelectImageAdapter;

/**
 * Created by JuQiu
 * on 16/7/15.
 */

public class TweetSelectImageHolder extends RecyclerView.ViewHolder {
    private ImageView mImage;
    private ImageView mDelete;

    private TweetSelectImageHolder(View itemView) {
        super(itemView);

        mImage = (ImageView) itemView.findViewById(R.id.iv_content);
        mDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
    }

    public static TweetSelectImageHolder create(View view, View.OnClickListener listener) {
        TweetSelectImageHolder holder = new TweetSelectImageHolder(view);
        holder.mDelete.setOnClickListener(listener);
        return holder;
    }

    public static TweetSelectImageHolder createByMore(View view, View.OnClickListener listener) {
        TweetSelectImageHolder holder = new TweetSelectImageHolder(view);
        holder.mImage.setOnClickListener(listener);
        holder.mDelete.setVisibility(View.GONE);
        holder.mImage.setImageResource(R.drawable.ic_tweet_add);
        return holder;
    }

    public void bind(int position, TweetSelectImageAdapter.Model model, RequestManager loader) {
        mDelete.setTag(model);
        loader.load(model.path)
                .centerCrop()
                .into(mImage);
    }
}
