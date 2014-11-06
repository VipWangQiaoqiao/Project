package net.oschina.app.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.FriendsList.Friend;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.AvatarView;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 好友列表适配器
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年11月6日 上午11:22:27
 *
 */
public class FriendAdapter extends ListBaseAdapter {

	@SuppressLint("InflateParams")
	@Override
	protected View getRealView(int position, View convertView,
			final ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_friend, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final Friend item = (Friend) _data.get(position);

		vh.name.setText(item.getName());
		vh.desc.setText(item.getExpertise());
		vh.gender.setImageResource(item.getGender() == 1 ? R.drawable.userinfo_icon_male
				: R.drawable.userinfo_icon_female);
		
		vh.avatar.setAvatarUrl(item.getPortrait());
		vh.avatar.setUserInfo(item.getUserid(), item.getName());

		return convertView;
	}

	static class ViewHolder {
		
		@InjectView(R.id.tv_name) TextView name;
		@InjectView(R.id.tv_desc) TextView desc;
		@InjectView(R.id.iv_gender) ImageView gender;
		@InjectView(R.id.iv_avatar) AvatarView avatar;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
