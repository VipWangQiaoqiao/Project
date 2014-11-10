package net.oschina.app.adapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import net.oschina.app.R;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.Comment.Refer;
import net.oschina.app.bean.Comment.Reply;
import net.oschina.app.bean.Tweet;
import net.oschina.app.util.StringUtils;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;
import android.annotation.SuppressLint;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentAdapter extends ListBaseAdapter {

	private boolean showSplit;

	public interface OnOperationListener {
		void onMoreClick(Comment comment);
	}

	private OnOperationListener mListener;

	public CommentAdapter(OnOperationListener lis) {
		mListener = lis;
	}

	public CommentAdapter(OnOperationListener lis, boolean showSplit) {
		this.showSplit = showSplit;
		mListener = lis;
	}

	@SuppressLint({ "InflateParams", "CutPasteId" })
	@Override
	protected View getRealView(int position, View convertView,
			final ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_comment, null);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final Comment item = (Comment) _data.get(position);

		vh.name.setText(item.getAuthor());

		vh.content.setMovementMethod(MyLinkMovementMethod.a());
		vh.content.setFocusable(false);
		vh.content.setDispatchToParent(true);
		vh.content.setLongClickable(false);
		Spanned span = Html.fromHtml(item.getContent());
		vh.content.setText(span);
		MyURLSpan.parseLinkText(vh.content, span);

		vh.time.setText(StringUtils.friendly_time(item.getPubDate()));

		vh.from.setVisibility(View.VISIBLE);
		switch (item.getAppClient()) {
		default:
			vh.from.setText("");
			vh.from.setVisibility(View.GONE);
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

		// setup refers
		List<Refer> refers = item.getRefers();
		vh.refers.removeAllViews();
		if (refers == null || refers.size() <= 0) {
			vh.refers.setVisibility(View.GONE);
		} else {
			vh.refers.setVisibility(View.VISIBLE);

			// add refer item
			for (Refer reply : refers) {
				View replyItemView = getLayoutInflater(parent.getContext())
						.inflate(R.layout.list_cell_reply_name_content, null);

				View countView = getLayoutInflater(parent.getContext())
						.inflate(R.layout.list_cell_reply_count, null);
				TextView name = (TextView) countView
						.findViewById(R.id.tv_comment_reply_count);

				name.setText(reply.refertitle);
				vh.refers.addView(name);

				TweetTextView refersContent = (TweetTextView) replyItemView
						.findViewById(R.id.tv_reply_content);
				refersContent.setMovementMethod(MyLinkMovementMethod.a());
				refersContent.setFocusable(false);
				refersContent.setDispatchToParent(true);
				refersContent.setLongClickable(false);
				Spanned rcontent = Html.fromHtml(reply.referbody);
				refersContent.setText(rcontent);
				MyURLSpan.parseLinkText(refersContent, rcontent);

				vh.refers.addView(replyItemView);
			}
		}

		// setup replies
		List<Reply> replies = item.getReplies();
		vh.relies.removeAllViews();
		if (replies == null || replies.size() <= 0) {
			vh.relies.setVisibility(View.GONE);
		} else {
			vh.relies.setVisibility(View.VISIBLE);

			// add count layout
			View countView = getLayoutInflater(parent.getContext()).inflate(
					R.layout.list_cell_reply_count, null);
			TextView count = (TextView) countView
					.findViewById(R.id.tv_comment_reply_count);
			count.setText(parent.getContext().getResources()
					.getString(R.string.comment_reply_count, replies.size()));
			vh.relies.addView(countView);

			// add reply item
			for (Reply reply : replies) {
				View replyItemView = getLayoutInflater(parent.getContext())
						.inflate(R.layout.list_cell_reply_name_content, null);

				TextView name = (TextView) replyItemView
						.findViewById(R.id.tv_reply_name);
				name.setText(reply.rauthor + ":");

				TweetTextView replyContent = (TweetTextView) replyItemView
						.findViewById(R.id.tv_reply_content);
				replyContent.setMovementMethod(MyLinkMovementMethod.a());
				replyContent.setFocusable(false);
				replyContent.setDispatchToParent(true);
				replyContent.setLongClickable(false);
				Spanned rcontent = Html.fromHtml(reply.rcontent);
				replyContent.setText(rcontent);
				MyURLSpan.parseLinkText(replyContent, rcontent);

				vh.relies.addView(replyItemView);
			}
		}

		vh.avatar.setAvatarUrl(item.getPortrait());
		vh.avatar.setUserInfo(item.getAuthorId(), item.getAuthor());

		return convertView;
	}

	static class ViewHolder {
		@InjectView(R.id.iv_avatar)
		AvatarView avatar;
		@InjectView(R.id.tv_name)
		TextView name;
		@InjectView(R.id.tv_time)
		TextView time;
		@InjectView(R.id.tv_from)
		TextView from;
		@InjectView(R.id.tv_content)
		TweetTextView content;
		@InjectView(R.id.ly_relies)
		LinearLayout relies;
		@InjectView(R.id.ly_refers)
		LinearLayout refers;

		ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
