package net.oschina.app.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Tweet;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.LinkView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author HuangWenwei
 * 
 * @date 2014年10月10日
 */
public class TweetAdapter extends ListBaseAdapter {

	static class ViewHolder {

		@InjectView(R.id.tv_tweet_name) TextView author;
		@InjectView(R.id.tv_tweet_time) TextView time;
		@InjectView(R.id.tweet_item) LinkView content;
		@InjectView(R.id.tv_tweet_comment_count) TextView commentcount;
		@InjectView(R.id.tv_tweet_platform) TextView platform;
		
		@InjectView(R.id.iv_tweet_face) 
		public AvatarView face;
		
		@InjectView(R.id.iv_tweet_image)
		public ImageView image;


		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	@Override
	protected View getRealView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_tweets, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else 
			vh = (ViewHolder) convertView.getTag();

		Tweet tweet = (Tweet) _data.get(position);

		vh.face.setUserInfo(tweet.getAuthorid(), tweet.getAuthor());
		vh.face.setAvatarUrl(tweet.getPortrait());
		vh.author.setText(tweet.getAuthor());
		vh.time.setText(StringUtils.friendly_time(tweet.getPubDate()));
		vh.content.setLinkText(tweet.getBody());
		vh.commentcount.setText(String.valueOf(tweet.getCommentCount()));
		if(tweet.getImgSmall()!=null){
			vh.image.setVisibility(AvatarView.VISIBLE);
			ImageLoader.getInstance().displayImage(tweet.getImgSmall(), vh.image);
		}
		switch (tweet.getAppclient()) {
		case 2:
			vh.platform.setText("来自：手机");
		case 3:
			vh.platform.setText("来自：Android");
		case 4:
			vh.platform.setText("来自：微信");
		case 5:
			vh.platform.setText("来自：Windows Phone");
		case 6:
			vh.platform.setText("来自：iPhone");
			break;
		default:
			vh.platform.setText("来自：网页");
			break;
		}
		return convertView;
	}
}