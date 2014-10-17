package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseDetailFragment;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.BlogDetail;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.fragment.ToolbarFragment.OnActionClickListener;
import net.oschina.app.fragment.ToolbarFragment.ToolAction;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.interf.ToolbarEmojiVisiableControl;
import net.oschina.app.interf.ToolbarFragmentControl;
import net.oschina.app.service.PublicCommentTask;
import net.oschina.app.service.ServerTaskUtils;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

public class BlogDetailFragment extends BaseDetailFragment implements
		EmojiTextListener, EmojiFragmentControl, ToolbarFragmentControl {

	protected static final String TAG = BlogDetailFragment.class
			.getSimpleName();
	private static final String BLOG_CACHE_KEY = "blog_";
	private static final String BLOG_DETAIL_SCREEN = "blog_detail_screen";
	@InjectView(R.id.tv_title) TextView mTvTitle;
	@InjectView(R.id.tv_source) TextView mTvSource;
	@InjectView(R.id.tv_time) TextView mTvTime;
	@InjectView(R.id.tv_comment_count) TextView mTvCommentCount;
	private WebView mWebView;
	private int mBlogId;
	private Blog mBlog;
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
				if (mBlog != null)
					UIHelper.showBlogComment(getActivity(), mBlogId,
							mBlog.getAuthorId());
				break;
			case ACTION_FAVORITE:
				handleFavoriteOrNot();
				break;
			case ACTION_SHARE:
				handleShare();
				break;
			case ACTION_REPORT:
				onReportMenuClick();
				break;
			default:
				break;
			}
		}
	};

	private AsyncHttpResponseHandler mReportHandler = new TextHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, String arg2) {
//			if (TextUtils.isEmpty(arg2)) {
//				AppContext.showToastShort(R.string.tip_report_success);
//			} else {
//				AppContext.showToastShort(R.string.tip_report_faile);
//			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, String arg2,
				Throwable arg3) {
//			AppContext.showToastShort(R.string.tip_report_faile);
		}

		@Override
		public void onFinish() {
//			hideWaitDialog();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_news_detail,
				container, false);

		mBlogId = getActivity().getIntent().getIntExtra("blog_id", 0);
		ButterKnife.inject(this, view);
		initViews(view);

		return view;
	}

	private void initViews(View view) {
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);

		mWebView = (WebView) view.findViewById(R.id.webview);
		initWebView(mWebView);
	}

	@Override
	protected boolean hasReportMenu() {
		return true;
	}

	@Override
	protected String getCacheKey() {
		return new StringBuilder(BLOG_CACHE_KEY).append(mBlogId).toString();
	}

	@Override
	protected void sendRequestData() {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		OSChinaApi.getBlogDetail(mBlogId, mHandler);
	}

	@Override
	protected Entity parseData(InputStream is) throws Exception {
		return XmlUtils.toBean(BlogDetail.class, is).getBlog();
	}

	@Override
	protected Entity readData(Serializable seri) {
		return (Blog) seri;
	}

//	@Override
//	protected void onCommentChanged(int opt, int id, int catalog,
//			boolean isBlog, Comment comment) {
//		if (id == mBlogId && isBlog) {
//			if (Comment.OPT_ADD == opt && mBlog != null) {
//				mBlog.setCommentCount(mBlog.getCommentCount() + 1);
//				// if (mTvCommentCount != null) {
//				// mTvCommentCount.setVisibility(View.VISIBLE);
//				// mTvCommentCount.setText(getString(R.string.comment_count,
//				// mBlog.getCommentCount()));
//				// }
//				if (mToolBarFragment != null) {
//					mToolBarFragment.setCommentCount(mBlog.getCommentCount());
//				}
//			}
//		}
//	}

	@Override
	protected void executeOnLoadDataSuccess(Entity entity) {
		mBlog = (Blog) entity;
		fillUI();
		fillWebViewBody();
	}

	private void fillUI() {
		mTvTitle.setText(mBlog.getTitle());
		mTvSource.setText(mBlog.getAuthor());
		mTvTime.setText(StringUtils.friendly_time(mBlog.getPubDate()));
		mTvCommentCount.setText(mBlog.getCommentCount() + "评");
		if (mToolBarFragment != null) {
			mToolBarFragment.setCommentCount(mBlog.getCommentCount());
		}
		notifyFavorite(mBlog.getFavorite() == 1);
	}

	private void fillWebViewBody() {
		String body = UIHelper.WEB_STYLE + mBlog.getBody();
		body = UIHelper.setHtmlCotentSupportImagePreview(body);
		body += UIHelper.WEB_LOAD_IMAGES;
		mWebView.setWebViewClient(mWebClient);
		mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
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
		mToolBarFragment.setActionVisiable(ToolAction.ACTION_REPORT, true);
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
		task.setId(mBlogId);
		task.setContent(text);
		task.setUid(AppContext.getInstance().getLoginUid());
		ServerTaskUtils.publicBlogComment(getActivity(), task);
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
		return mBlog != null ? mBlog.getId() : -1;
	}

	@Override
	protected int getFavoriteTargetType() {
		return mBlog != null ? FavoriteList.TYPE_BLOG : -1;
	}

	@Override
	protected String getShareContent() {
		return mBlog != null ? mBlog.getTitle() : null;
	}

	@Override
	protected String getShareUrl() {
		return mBlog != null ? mBlog.getUrl() : null;
	}

	@Override
	protected void onReportMenuClick() {
//		if (!AppContext.getInstance().isLogin()) {
//			UIHelper.showLoginActivity(getActivity());
//			return;
//		}
//		if (mBlog == null)
//			return;
//		int reportId = AppContext.getInstance().getLoginUid();
//		final ReportDialog dialog = new ReportDialog(getActivity(),
//				mBlog.getUrl(), reportId);
//		dialog.setCancelable(true);
//		dialog.setTitle(R.string.report);
//		dialog.setCanceledOnTouchOutside(true);
//		dialog.setNegativeButton(R.string.cancle, null);
//		dialog.setPositiveButton(R.string.ok,
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface d, int which) {
//						Report report = null;
//						if ((report = dialog.getReport()) != null) {
//							showWaitDialog(R.string.progress_submit);
//							NewsApi.report(report, mReportHandler);
//						}
//						d.dismiss();
//					}
//				});
//		dialog.show();
	}
}
