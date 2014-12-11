package net.oschina.app.adapter;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.EventList.Event;
import net.oschina.app.bean.Post;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.CircleImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * post（讨论区帖子）适配器
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月9日 下午6:22:54
 *
 */
public class EventAdapter extends ListBaseAdapter {

	static class ViewHolder {
		
		@InjectView(R.id.iv_event_img) ImageView img;
		@InjectView(R.id.tv_event_title) TextView title;
		@InjectView(R.id.tv_event_time) TextView time;
		@InjectView(R.id.tv_event_spot) TextView spot;
		
		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_event, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		Event item = (Event) _data.get(position);
		
		ImageLoader.getInstance().displayImage("http://static.oschina.net/uploads/cover/guangzhou_1032619_195698_vsAud_bi.jpg", vh.img);
		vh.title.setText(item.getTitle());
		vh.time.setText(item.getStartTime());
		vh.spot.setText(item.getSpot());
		
		return convertView;
	}
}
