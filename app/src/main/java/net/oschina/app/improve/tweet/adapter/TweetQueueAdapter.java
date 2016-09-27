package net.oschina.app.improve.tweet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.tweet.service.TweetPublishModel;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.widget.TweetTextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/8/03.
 */
public class TweetQueueAdapter extends RecyclerView.Adapter<TweetQueueAdapter.Holder> {
    private final List<TweetPublishModel> mModels = new ArrayList<>();
    private Callback mCallback;
    private static DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TweetQueueAdapter(Callback callback) {
        mCallback = callback;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tweet_queue, parent, false);
        return new Holder(view, new Holder.HolderListener() {
            @Override
            public void onDelete(TweetPublishModel model) {
                remove(model);
                Callback callback = mCallback;
                if (callback != null) {
                    callback.onClickDelete(model);
                }
            }

            @Override
            public void onContinue(TweetPublishModel model) {
                remove(model);
                Callback callback = mCallback;
                if (callback != null) {
                    callback.onClickContinue(model);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        holder.bind(position, mModels.get(position), mCallback.getImageLoader());
    }

    @Override
    public int getItemCount() {
        return mModels.size();
    }

    public void add(List<TweetPublishModel> models) {
        Log.e("TAG", models.size() + "");
        mModels.addAll(models);
        notifyDataSetChanged();
    }

    public void remove(TweetPublishModel model) {
        int pos = mModels.indexOf(model);
        if (pos != -1) {
            mModels.remove(pos);
            notifyItemRemoved(pos);
        }
    }


    public interface Callback {
        RequestManager getImageLoader();

        void onClickContinue(TweetPublishModel model);

        void onClickDelete(TweetPublishModel model);
    }

    /**
     * Holder
     */
    static class Holder extends RecyclerView.ViewHolder {
        private TweetTextView mTitle;
        private TextView mDate;
        private TextView mLog;
        private Button mContinue;
        private Button mDelete;
        private HolderListener mListener;

        private Holder(final View itemView, HolderListener listener) {
            super(itemView);
            mListener = listener;

            mTitle = (TweetTextView) itemView.findViewById(R.id.tv_title);
            mLog = (TextView) itemView.findViewById(R.id.tv_log);
            mDate = (TextView) itemView.findViewById(R.id.tv_date);
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

            Context context = itemView.getContext();

            Spannable spannable = AssimilateUtils.assimilateOnlyAtUser(context, model.getContent());
            spannable = AssimilateUtils.assimilateOnlyTag(context, spannable);
            spannable = AssimilateUtils.assimilateOnlyLink(context, spannable);
            spannable = InputHelper.displayEmoji(context.getResources(), spannable);
            mTitle.setText(spannable);
            mTitle.setMovementMethod(LinkMovementMethod.getInstance());
            mTitle.setFocusable(false);
            mTitle.setDispatchToParent(true);
            mTitle.setLongClickable(false);

            mLog.setText(String.format("Error:%s.",
                    model.getErrorString() == null ? "null" : model.getErrorString()));
            mDate.setText(FORMAT.format(new Date(model.getDate())));
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
