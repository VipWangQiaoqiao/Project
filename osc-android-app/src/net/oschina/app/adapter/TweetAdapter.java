package net.oschina.app.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Tweet;
import net.oschina.app.ui.ImagePreviewActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TLog;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.LinkView;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
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
	private final static String AT_HOST_PRE = "http://my.oschina.net";
	private final static String MAIN_HOST = "http://www.oschina.net";

	static class ViewHolder {

		@InjectView(R.id.tv_tweet_name)
		TextView author;
		@InjectView(R.id.tv_tweet_time)
		TextView time;
		@InjectView(R.id.tweet_item)
		TweetTextView content;
		@InjectView(R.id.tv_tweet_comment_count)
		TextView commentcount;
		@InjectView(R.id.tv_tweet_platform)
		TextView platform;

		@InjectView(R.id.iv_tweet_face)
		public AvatarView face;

		@InjectView(R.id.iv_tweet_image)
		public ImageView image;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	@Override
	protected View getRealView(int position, View convertView,
			final ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_tweets, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else
			vh = (ViewHolder) convertView.getTag();

		final Tweet tweet = (Tweet) _data.get(position);

		vh.face.setUserInfo(tweet.getAuthorid(), tweet.getAuthor());
		vh.face.setAvatarUrl(tweet.getPortrait());
		vh.author.setText(tweet.getAuthor());
		vh.time.setText(StringUtils.friendly_time(tweet.getPubDate()));

		vh.content.setMovementMethod(MyLinkMovementMethod.a());
		vh.content.setFocusable(false);
		vh.content.setDispatchToParent(true);
		vh.content.setLongClickable(false);
		Spanned span = Html.fromHtml(modifyPath(tweet.getBody()));
		vh.content.setText(span);
		MyURLSpan.parseLinkText(vh.content, span);

		vh.commentcount.setText(tweet.getCommentCount() + "");
		
		showTweetImage(vh, tweet.getImgSmall(), tweet.getImgBig(), parent.getContext());
		
		vh.platform.setVisibility(View.VISIBLE);
		switch (tweet.getAppclient()) {
		case Tweet.CLIENT_MOBILE:
			vh.platform.setText(R.string.from_mobile);
			break;
		case Tweet.CLIENT_ANDROID:
			vh.platform.setText(R.string.from_android);
			break;
		case Tweet.CLIENT_IPHONE:
			vh.platform.setText(R.string.from_iphone);
			break;
		case Tweet.CLIENT_WINDOWS_PHONE:
			vh.platform.setText(R.string.from_windows_phone);
			break;
		case Tweet.CLIENT_WECHAT:
			vh.platform.setText(R.string.from_wechat);
			break;
		default:
			vh.platform.setText("");
			vh.platform.setVisibility(View.GONE);
			break;
		}
		return convertView;
	}

	private String modifyPath(String message) {
		message = message.replaceAll("(<a[^>]+href=\")/([\\S]+)\"", "$1"
				+ AT_HOST_PRE + "/$2\"");
		message = message.replaceAll(
				"(<a[^>]+href=\")http://m.oschina.net([\\S]+)\"", "$1"
						+ MAIN_HOST + "$2\"");
		return message;
	}

	private void showTweetImage(ViewHolder vh, String imgSmall,
			final String imgBig, final Context context) {
		vh.image.setTag(imgSmall);
		if (imgSmall != null && !TextUtils.isEmpty(imgSmall)) {
			vh.image.setImageResource(R.drawable.pic_bg);
			if (vh.image.getTag() != null && vh.image.getTag().equals(imgSmall)) {
				ImageLoader.getInstance().displayImage(imgSmall, vh.image);
				vh.image.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						ImagePreviewActivity.showImagePrivew(context, 0,
								new String[] { imgBig });
					}
				});
				
			}
			vh.image.setVisibility(AvatarView.VISIBLE);
		} else {
			vh.image.setVisibility(AvatarView.GONE);
		}
	}
}
