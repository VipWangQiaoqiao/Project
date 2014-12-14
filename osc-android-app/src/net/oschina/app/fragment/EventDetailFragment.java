package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostDetail;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.fragment.ToolbarFragment.OnActionClickListener;
import net.oschina.app.fragment.ToolbarFragment.ToolAction;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.interf.ToolbarEmojiVisiableControl;
import net.oschina.app.interf.ToolbarFragmentControl;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 活动详情页面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年12月12日 下午3:08:49
 *
 */
public class EventDetailFragment extends BaseDetailFragment implements
		EmojiTextListener, EmojiFragmentControl, ToolbarFragmentControl {

	protected static final String TAG = EventDetailFragment.class
			.getSimpleName();
	private static final String POST_CACHE_KEY = "post_";
	
	@InjectView(R.id.tv_event_title) TextView mTvTitle;
	
	@InjectView(R.id.tv_event_start_time) TextView mTvStartTime;
	
	@InjectView(R.id.tv_event_end_time) TextView mTvEndTime;
	
	@InjectView(R.id.tv_event_spot) TextView mTvSpot;
	
	@InjectView(R.id.webview) WebView mWebView;
	
	@InjectView(R.id.rl_event_location) View mLocation;
	
	@InjectView(R.id.rl_event_attend) View mAttend;
	
	private int mPostId;
	private Post mPost;
	private EmojiFragment mEmojiFragment;
	private ToolbarFragment mToolBarFragment;

	private OnClickListener mMoreListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Activity act = getActivity();
			if (act != null && act instanceof ToolbarEmojiVisiableControl) {
				((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
			}
		}
	};

	private OnActionClickListener mActionListener = new OnActionClickListener() {

		@Override
		public void onActionClick(ToolAction action) {
			switch (action) {
			case ACTION_CHANGE:
				Activity act = getActivity();
				if (act != null && act instanceof ToolbarEmojiVisiableControl) {
					((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
				}
				break;
			case ACTION_WRITE_COMMENT:
				act = getActivity();
				if (act != null && act instanceof ToolbarEmojiVisiableControl) {
					((ToolbarEmojiVisiableControl) act).toggleToolbarEmoji();
				}
				mEmojiFragment.showKeyboardIfNoEmojiGrid();
				break;
			case ACTION_VIEW_COMMENT:
				UIHelper.showComment(getActivity(), mPostId,
						CommentList.CATALOG_POST);
				break;
			case ACTION_FAVORITE:
				handleFavoriteOrNot();
				break;
			case ACTION_SHARE:
				handleShare();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_event_detail, container,
				false);

		mPostId = getActivity().getIntent().getIntExtra("post_id", 0);
		ButterKnife.inject(this, view);
		initViews(view);

		return view;
	}

	private void initViews(View view) {
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		
		mLocation.setOnClickListener(this);
		mAttend.setOnClickListener(this);
		
		UIHelper.initWebView(mWebView);
	}
	
	

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		switch (id) {
		case R.id.rl_event_location:
			UIHelper.showEventLocation(getActivity());
			break;
		case R.id.rl_event_attend:
			showEventApplies();
			break;
		default:
			break;
		}
	}
	
	private void showEventApplies() {
		Bundle args = new Bundle();
		args.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, mPost.getEvent().getId());
		UIHelper.showSimpleBack(getActivity(), SimpleBackPage.EVENT_APPLY, args);
	}

	@Override
	protected String getCacheKey() {
		return new StringBuilder(POST_CACHE_KEY).append(mPostId).toString();
	}

	@Override
	protected void sendRequestData() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		OSChinaApi.getPostDetail(mPostId, mHandler);
	}

	@Override
	protected Entity parseData(InputStream is) throws Exception {
		return XmlUtils.toBean(PostDetail.class, is).getPost();
	}

	@Override
	protected Entity readData(Serializable seri) {
		return (Post) seri;
	}

	@Override
	protected void executeOnLoadDataSuccess(Entity entity) {
		mPost = (Post) entity;
		fillUI();
		fillWebViewBody();
	}

	private void fillUI() {
		
		mTvTitle.setText(mPost.getTitle());
		mTvStartTime.setText(String.format(getString(R.string.event_start_time), mPost.getEvent().getStartTime()));
		mTvEndTime.setText(String.format(getString(R.string.event_end_time), mPost.getEvent().getEndTime()));
		mTvSpot.setText(mPost.getEvent().getCity() + " " + mPost.getEvent().getSpot());
		if (mToolBarFragment != null) {
			mToolBarFragment.setCommentCount(mPost.getAnswerCount());
		}
		notifyFavorite(mPost.getFavorite() == 1);
	}

	private void fillWebViewBody() {
		// 显示标签
		StringBuffer body = new StringBuffer();
		body.append(UIHelper.setHtmlCotentSupportImagePreview(mPost.getBody()));
		body.append(UIHelper.WEB_STYLE)
			.append(UIHelper.WEB_LOAD_IMAGES)
			.append(getPostTags(mPost.getTags()))
			.append("<div style=\"margin-bottom: 80px\" />");
		mWebView.loadDataWithBaseURL(null, body.toString(), "text/html", "utf-8", null);
	}

	@SuppressWarnings("deprecation")
	private String getPostTags(Post.Tags taglist) {
		if (taglist == null)
			return "";
		StringBuffer tags = new StringBuffer();
		for (String tag : taglist.getTags()) {
			tags.append(String
					.format("<a class='tag' href='http://www.oschina.net/question/tag/%s' >&nbsp;%s&nbsp;</a>&nbsp;&nbsp;",
							URLEncoder.encode(tag), tag));
		}
		return String.format("<div style='margin-top:10px;'>%s</div>", tags);
	}

	@Override
	public void setEmojiFragment(EmojiFragment fragment) {
		mEmojiFragment = fragment;
		mEmojiFragment.setEmojiTextListener(this);
		mEmojiFragment.setButtonMoreVisibility(View.VISIBLE);
		mEmojiFragment.setButtonMoreClickListener(mMoreListener);
	}

	@Override
	public void setToolBarFragment(ToolbarFragment fragment) {
		mToolBarFragment = fragment;
		mToolBarFragment.setOnActionClickListener(mActionListener);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_CHANGE, true);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_FAVORITE, true);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_WRITE_COMMENT,
				true);
		mToolBarFragment
				.setActionVisiable(ToolAction.ACTION_VIEW_COMMENT, true);
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_SHARE, true);
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
		showWaitDialog(R.string.progress_submit);
		OSChinaApi.publicComment(CommentList.CATALOG_POST, mPostId, AppContext
				.getInstance().getLoginUid(), text, 0, mCommentHandler);
	}

	@Override
	protected void commentPubSuccess(Comment comment) {
		super.commentPubSuccess(comment);
		mEmojiFragment.reset();
	}

	@Override
	protected void onFavoriteChanged(boolean flag) {
		super.onFavoriteChanged(flag);
		if (mToolBarFragment != null) {
			mToolBarFragment.setFavorite(flag);
		}
	}

	@Override
	protected int getFavoriteTargetId() {
		return mPost != null ? mPost.getId() : -1;
	}

	@Override
	protected int getFavoriteTargetType() {
		return mPost != null ? FavoriteList.TYPE_POST : -1;
	}

	@Override
	protected String getShareTitle() {
		return getString(R.string.share_title_post);
	}

	@Override
	protected String getShareContent() {
		return mPost != null ? mPost.getTitle() : null;
	}

	@Override
	protected String getShareUrl() {
		return mPost != null ? mPost.getUrl().replace("http://www", "http://m")
				: null;
	}
}