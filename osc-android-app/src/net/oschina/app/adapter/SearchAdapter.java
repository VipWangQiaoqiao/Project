package net.oschina.app.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.SearchList.SearchResult;
import net.oschina.app.util.StringUtils;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchAdapter extends ListBaseAdapter {

	@SuppressLint("InflateParams")
	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_news, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		SearchResult item = (SearchResult) _data.get(position);

		vh.title.setText(item.getTitle());
		if (!StringUtils.isEmpty(item.getAuthor())) {
			vh.source.setText(item.getAuthor());
		} else {
			vh.source.setVisibility(View.GONE);
		}
		
		if (!StringUtils.isEmpty(item.getPubDate())) {
			vh.time.setText(StringUtils.friendly_time(item.getPubDate()));
		} else {
			vh.time.setVisibility(View.GONE);
		}
		
		vh.tip.setVisibility(View.GONE);
		vh.comment_count.setVisibility(View.GONE);
		return convertView;
	}

	static class ViewHolder {
		public TextView title, source, time,comment_count;
		public ImageView tip;

		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.tv_title);
			source = (TextView) view.findViewById(R.id.tv_source);
			time = (TextView) view.findViewById(R.id.tv_time);
			comment_count = (TextView) view.findViewById(R.id.tv_comment_count);
			tip = (ImageView) view.findViewById(R.id.iv_tip);
		}
	}
}
