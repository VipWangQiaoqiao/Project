package net.oschina.app.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Post;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.CircleImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * post（讨论区帖子）适配器
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月9日 下午6:22:54
 *
 */
public class PostAdapter extends ListBaseAdapter {

	static class ViewHolder {
		
		@InjectView(R.id.tv_post_title) TextView title;
		@InjectView(R.id.tv_post_author) TextView author;
		@InjectView(R.id.tv_post_date) TextView time;
		@InjectView(R.id.tv_post_count) TextView comment_count;
		
		@InjectView(R.id.iv_post_face)
		public AvatarView face;
		
		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_post, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		Post post = (Post) _data.get(position);
		
		vh.face.setUserInfo(post.getAuthorId(), post.getAuthor());
		vh.face.setAvatarUrl(post.getPortrait());
		
		vh.title.setText(post.getTitle());
		vh.author.setText(post.getAuthor());
		vh.time.setText(StringUtils.friendly_time(post.getPubDate()));
		vh.comment_count.setText(post.getAnswerCount() + "回/" + post.getViewCount() + "阅");
		return convertView;
	}
}
