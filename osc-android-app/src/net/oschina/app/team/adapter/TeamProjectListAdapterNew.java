package net.oschina.app.team.adapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.team.bean.TeamProject;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 团队项目适配器
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月19日 下午6:00:33
 * 
 */

public class TeamProjectListAdapterNew extends ListBaseAdapter<TeamProject> {
    
    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
	// TODO Auto-generated method stub
	ViewHolder vh;
	if (convertView == null || convertView.getTag() == null) {
	    convertView = View.inflate(parent.getContext(),
		    R.layout.list_cell_team_project, null);
	    vh = new ViewHolder(convertView);
	    convertView.setTag(vh);
	} else {
	    vh = (ViewHolder) convertView.getTag();
	}

	TeamProject item = mDatas.get(position);

	String source = item.getSource();
	if (source == null) {

	} else if (source.equalsIgnoreCase(TeamProject.GITOSC)) {
	    vh.source.setBackgroundResource(R.drawable.ic_comment_count);
	} else if (source.equalsIgnoreCase(TeamProject.GITHUB)) {
	    vh.source.setBackgroundResource(R.drawable.ic_action_comment);
	}

	vh.name.setText(item.getGit().getName());

	return convertView;
    }

    public static class ViewHolder {
	@InjectView(R.id.iv_source)
	ImageView source;
	@InjectView(R.id.tv_project_name)
	TextView name;

	public ViewHolder(View view) {
	    ButterKnife.inject(this, view);
	}
    }

}
