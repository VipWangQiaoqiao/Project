package net.oschina.app.improve.user.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.emoji.InputHelper;
import net.oschina.app.improve.base.adapter.BaseGeneralRecyclerAdapter;
import net.oschina.app.improve.bean.Mention;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.utils.AssimilateUtils;
import net.oschina.app.util.PlatfromUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.TweetTextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */

public class UserMentionAdapter extends BaseGeneralRecyclerAdapter<Mention> {
    public UserMentionAdapter(Callback callback) {
        super(callback, ONLY_FOOTER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new UserMentionAdapter.MentionViewHolder(mInflater.inflate(R.layout.item_list_comment, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Mention item, int position) {
//        MentionViewHolder viewHolder = (MentionViewHolder) holder;
//        Author author = item.getAuthor();
//        mCallBack.getImgLoader().load(author.getPortrait()).asBitmap().into(viewHolder.iv_user_avatar);
//        viewHolder.tv_user_name.setText(author.getName());
//        PlatfromUtil.setPlatFromString(viewHolder.tv_platform, 1);
//        viewHolder.tv_comment_count.setText(String.valueOf(2));
//        viewHolder.tv_time.setText(StringUtils.formatSomeAgo(item.getPubDate()));
//        parseContent(viewHolder.tv_content, item.getContent());
//        parseContent(viewHolder.tv_origin, item.getOrigin().getDesc());
    }

    private void parseContent(TweetTextView textView, String text) {
        String content = "";
        if (TextUtils.isEmpty(text)) return;
        content = text.replaceAll("[\n\\s]+", " ");
        Spannable spannable = AssimilateUtils.assimilateOnlyAtUser(mCallBack.getContext(), content);
        spannable = AssimilateUtils.assimilateOnlyTag(mCallBack.getContext(), spannable);
        spannable = AssimilateUtils.assimilateOnlyLink(mCallBack.getContext(), spannable);
        spannable = InputHelper.displayEmoji(mCallBack.getContext().getResources(), spannable);
        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setFocusable(false);
        textView.setDispatchToParent(true);
        textView.setLongClickable(false);
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
}
