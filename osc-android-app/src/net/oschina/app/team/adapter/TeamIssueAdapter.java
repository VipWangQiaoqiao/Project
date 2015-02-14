package net.oschina.app.team.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.team.bean.TeamIssue;
import net.oschina.app.util.StringUtils;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 任务列表适配器
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月14日 下午5:28:51
 * 
 */
public class TeamIssueAdapter extends ListBaseAdapter<TeamIssue> {

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
	ViewHolder vh = null;
	if (convertView == null || convertView.getTag() == null) {
	    convertView = getLayoutInflater(parent.getContext()).inflate(
		    R.layout.list_cell_team_issue, null);
	    vh = new ViewHolder(convertView);
	    convertView.setTag(vh);
	} else {
	    vh = (ViewHolder) convertView.getTag();
	}

	TeamIssue item = (TeamIssue) mDatas.get(position);
	
	vh.comment.setText(item.getReplyCount() + "");

	vh.title.setText(item.getTitle());
	if (item.getState().equals("closed")) {
	    vh.title.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // 中划线
	} else {
	    vh.title.getPaint().setFlags(0);
	}
	
	setText(vh.accept_time, item.getUpdateTime(), true);
	
	vh.author.setText(item.getAuthor().getName());
	if (item.getToUser() == null
		|| TextUtils.isEmpty(item.getToUser().getName())) {
	    vh.to.setText("未指派");
	    vh.touser.setVisibility(View.GONE);
	} else {
	    vh.to.setText("指派给");
	    vh.touser.setText(item.getToUser().getName());
	}
	vh.time.setText(StringUtils.friendly_time(item.getCreateTime()));

	return convertView;
    }

    static class ViewHolder {

	@InjectView(R.id.tv_title)
	TextView title;
	@InjectView(R.id.iv_issue_state)
	ImageView state;
	@InjectView(R.id.tv_accept_time)
	TextView accept_time;
	@InjectView(R.id.tv_author)
	TextView author;
	@InjectView(R.id.tv_to)
	TextView to;
	@InjectView(R.id.tv_touser)
	TextView touser;
	@InjectView(R.id.tv_time)
	TextView time;
	@InjectView(R.id.tv_comment_count)
	TextView comment;

	public ViewHolder(View view) {
	    ButterKnife.inject(this, view);
	}
    }
}
