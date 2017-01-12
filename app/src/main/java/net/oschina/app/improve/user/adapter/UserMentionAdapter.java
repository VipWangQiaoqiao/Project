package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.Mention;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.Origin;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMentionAdapter extends BaseGeneralRecyclerAdapter<Mention> {
    private OnUserFaceClickListener mListener;

    public UserMentionAdapter(Callback callback) {
        super(callback, ONLY_FOOTER);
        initListener();
    }

    private void initListener() {
        mListener = new UserMentionAdapter.OnUserFaceClickListener() {
            @Override
            public void onClick(View v, int position) {
                Author author = getItem(position).getAuthor();
                if (author != null)
                    OtherUserHomeActivity.show(mCallBack.getContext(), author.getId());
            }
        };
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        MentionViewHolder holder = new UserMentionAdapter.MentionViewHolder(mInflater.inflate(R.layout.item_list_comment, parent, false));
        holder.iv_user_avatar.setTag(R.id.iv_face, holder);
        return holder;
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Mention item, int position) {
        MentionViewHolder viewHolder = (MentionViewHolder) holder;
        Author author = item.getAuthor();
        if (author != null) {
            mCallBack.getImgLoader().load(author.getPortrait()).asBitmap().placeholder(R.mipmap.widget_default_face).into(viewHolder.iv_user_avatar);
            viewHolder.tv_user_name.setText(author.getName());
        }
        viewHolder.iv_user_avatar.setOnClickListener(mListener);
        PlatfromUtil.setPlatFromString(viewHolder.tv_platform, item.getAppClient());
        viewHolder.tv_comment_count.setText(String.valueOf(item.getCommentCount()));
        viewHolder.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate()));
        parseAtUserContent(viewHolder.tv_content, item.getContent());
        Origin origin = item.getOrigin();
        if (origin != null && !TextUtils.isEmpty(origin.getDesc())) {
            viewHolder.tv_origin.setVisibility(View.VISIBLE);
            parseAtUserContent(viewHolder.tv_origin, item.getOrigin().getDesc());
        } else {
            viewHolder.tv_origin.setVisibility(View.GONE);
        }

    }

    private static class MentionViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_user_avatar;
        TextView tv_user_name, tv_time, tv_platform, tv_comment_count;
        TweetTextView tv_content, tv_origin;

        public MentionViewHolder(View itemView) {
            super(itemView);
            iv_user_avatar = (CircleImageView) itemView.findViewById(R.id.iv_user_avatar);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TweetTextView) itemView.findViewById(R.id.tv_content);
            tv_origin = (TweetTextView) itemView.findViewById(R.id.tv_origin);
            tv_platform = (TextView) itemView.findViewById(R.id.tv_platform);
            tv_comment_count = (TextView) itemView.findViewById(R.id.tv_comment_count);
        }
    }

    private abstract class OnUserFaceClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MentionViewHolder holder = (MentionViewHolder) v.getTag(R.id.iv_face);
            onClick(v, holder.getAdapterPosition());
        }

        public abstract void onClick(View v, int position);
    }
}
