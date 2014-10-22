package net.oschina.app.adapter;

import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.Active.ObjectReply;
import net.oschina.app.bean.Tweet;
import net.oschina.app.ui.ImagePreviewActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class ActiveAdapter extends ListBaseAdapter {
	private final static String AT_HOST_PRE = "http://my.oschina.net";
	private final static String MAIN_HOST = "http://www.oschina.net";
	private DisplayImageOptions options;

	public ActiveAdapter(){
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true).postProcessor(new BitmapProcessor() {

					@Override
					public Bitmap process(Bitmap arg0) {
						return arg0;
					}
				}).build();
	}
	
	@SuppressLint("InflateParams")
	@Override
	protected View getRealView(int position, View convertView,final ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_active, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final Active item = (Active) _data.get(position);

		vh.name.setText(item.getAuthor());

		vh.action.setText(UIHelper.parseActiveAction(item.getObjectType(),
				item.getObjectCatalog(), item.getObjectTitle()));

		if (TextUtils.isEmpty(item.getMessage())) {
			vh.body.setVisibility(View.GONE);
		} else {
			vh.body.setMovementMethod(MyLinkMovementMethod.a());
			vh.body.setFocusable(false);
			vh.body.setDispatchToParent(true);
			vh.body.setLongClickable(false);
			Spanned span = Html.fromHtml(modifyPath(item.getMessage()));
			vh.body.setText(span);
			MyURLSpan.parseLinkText(vh.body, span);
		}
		
		ObjectReply reply = item.getObjectReply();
		if (reply != null) {
			vh.reply.setMovementMethod(MyLinkMovementMethod.a());
			vh.reply.setFocusable(false);
			vh.reply.setDispatchToParent(true);
			vh.reply.setLongClickable(false);
			Spanned span = UIHelper.parseActiveReply(reply.objectName, reply.objectBody);
			vh.reply.setText(span);//
			MyURLSpan.parseLinkText(vh.reply, span);
			vh.lyReply.setVisibility(TextView.VISIBLE);
		} else {
			vh.reply.setText("");
			vh.lyReply.setVisibility(TextView.GONE);
		}
		
		vh.time.setText(StringUtils.friendly_time(item.getPubDate()));

		vh.from.setVisibility(View.VISIBLE);
		switch (item.getAppClient()) {
		default:
			vh.from.setText("");
			break;
		case Tweet.CLIENT_MOBILE:
			vh.from.setText(R.string.from_mobile);
			break;
		case Tweet.CLIENT_ANDROID:
			vh.from.setText(R.string.from_android);
			break;
		case Tweet.CLIENT_IPHONE:
			vh.from.setText(R.string.from_iphone);
			break;
		case Tweet.CLIENT_WINDOWS_PHONE:
			vh.from.setText(R.string.from_windows_phone);
			break;
		case Tweet.CLIENT_WECHAT:
			vh.from.setText(R.string.from_wechat);
			break;
		}

		if (item.getCommentCount() > 0) {
			vh.commentCount.setText(String.valueOf(item.getCommentCount()));
			vh.commentCount.setVisibility(View.VISIBLE);
		} else {
			vh.commentCount.setVisibility(View.GONE);
		}
//		if (item.getActiveType() == Active.CATALOG_OTHER) {
//			vh.retweetCount.setVisibility(View.VISIBLE);
//		} else {
//			vh.retweetCount.setVisibility(View.GONE);
//		}
		
		vh.avatar.setUserInfo(item.getAuthorId(), item.getAuthor());
		vh.avatar.setAvatarUrl(item.getPortrait());
		
		if (!TextUtils.isEmpty(item.getTweetimage())) {
			vh.pic.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(item.getTweetimage(), vh.pic,
					options);
			vh.pic.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ImagePreviewActivity.showImagePrivew(parent.getContext(), 0, new String[]{getOriginalUrl(item.getTweetimage())});
				}
			});
		} else {
			vh.pic.setVisibility(View.GONE);
			vh.pic.setImageBitmap(null);
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
	
	private String getOriginalUrl(String url) {
		return url.replaceAll("_thumb", "");
	}
	
	static class ViewHolder {
		@InjectView(R.id.tv_name)
		TextView name;
		@InjectView(R.id.tv_from)
		TextView from;
		@InjectView(R.id.tv_time)
		TextView time;
		@InjectView(R.id.tv_action)
		TextView action;
		@InjectView(R.id.tv_action_name)
		TextView actionName;
		@InjectView(R.id.tv_comment_count)
		TextView commentCount;
//		@InjectView(R.id.tv_reply_content)
//		TextView retweetCount;
		@InjectView(R.id.tv_body)
		TweetTextView body;
		@InjectView(R.id.tv_reply)
		TweetTextView reply;
		@InjectView(R.id.iv_pic)
		ImageView pic;
		@InjectView(R.id.ly_reply)
		View lyReply;
		@InjectView(R.id.iv_avatar)
		AvatarView avatar;

		public ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
