package net.oschina.app.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.SoftwareList.SoftwareDec;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class SoftwareAdapter extends ListBaseAdapter {
	
	static class ViewHold{
		@InjectView(R.id.tv_software_name)TextView name;
		@InjectView(R.id.tv_software_des)TextView des;
		public ViewHold(View view) {
			ButterKnife.inject(this,view);
		}
	}

	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		
		ViewHold vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_cell_software, null);
			vh = new ViewHold(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHold)convertView.getTag();
		}
		
		SoftwareDec softwareDes = (SoftwareDec) mDatas.get(position);
		vh.name.setText(softwareDes.getName());
		vh.des.setText(softwareDes.getDescription());
		
		return convertView;
	}
}
