package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.widget.TweetTextView;

/**
 * Created by huanghaibin_dev
 * on 2016/8/18.
 */
public class UserSendMessageAdapter extends BaseGeneralRecyclerAdapter<Message> {
    private static final int SENDER = 1;
    private static final int RECEIVER = 2;

    public UserSendMessageAdapter(Callback callback) {
        super(callback, NEITHER);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getSender() == null ? RECEIVER : SENDER;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        if (type == SENDER)
            return new SenderViewHolder(mInflater.inflate(R.layout.item_list_user_send_message, parent, false));
        return new ReceiverViewHolder(mInflater.inflate(R.layout.item_list_receiver_message, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Message item, int position) {
        switch (getItemViewType(position)) {
            case SENDER:
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                senderViewHolder.tv_sender.setText(item.getContent());
                break;
            case RECEIVER:
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                receiverViewHolder.tv_receiver.setText(item.getContent());
                break;
        }
    }

    private static class SenderViewHolder extends RecyclerView.ViewHolder {
        TweetTextView tv_sender;

        public SenderViewHolder(View itemView) {
            super(itemView);
            tv_sender = (TweetTextView) itemView.findViewById(R.id.tv_sender);
        }
    }

    private static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TweetTextView tv_receiver;

        public ReceiverViewHolder(View itemView) {
            super(itemView);
            tv_receiver = (TweetTextView) itemView.findViewById(R.id.tv_receiver);
        }
    }
}
