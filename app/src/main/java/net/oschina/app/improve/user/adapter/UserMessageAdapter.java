package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.utils.StreamUtils;
import net.oschina.app.util.StringUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMessageAdapter extends BaseGeneralRecyclerAdapter<Message> {
    public UserMessageAdapter(Callback callback) {
        super(callback, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new MessageViewHolder(mInflater.inflate(R.layout.item_list_message, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Message item, int position) {
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        Author author = item.getSender();
        mCallBack.getImgLoader().load(author.getPortrait()).asBitmap().into(messageViewHolder.iv_user_avatar);
        messageViewHolder.tv_user_name.setText(author.getName());
        messageViewHolder.tv_content.setText(item.getContent());
        messageViewHolder.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate()));
    }

    private static class MessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_user_avatar;
        TextView tv_user_name, tv_time, tv_content;

        public MessageViewHolder(View itemView) {
            super(itemView);
            iv_user_avatar = (CircleImageView) itemView.findViewById(R.id.iv_user_avatar);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
