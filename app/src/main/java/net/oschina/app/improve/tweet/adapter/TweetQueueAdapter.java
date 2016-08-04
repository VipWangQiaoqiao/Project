package net.oschina.app.improve.tweet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.improve.tweet.service.TweetPublishModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/8/03.
 */
public class TweetQueueAdapter extends RecyclerView.Adapter<TweetQueueAdapter.Holder> {
    private final List<TweetPublishModel> mModels = new ArrayList<>();
    private Callback mCallback;

    public TweetQueueAdapter(Callback callback) {
        mCallback = callback;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet_queue, parent, false);
        return new Holder(view, new Holder.HolderListener() {
            @Override
            public void onDelete(TweetPublishModel model) {
                Callback callback = mCallback;
                if (callback != null) {
                    callback.onClickDelete(model);
                }
            }

            @Override
            public void onContinue(TweetPublishModel model) {
                Callback callback = mCallback;
                if (callback != null) {
                    callback.onClickContinue(model);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        holder.bind(position, mModels.get(position), mCallback.getImgLoader());
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void add(TweetPublishModel model){
        mModels.add(model);
        notifyDataSetChanged();
    }

    public void remove(TweetPublishModel model) {

    }


    public interface Callback {
        RequestManager getImgLoader();

        void onClickContinue(TweetPublishModel model);

        void onClickDelete(TweetPublishModel model);
    }

    /**
     * Holder
     */
    static class Holder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private Button mContinue;
        private Button mDelete;
        private HolderListener mListener;

        private Holder(final View itemView, HolderListener listener) {
            super(itemView);
            mListener = listener;

            mTitle = (TextView)itemView.findViewById(R.id.tv_title);
            mContinue = (Button) itemView.findViewById(R.id.btn_continue);
            mDelete = (Button) itemView.findViewById(R.id.btn_delete);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = Holder.this.itemView.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof TweetPublishModel) {
                        holderListener.onDelete((TweetPublishModel) obj);
                    }
                }
            });
            mContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = Holder.this.itemView.getTag();
                    final HolderListener holderListener = mListener;
                    if (holderListener != null && obj != null && obj instanceof TweetPublishModel) {
                        holderListener.onContinue((TweetPublishModel) obj);
                    }
                }
            });
        }

        public void bind(int position, TweetPublishModel model, RequestManager loader) {
            itemView.setTag(model);

        }

        /**
         * Holder 与Adapter之间的桥梁
         */
        interface HolderListener {
            void onDelete(TweetPublishModel model);

            void onContinue(TweetPublishModel model);
        }
    }

}
