package net.oschina.app.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Blog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;

/**
 * @author HuangWenwei
 * 
 * @date 2014年9月29日
 */
public class BlogAdapter extends ListBaseAdapter {

	static class ViewHolder {
		public TextView title, source, time;
		public ImageView tip;
		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.tv_title);
			source = (TextView) view.findViewById(R.id.tv_source);
			time = (TextView) view.findViewById(R.id.tv_time);
			tip = (ImageView) view.findViewById(R.id.iv_tip);
		}
	}

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

		Blog blog = (Blog) _data.get(position);

		vh.title.setText(blog.getTitle());
		vh.source.setText(blog.getAuthor());
		return convertView;
	}
}
