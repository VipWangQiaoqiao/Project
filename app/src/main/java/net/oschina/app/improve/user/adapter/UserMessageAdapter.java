package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.util.StringUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMessageAdapter extends BaseGeneralRecyclerAdapter<Message> {
    private OnUserFaceClickListener mListener;

    public UserMessageAdapter(Callback callback) {
        super(callback, ONLY_FOOTER);
        initListener();
    }

    private void initListener() {
        mListener = new OnUserFaceClickListener() {
            @Override
            public void onClick(View v, int position) {
                User author = getItem(position).getSender();
                if (author != null)
                    OtherUserHomeActivity.show(mCallBack.getContext(), author.getId());
            }
        };
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        MessageViewHolder holder = new MessageViewHolder(mInflater.inflate(R.layout.item_list_message, parent, false));
        holder.iv_user_avatar.setTag(R.id.iv_face, holder);
        return holder;
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Message item, int position) {
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        User author = item.getSender();
        if (author != null) {
            mCallBack.getImgLoader().load(author.getPortrait()).asBitmap().placeholder(R.mipmap.widget_default_face).into(messageViewHolder.iv_user_avatar);
            messageViewHolder.tv_user_name.setText(author.getName());
        }
        messageViewHolder.iv_user_avatar.setOnClickListener(mListener);
        parseAtUserContent(messageViewHolder.tv_content, item.getContent());
        messageViewHolder.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate()));
    }


    protected void parseAtUserContent(TextView textView, String text) {
        String content = "";
        if (TextUtils.isEmpty(text)) {
            textView.setText("[图片]");
            return;
        }
        content = text.replaceAll("[\n\\s]+", " ").replaceAll("<[^<>]+>([^<>]*)</[^<>]+>", "$1");
        textView.setText(InputHelper.displayEmoji(mCallBack.getContext().getResources(), content));
    }

    private static class MessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_user_avatar;
        TextView tv_user_name, tv_time;
        TextView tv_content;

        public MessageViewHolder(View itemView) {
            super(itemView);
            iv_user_avatar = (CircleImageView) itemView.findViewById(R.id.iv_user_avatar);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    private abstract class OnUserFaceClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MessageViewHolder holder = (MessageViewHolder) v.getTag(R.id.iv_face);
            onClick(v, holder.getAdapterPosition());
        }

        public abstract void onClick(View v, int position);
    }
}
