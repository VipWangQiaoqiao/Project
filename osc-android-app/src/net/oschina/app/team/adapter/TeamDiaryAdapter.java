package net.oschina.app.team.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.team.bean.TeamDiary;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class TeamDiaryAdapter extends ListBaseAdapter<TeamDiary> {

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.list_cell_team_diary, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        TeamDiary item = mDatas.get(position);

        vh.face.setAvatarUrl(item.getAuthor().getPortrait());
        vh.author.setText(item.getAuthor().getName());
        vh.title.setText(Html.fromHtml(item.getTitle().trim()));
        vh.time.setText(StringUtils.friendly_time(item.getCreateTime()));
        vh.comment_count.setText(item.getReply() + "");

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.tv_author)
        TextView author;
        @InjectView(R.id.tv_title)
        TextView title;
        @InjectView(R.id.tv_date)
        TextView time;
        @InjectView(R.id.tv_count)
        TextView comment_count;

        @InjectView(R.id.iv_face)
        public AvatarView face;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
