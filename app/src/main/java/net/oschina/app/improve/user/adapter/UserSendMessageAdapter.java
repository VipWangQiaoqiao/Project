package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by huanghaibin_dev
 * on 2016/8/18.
 */
public class UserSendMessageAdapter extends BaseGeneralRecyclerAdapter<Message> {
    private static final int SENDER = 1;
    private static final int RECEIVER = 2;
    private long authorId;

    public UserSendMessageAdapter(Callback callback) {
        super(callback, NEITHER);
        authorId = Long.parseLong(String.valueOf(AppContext.getInstance().getLoginUid()));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getSender().getId() == authorId ? SENDER : RECEIVER;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        if (type == SENDER)
            return new SenderViewHolder(mInflater.inflate(R.layout.item_list_user_send_message, parent, false));
        return new ReceiverViewHolder(mInflater.inflate(R.layout.item_list_receiver_message, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Message item, int position) {
        Message preMessage = position != 0 ? getItem(position - 1) : null;
        switch (getItemViewType(position)) {
            case SENDER:
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                parseAtUserContent(senderViewHolder.tv_sender, item.getContent());
                formatTime(preMessage,item,senderViewHolder.tv_send_time);
                break;
            case RECEIVER:
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                parseAtUserContent(receiverViewHolder.tv_receiver, item.getContent());
                formatTime(preMessage,item,receiverViewHolder.tv_send_time);
                break;
        }
    }

    private void formatTime(Message preMessage, Message item, TextView tv_time) {
        tv_time.setVisibility(View.GONE);
        if (preMessage == null ) {
            formatTime(tv_time, item.getPubDate());
            tv_time.setVisibility(View.VISIBLE);
        }else {
            if(checkTime(preMessage.getPubDate(), item.getPubDate())){
                formatTime(tv_time, item.getPubDate());
                tv_time.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean checkTime(String firstTime, String secondTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            long first = format.parse(firstTime).getTime();
            long second = format.parse(secondTime).getTime();
            return second - first > 300000;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String formatWeek(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        return format.format(date);
    }

    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        return format.format(date);
    }

    private void formatTime(TextView tv_time, String time) {
        if (TextUtils.isEmpty(time)) return;
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        Date date = StringUtils.toDate(time);
        tv_time.setText(formatWeek(date) + "," + formatDate(date) + "," + timeFormat.format(date));
    }

    /**
     * 倒序
     *
     * @param items items
     */
    @Override
    public void addAll(List<Message> items) {
        if (items != null) {
            mItems.addAll(0, items);
            notifyDataSetChanged();
        }

    }

    private static class SenderViewHolder extends RecyclerView.ViewHolder {
        TweetTextView tv_sender;
        TextView tv_send_time;

        public SenderViewHolder(View itemView) {
            super(itemView);
            tv_sender = (TweetTextView) itemView.findViewById(R.id.tv_sender);
            tv_send_time = (TextView) itemView.findViewById(R.id.tv_send_time);
        }
    }

    private static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TweetTextView tv_receiver;
        TextView tv_send_time;

        public ReceiverViewHolder(View itemView) {
            super(itemView);
            tv_receiver = (TweetTextView) itemView.findViewById(R.id.tv_receiver);
            tv_send_time = (TextView) itemView.findViewById(R.id.tv_send_time);
        }
    }
}
