package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetDetail;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.interf.ToolbarEmojiVisiableControl;
import net.oschina.app.service.PublicCommentTask;
import net.oschina.app.service.ServerTaskUtils;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.LinkView;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class TweetDetailFragment extends BaseDetailFragment implements
		EmojiTextListener, EmojiFragmentControl {

	private static final String TWEET_CACHE_KEY = "tweet_";
	protected static final String TAG = TweetDetailFragment.class.getSimpleName();
	private static final String TWEET_DETAIL_SCREEN = "tweet_detail_screen";
	
	@InjectView(R.id.iv_tweet_detail_face)
	public AvatarView face;
	@InjectView(R.id.tv_tweet_detail_name)
	TextView author;
	@InjectView(R.id.tv_tweet_detail_time)
	TextView time;
	@InjectView(R.id.tweet_detail_item)
	LinkView content;
	
	private int mTweetId;
	private Tweet mTweet;
	private EmojiFragment mEmojiFragment;

	private OnClickListener mMoreListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Activity act = getActivity();
			if (act != null && act instanceof ToolbarEmojiVisiableControl) {
				((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tweet_detail, container, false);

		mTweetId = getActivity().getIntent().getIntExtra("tweet_id", 0);
		ButterKnife.inject(this, view);
		initViews(view);
		return view;
	}

	private void initViews(View view) {
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);

	}

	@Override
	protected String getCacheKey() {
		return new StringBuilder(TWEET_CACHE_KEY).append(mTweetId).toString();
	}

	@Override
	protected void sendRequestData() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		OSChinaApi.getTweetDetail(mTweetId, mHandler);
	}

	@Override
	protected Entity parseData(InputStream is) throws Exception {
		return XmlUtils.toBean(TweetDetail.class, is).getTweet();
	}

	@Override
	protected Entity readData(Serializable seri) {
		return (Tweet) seri;
	}

	@Override
	protected void executeOnLoadDataSuccess(Entity entity) {
		mTweet = (Tweet) entity;
		fillUI();
	}

	private void fillUI() {
		face.setUserInfo(mTweet.getAuthorid(), mTweet.getAuthor());
		face.setAvatarUrl(mTweet.getPortrait());
		author.setText(mTweet.getAuthor());
		time.setText(StringUtils.friendly_time(mTweet.getPubDate()));
		content.setLinkText(mTweet.getBody());
	}

	@Override
	public void setEmojiFragment(EmojiFragment fragment) {
		mEmojiFragment = fragment;
		mEmojiFragment.setEmojiTextListener(this);
		mEmojiFragment.setButtonMoreVisibility(View.VISIBLE);
		mEmojiFragment.setButtonMoreClickListener(mMoreListener);
	}

	@Override
	public void onSendClick(String text) {
		if (!TDevice.hasInternet()) {
			AppContext.showToastShort(R.string.tip_network_error);
			return;
		}
		if (!AppContext.getInstance().isLogin()) {
			UIHelper.showLoginActivity(getActivity());
			return;
		}
		if (TextUtils.isEmpty(text)) {
			AppContext.showToastShort(R.string.tip_comment_content_empty);
			mEmojiFragment.requestFocusInput();
			return;
		}
		PublicCommentTask task = new PublicCommentTask();
		task.setId(mTweetId);
		task.setCatalog(CommentList.CATALOG_POST);
		task.setIsPostToMyZone(0);
		task.setContent(text);
		task.setUid(AppContext.getInstance().getLoginUid());
		ServerTaskUtils.publicNewsComment(getActivity(), task);
		mEmojiFragment.reset();
	}
}
