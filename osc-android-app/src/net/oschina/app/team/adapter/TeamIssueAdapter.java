package net.oschina.app.team.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	vh.author.setText(item.getAuthor().getName());
	if (item.getToUser() == null
		|| TextUtils.isEmpty(item.getToUser().getName())) {
	    vh.to.setText("未指派");
	    vh.touser.setVisibility(View.GONE);
	} else {
	    vh.to.setText("指派给");
	    vh.touser.setText(item.getToUser().getName());
	}
	
	setText(vh.time, StringUtils.friendly_time(item.getCreateTime()));
	
	if (item.getProject() != null && item.getProject().getGit() != null) {
	    vh.project.setVisibility(View.VISIBLE);
	    String gitState = item.getGitpush() == TeamIssue.TEAM_ISSUE_GITPUSHED ? "" : " -未同步";
	    setText(vh.project, item.getProject().getGit().getName()+ gitState);
	} else {
	    vh.project.setVisibility(View.GONE);
	}
	
	String deadlineTime = item.getAcceptTime();
	if (!StringUtils.isEmpty(deadlineTime)) {
	    vh.accept_time.setVisibility(View.VISIBLE);
	    setText(vh.accept_time, getDeadlineTime(item), true);
	} else {
	    vh.accept_time.setVisibility(View.GONE);
	}
	
	if (item.getAttachments() != 0) {
	    vh.attachments.setVisibility(View.VISIBLE);
	    vh.attachments.setText("附件" + item.getAttachments() + "");
	} else {
	    vh.attachments.setVisibility(View.GONE);
	}
	
	if (item.getChildIssues() != null && item.getChildIssues().getTotalCount() != 0) {
	    vh.childissues.setVisibility(View.VISIBLE);
	    setText(vh.childissues, "子任务" + item.getChildIssues().getTotalCount() + "");
	} else {
	    vh.childissues.setVisibility(View.GONE);
	}
	
	if (item.getRelations() != 0) {
	    vh.relations.setVisibility(View.VISIBLE);
	    vh.relations.setText("关联" + item.getRelations());
	} else {
	    vh.relations.setVisibility(View.GONE);
	}

	return convertView;
    }
    
    private String getDeadlineTime(TeamIssue teamIssue) {
	SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
	Date date = StringUtils.toDate(teamIssue.getUpdateTime(), dataFormat);
	return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
    }

    static class ViewHolder {
	
	@InjectView(R.id.iv_issue_state)
	ImageView state;
	@InjectView(R.id.tv_title)
	TextView title;
	@InjectView(R.id.tv_project)
	TextView project;
	@InjectView(R.id.tv_attachments)
	TextView attachments;//附件
	@InjectView(R.id.tv_childissues)
	TextView childissues;// 子任务
	@InjectView(R.id.tv_relations)
	TextView relations;// 关联任务
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
