package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLEncoder;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;

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
import net.oschina.app.bean.Event;
import net.oschina.app.bean.EventApplyData;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostDetail;
import net.oschina.app.bean.Report;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.fragment.ToolbarFragment.OnActionClickListener;
import net.oschina.app.fragment.ToolbarFragment.ToolAction;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.interf.ToolbarEmojiVisiableControl;
import net.oschina.app.interf.ToolbarFragmentControl;
import net.oschina.app.ui.EventApplyDialog;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
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

	@InjectView(R.id.tv_event_title)
	TextView mTvTitle;

	@InjectView(R.id.tv_event_start_time)
	TextView mTvStartTime;

	@InjectView(R.id.tv_event_end_time)
	TextView mTvEndTime;

	@InjectView(R.id.tv_event_spot)
	TextView mTvSpot;

	@InjectView(R.id.webview)
	WebView mWebView;

	@InjectView(R.id.rl_event_location)
	View mLocation;

	@InjectView(R.id.bt_event_attend)
	Button mBtAttend;// 出席人员

	@InjectView(R.id.bt_event_apply)
	Button mBtEventApply;// 活动报名

	private int mPostId;
	private Post mPost;
	private EmojiFragment mEmojiFragment;
	private ToolbarFragment mToolBarFragment;
	
	private EventApplyDialog mEventApplyDialog;

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
		mBtAttend.setOnClickListener(this);
		mBtEventApply.setOnClickListener(this);
		UIHelper.initWebView(mWebView);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		switch (id) {
		case R.id.rl_event_location:
			UIHelper.showEventLocation(getActivity(), mPost.getEvent()
					.getCity(), mPost.getEvent().getSpot());
			break;
		case R.id.bt_event_attend:
			showEventApplies();
			break;
		case R.id.bt_event_apply:
			showEventApply();
			break;
		default:
			break;
		}
	}

	private void showEventApplies() {
		Bundle args = new Bundle();
		args.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, mPost.getEvent()
				.getId());
		UIHelper.showSimpleBack(getActivity(), SimpleBackPage.EVENT_APPLY, args);
	}

	private AsyncHttpResponseHandler mApplyHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			Result rs = XmlUtils.toBean(ResultBean.class,
					new ByteArrayInputStream(arg2)).getResult();
			if (rs.OK()) {
				AppContext.showToast("报名成功");
				mEventApplyDialog.dismiss();
				mPost.getEvent().setApplyStatus(Event.APPLYSTATUS_CHECKING);
				notifyEventStatus();
			} else {
				AppContext.showToast(rs.getErrorMessage());
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			AppContext.showToast("报名失败");
		}

		@Override
		public void onFinish() {
			hideWaitDialog();
		}
	};

	/**
	 * 显示活动报名对话框
	 */
	private void showEventApply() {
		if (!AppContext.getInstance().isLogin()) {
			UIHelper.showLoginActivity(getActivity());
			return;
		}
		if (mEventApplyDialog == null) {
			mEventApplyDialog = new EventApplyDialog(getActivity());
			mEventApplyDialog.setCanceledOnTouchOutside(true);
			mEventApplyDialog.setCancelable(true);
			mEventApplyDialog.setTitle("活动报名");
			mEventApplyDialog.setCanceledOnTouchOutside(true);
			mEventApplyDialog.setNegativeButton(R.string.cancle, null);
			mEventApplyDialog.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface d, int which) {
							EventApplyData data = null;
							if ((data = mEventApplyDialog.getApplyData()) != null) {
								data.setEvent(mPostId);
								data.setUser(AppContext.getInstance().getLoginUid());
								showWaitDialog(R.string.progress_submit);
								OSChinaApi.eventApply(data, mApplyHandler);
							}
						}
					});
		}
		
		mEventApplyDialog.show();
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
		mTvStartTime.setText(String.format(
				getString(R.string.event_start_time), mPost.getEvent()
						.getStartTime()));
		mTvEndTime.setText(String.format(getString(R.string.event_end_time),
				mPost.getEvent().getEndTime()));
		mTvSpot.setText(mPost.getEvent().getCity() + " "
				+ mPost.getEvent().getSpot());
		if (mToolBarFragment != null) {
			mToolBarFragment.setCommentCount(mPost.getAnswerCount());
		}
		notifyFavorite(mPost.getFavorite() == 1);
		notifyEventStatus();
	}
	
	// 显示活动 以及报名的状态
	private void notifyEventStatus() {
		int eventStatus = mPost.getEvent().getStatus();
		int applyStatus = mPost.getEvent().getApplyStatus();

		if (applyStatus == Event.APPLYSTATUS_ATTEND) {
			mBtAttend.setVisibility(View.VISIBLE);
		} else {
			mBtAttend.setVisibility(View.GONE);
		}

		if (eventStatus == Event.EVNET_STATUS_APPLYING) {
			mBtEventApply.setVisibility(View.VISIBLE);
			mBtEventApply.setEnabled(false);
			switch (applyStatus) {
			case Event.APPLYSTATUS_CHECKING:
				mBtEventApply.setText("待确认");
				break;
			case Event.APPLYSTATUS_CHECKED:
				mBtEventApply.setText("已确认");
				break;
			case Event.APPLYSTATUS_ATTEND:
				mBtEventApply.setText("已出席");
				break;
			case Event.APPLYSTATUS_CANCLE:
				mBtEventApply.setText("已取消");
				break;
			case Event.APPLYSTATUS_REJECT:
				mBtEventApply.setText("已拒绝");
				break;
			default:
				mBtEventApply.setText("我要报名");
				mBtEventApply.setEnabled(true);
				break;
			}
		} else {
			mBtEventApply.setVisibility(View.GONE);
		}
	}

	private void fillWebViewBody() {
		// 显示标签
		StringBuffer body = new StringBuffer();
		body.append(UIHelper.setHtmlCotentSupportImagePreview(mPost.getBody()));
		body.append(UIHelper.WEB_STYLE).append(UIHelper.WEB_LOAD_IMAGES)
				.append(getPostTags(mPost.getTags()))
				.append("<div style=\"margin-bottom: 80px\" />");
		mWebView.loadDataWithBaseURL(null, body.toString(), "text/html",
				"utf-8", null);
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