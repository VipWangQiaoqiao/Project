package net.oschina.app.improve.user.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.improve.utils.parser.TweetParser;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;
import net.oschina.common.widget.Loading;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by huanghaibin_dev
 * on 2016/8/18.
 */
@SuppressLint("SimpleDateFormat")
public class UserSendMessageAdapter extends BaseGeneralRecyclerAdapter<Message> {
    private static final int SENDER = 1;
    private static final int SENDER_PICTURE = 2;
    private static final int RECEIVER = 3;
    private static final int RECEIVER_PICTURE = 4;
    private long authorId;

    public UserSendMessageAdapter(Callback callback) {
        super(callback, NEITHER);
        authorId = AccountHelper.getUserId();
    }

    @SuppressWarnings("all")
    @Override
    public int getItemViewType(int position) {
        Message item = getItem(position);
        if (item.getSender().getId() == authorId) {//如果是个人发送的私信
            if (Message.TYPE_IMAGE == item.getType())
                return SENDER_PICTURE;
            return SENDER;
        } else {
            if (Message.TYPE_IMAGE == item.getType())
                return RECEIVER_PICTURE;
            return RECEIVER;
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        if (type == SENDER)
            return new SenderViewHolder(mInflater.inflate(R.layout.item_list_user_send_message, parent, false));
        else if (type == SENDER_PICTURE)
            return new SenderPictureViewHolder(mInflater.inflate(R.layout.item_list_user_send_message_picture, parent, false));
        else if (type == RECEIVER)
            return new ReceiverViewHolder(mInflater.inflate(R.layout.item_list_receiver_message, parent, false));
        else
            return new ReceiverPictureViewHolder(mInflater.inflate(R.layout.item_list_receiver_message_picture, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, final Message item, int position) {
        Message preMessage = position != 0 ? getItem(position - 1) : null;
        switch (getItemViewType(position)) {
            case SENDER:
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                //parseAtUserContent(senderViewHolder.tv_sender, item.getContent());
                senderViewHolder.tv_sender.setText(TweetParser.getInstance().parse(mContext, item.getContent()));
                formatTime(preMessage, item, senderViewHolder.tv_send_time);
                break;
            case SENDER_PICTURE:
                final SenderPictureViewHolder senderPictureViewHolder = (SenderPictureViewHolder) holder;
                if (item.getId() == 0) {
                    mCallBack.getImgLoader()
                            .load(item.getResource())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.mipmap.ic_split_graph)
                            .into(senderPictureViewHolder.iv_sender_picture);
                    senderPictureViewHolder.loading.setVisibility(View.VISIBLE);
                    senderPictureViewHolder.loading.start();
                    senderPictureViewHolder.iv_resend.setVisibility(View.INVISIBLE);
                } else if (item.getId() == -1) {
                    mCallBack.getImgLoader()
                            .load(item.getResource())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.mipmap.ic_split_graph)
                            .into(senderPictureViewHolder.iv_sender_picture);
                    senderPictureViewHolder.loading.setVisibility(View.GONE);
                    senderPictureViewHolder.loading.stop();
                    senderPictureViewHolder.iv_resend.setVisibility(View.VISIBLE);
                } else {
                    senderPictureViewHolder.loading.setVisibility(View.VISIBLE);
                    senderPictureViewHolder.loading.start();
                    senderPictureViewHolder.iv_resend.setVisibility(View.INVISIBLE);
                    Glide.clear(senderPictureViewHolder.iv_sender_picture);
                    mCallBack.getImgLoader()
                            .load(AppOperator.getGlideUrlByUser(item.getResource()))
                            .listener(new RequestListener<GlideUrl, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    senderPictureViewHolder.loading.setVisibility(View.GONE);
                                    senderPictureViewHolder.loading.stop();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    senderPictureViewHolder.loading.setVisibility(View.GONE);
                                    senderPictureViewHolder.loading.stop();
                                    return false;
                                }
                            })
                            .placeholder(R.color.list_divider_color)
                            .error(R.mipmap.ic_split_graph)
                            .into(senderPictureViewHolder.iv_sender_picture);

                }
                formatTime(preMessage, item, senderPictureViewHolder.tv_send_time);
                break;
            case RECEIVER:
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                //parseAtUserContent(receiverViewHolder.tv_receiver, item.getContent());
                receiverViewHolder.tv_receiver.setText(TweetParser.getInstance().parse(mContext, item.getContent()));
                formatTime(preMessage, item, receiverViewHolder.tv_send_time);
                break;
            case RECEIVER_PICTURE:
                final ReceiverPictureViewHolder receiverPictureViewHolder = (ReceiverPictureViewHolder) holder;
                receiverPictureViewHolder.loading.setVisibility(View.VISIBLE);
                receiverPictureViewHolder.loading.start();
                mCallBack.getImgLoader()
                        .load(AppOperator.getGlideUrlByUser(item.getResource()))
                        .listener(new RequestListener<GlideUrl, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
                                receiverPictureViewHolder.loading.setVisibility(View.GONE);
                                receiverPictureViewHolder.loading.stop();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                receiverPictureViewHolder.loading.setVisibility(View.GONE);
                                receiverPictureViewHolder.loading.stop();
                                return false;
                            }
                        })
                        .placeholder(R.color.list_divider_color)
                        .error(R.mipmap.ic_split_graph)
                        .into(receiverPictureViewHolder.iv_receiver_picture);
                formatTime(preMessage, item, receiverPictureViewHolder.tv_send_time);
                break;
        }
    }


    private void formatTime(Message preMessage, Message item, TextView tv_time) {
        tv_time.setVisibility(View.GONE);
        if (preMessage == null) {
            formatTime(tv_time, item.getPubDate());
            tv_time.setVisibility(View.VISIBLE);
        } else {
            if (checkTime(preMessage.getPubDate(), item.getPubDate())) {
                formatTime(tv_time, item.getPubDate());
                tv_time.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean checkTime(String firstTime, String secondTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.CHINESE);
        return format.format(date);
    }

    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        return format.format(date);
    }

    private void formatTime(TextView tv_time, String time) {
        if (TextUtils.isEmpty(time)) return;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date date = StringUtils.toDate(time);
        tv_time.setText(formatWeek(date) + ", " + formatDate(date) + ", " + timeFormat.format(date));
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

        SenderViewHolder(View itemView) {
            super(itemView);
            tv_sender = (TweetTextView) itemView.findViewById(R.id.tv_sender);
            tv_send_time = (TextView) itemView.findViewById(R.id.tv_send_time);
            tv_sender.setMovementMethod(LinkMovementMethod.getInstance());
            tv_sender.setFocusable(false);
            tv_sender.setDispatchToParent(true);
            tv_sender.setLongClickable(false);
        }
    }

    private static class SenderPictureViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_sender_picture, iv_resend;
        TextView tv_send_time;
        Loading loading;

        SenderPictureViewHolder(View itemView) {
            super(itemView);
            iv_sender_picture = (ImageView) itemView.findViewById(R.id.iv_sender_picture);
            iv_resend = (ImageView) itemView.findViewById(R.id.iv_resend);
            tv_send_time = (TextView) itemView.findViewById(R.id.tv_send_time);
            loading = (Loading) itemView.findViewById(R.id.loading);
        }
    }

    private static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TweetTextView tv_receiver;
        TextView tv_send_time;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            tv_receiver = (TweetTextView) itemView.findViewById(R.id.tv_receiver);
            tv_send_time = (TextView) itemView.findViewById(R.id.tv_send_time);
            tv_receiver.setMovementMethod(LinkMovementMethod.getInstance());
            tv_receiver.setFocusable(false);
            tv_receiver.setDispatchToParent(true);
            tv_receiver.setLongClickable(false);
        }
    }

    private static class ReceiverPictureViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_receiver_picture;
        TextView tv_send_time;
        Loading loading;

        ReceiverPictureViewHolder(View itemView) {
            super(itemView);
            iv_receiver_picture = (ImageView) itemView.findViewById(R.id.iv_receiver_picture);
            tv_send_time = (TextView) itemView.findViewById(R.id.tv_send_time);
            loading = (Loading) itemView.findViewById(R.id.loading);
        }
    }
}
