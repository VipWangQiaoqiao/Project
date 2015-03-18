package net.oschina.app.team.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.team.bean.TeamReply;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.TweetTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 评论适配器 TeamReply.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 * 
 * @data 2015-1-30 下午4:05:00
 */
public class TeamReplyAdapter extends ListBaseAdapter<TeamReply> {

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_team_reply, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TeamReply item = mDatas.get(position);
        vh.name.setText(item.getAuthor().getName());
        vh.avatar.setAvatarUrl(item.getAuthor().getPortrait());
        setContent(vh.content, HTMLUtil.delHTMLTag(item.getContent()));
        vh.time.setText(StringUtils.friendly_time(item.getCreateTime()));

        if (StringUtils.isEmpty(item.getAppName())) {
            vh.from.setVisibility(View.GONE);
        } else {
            vh.from.setVisibility(View.VISIBLE);
            vh.from.setText(item.getAppName());
        }
        return convertView;
    }

    static class ViewHolder {

        @InjectView(R.id.iv_avatar)
        AvatarView avatar;
        @InjectView(R.id.tv_name)
        TextView name;
        @InjectView(R.id.tv_time)
        TextView time;
        @InjectView(R.id.tv_from)
        TextView from;
        @InjectView(R.id.tv_content)
        TweetTextView content;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
