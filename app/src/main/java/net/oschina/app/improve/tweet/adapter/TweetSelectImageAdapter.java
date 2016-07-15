package net.oschina.app.improve.tweet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.tweet.adapter.holder.TweetSelectImageHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/7/15.
 */
public class TweetSelectImageAdapter extends RecyclerView.Adapter<TweetSelectImageHolder> {
    private final int MAX_SIZE = 9;
    private final int TYPE_NONE = 0;
    private final int TYPE_ADD = 1;
    private final List<Model> mModels = new ArrayList<>();
    private Callback mCallback;

    public TweetSelectImageAdapter(Callback callback) {
        mCallback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE)
            return TYPE_NONE;
        else if (position == size) {
            return TYPE_ADD;
        } else {
            return TYPE_NONE;
        }
    }

    @Override
    public TweetSelectImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NONE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet_publish_selecter, parent, false);
            return TweetSelectImageHolder.create(view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Callback callback = mCallback;
                    Object obj = v.getTag();
                    if (callback != null && obj != null && obj instanceof Model) {
                        Model model = (Model) obj;
                        int pos = mModels.indexOf(model);
                        if (pos == -1)
                            return;
                        mModels.remove(pos);
                        notifyItemRemoved(pos);
                    }
                }
            });
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet_publish_selecter, parent, false);
            return TweetSelectImageHolder.createByMore(view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Callback callback = mCallback;
                    if (callback != null) {
                        callback.onLoadMoreClick();
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(TweetSelectImageHolder holder, int position) {
        int size = mModels.size();
        if (size >= MAX_SIZE || size != position) {
            Model model = mModels.get(position);
            holder.bind(position, model, mCallback.getImgLoader());
        }
    }

    @Override
    public int getItemCount() {
        int size = mModels.size();
        if (size == MAX_SIZE) {
            return size;
        } else {
            return ++size;
        }
    }

    public void add(Model model) {
        if (mModels.size() >= MAX_SIZE)
            return;
        mModels.add(model);
    }

    public void add(String path) {
        add(new Model(path));
    }

    public static class Model {
        public Model(String path) {
            this.path = path;
        }

        public String path;
        public boolean isUpload;
    }

    public interface Callback {
        void onLoadMoreClick();

        RequestManager getImgLoader();
    }
}
