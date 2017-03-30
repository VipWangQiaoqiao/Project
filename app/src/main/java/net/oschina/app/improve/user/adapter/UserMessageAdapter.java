package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.Message;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.utils.parser.StringParser;
import net.oschina.app.improve.widget.IdentityView;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.util.StringUtils;

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
            @SuppressWarnings("ConstantConditions")
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
        messageViewHolder.iv_user_identify.setup(author);
        if (author != null) {
            messageViewHolder.iv_user_avatar.setup(author);
            messageViewHolder.tv_user_name.setText(author.getName());
        } else {
            messageViewHolder.iv_user_avatar.setup(0, "匿名用户", "");
            messageViewHolder.tv_user_name.setText("匿名用户");
        }
        messageViewHolder.iv_user_avatar.setOnClickListener(mListener);
        messageViewHolder.tv_content.setText(StringParser.getInstance().parse(mContext, item.getContent()));
        messageViewHolder.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate()));
    }

    private static class MessageViewHolder extends RecyclerView.ViewHolder {
        PortraitView iv_user_avatar;
        IdentityView iv_user_identify;
        TextView tv_user_name, tv_time;
        TextView tv_content;

         MessageViewHolder(View itemView) {
            super(itemView);
            iv_user_avatar = (PortraitView) itemView.findViewById(R.id.iv_user_avatar);
            iv_user_identify = (IdentityView) itemView.findViewById(R.id.identityView);
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
