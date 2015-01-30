package net.oschina.app.team.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.team.bean.TeamIssue;
import android.view.View;
import android.view.ViewGroup;
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
public class TeamIssueAdapter extends ListBaseAdapter {

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
		
		vh.title.setText(item.getTitle());
		
		return convertView;
	}
	
	static class ViewHolder {
		
		@InjectView(R.id.tv_title) TextView title;
		
		public ViewHolder(View view) {
			ButterKnife.inject(this,view);
		}
	}
}
