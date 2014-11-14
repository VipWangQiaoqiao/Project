package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.CommentAdapter;
import net.oschina.app.adapter.CommentAdapter.OnOperationListener;
import net.oschina.app.api.OperationResponseHandler;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetDetail;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.interf.OnWebViewImageListener;
import net.oschina.app.service.PublicCommentTask;
import net.oschina.app.service.ServerTaskUtils;
import net.oschina.app.ui.dialog.CommonDialog;
import net.oschina.app.ui.dialog.DialogHelper;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.HTMLSpirit;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.AvatarView;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ZoomButtonsController;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class TweetDetailFragment extends BaseFragment implements
		EmojiTextListener, EmojiFragmentControl, OnOperationListener,
		OnItemClickListener, OnItemLongClickListener {
	protected static final String TAG = TweetDetailFragment.class
			.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "tweet_";
	private static final String CACHE_KEY_TWEET_COMMENT = "tweet_comment_";
	private ListView mListView;
	private EmptyLayout mEmptyView;
	private AvatarView mIvAvatar;
	private TextView mTvName, mTvFrom, mTvTime, mTvCommentCount;
	private WebView mContent;
	private int mTweetId;
	private Tweet mTweet;
	private int mCurrentPage = 0;
	private CommentAdapter mAdapter;
	private EmojiFragment mEmojiFragment;
	private BroadcastReceiver mCommentReceiver;

	private AsyncHttpResponseHandler mCommentHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				ResultBean rsb = XmlUtils.toBean(ResultBean.class,
						new ByteArrayInputStream(arg2));
				Result res = rsb.getResult();
				if (res.OK()) {
					hideWaitDialog();
					AppContext.showToastShort(R.string.comment_publish_success);
					mAdapter.addItem(0, rsb.getComment());
					mEmojiFragment.reset();
					setTweetCommentCount(1);
					// UIHelper.sendBroadCastCommentChanged(getActivity(),
					// mIsBlogComment, mId, mCatalog, Comment.OPT_ADD,
					// res.getComment());
				} else {
					hideWaitDialog();
					AppContext.showToastShort(res.getErrorMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			hideWaitDialog();
			AppContext.showToastShort(R.string.comment_publish_faile);
		}
	};

	class CommentChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int opt = intent.getIntExtra(Comment.BUNDLE_KEY_OPERATION, 0);
			int id = intent.getIntExtra(Comment.BUNDLE_KEY_ID, 0);
			int catalog = intent.getIntExtra(Comment.BUNDLE_KEY_CATALOG, 0);
			boolean isBlog = intent.getBooleanExtra(Comment.BUNDLE_KEY_BLOG,
					false);
			Comment comment = intent
					.getParcelableExtra(Comment.BUNDLE_KEY_COMMENT);
			onCommentChanged(opt, id, catalog, isBlog, comment);
		}
	}

	private OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (mAdapter != null
					&& mAdapter.getDataSize() > 0
					&& mListView.getLastVisiblePosition() == (mListView
							.getCount() - 1)) {
				if (mState == STATE_NONE
						&& mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE) {
					mState = STATE_LOADMORE;
					mCurrentPage++;
					requestTweetCommentData(true);
				}
			}
		}
	};

	private void onCommentChanged(int opt, int id, int catalog, boolean isBlog,
			Comment comment) {
		if (Comment.OPT_ADD == opt && catalog == CommentList.CATALOG_TWEET
				&& id == mTweetId) {
			if (mTweet != null && mTvCommentCount != null) {
				mTweet.setCommentCount(mTweet.getCommentCount() + 1);

				mAdapter.addItem(0, comment);
				mTvCommentCount.setText(getString(R.string.comment_count,
						mTweet.getCommentCount()));
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// IntentFilter filter = new IntentFilter(
		// Constants.INTENT_ACTION_COMMENT_CHANGED);
		// mCommentReceiver = new CommentChangeReceiver();
		// getActivity().registerReceiver(mCommentReceiver, filter);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		if (mCommentReceiver != null) {
			getActivity().unregisterReceiver(mCommentReceiver);
		}
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tweet_detail, container,
				false);
		mTweetId = getActivity().getIntent().getIntExtra("tweet_id", 0);

		initViews(view);

		requestTweetData(true);
		return view;
	}

	@SuppressLint("InflateParams")
	private void initViews(View view) {
		mEmptyView = (EmptyLayout) view.findViewById(R.id.error_layout);
		mListView = (ListView) view.findViewById(R.id.tweet_detail_listview);
		mListView.setOnScrollListener(mScrollListener);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		View header = LayoutInflater.from(getActivity()).inflate(
				R.layout.list_header_tweet_detail, null);
		mIvAvatar = (AvatarView) header.findViewById(R.id.iv_avatar);

		mTvName = (TextView) header.findViewById(R.id.tv_name);
		mTvFrom = (TextView) header.findViewById(R.id.tv_from);
		mTvTime = (TextView) header.findViewById(R.id.tv_time);
		mTvCommentCount = (TextView) header.findViewById(R.id.tv_comment_count);
		mContent = (WebView) header.findViewById(R.id.webview);
		UIHelper.initWebView(mContent);

		mListView.addHeaderView(header);
		mAdapter = new CommentAdapter(this, true);
		mListView.setAdapter(mAdapter);
	}

	private void fillUI() {
		mIvAvatar.setAvatarUrl(mTweet.getPortrait());
		mIvAvatar.setUserInfo(mTweet.getAuthorid(), mTweet.getAuthor());
		mTvName.setText(mTweet.getAuthor());
		mTvTime.setText(StringUtils.friendly_time(mTweet.getPubDate()));
		switch (mTweet.getAppclient()) {
		default:
			mTvFrom.setVisibility(View.GONE);
			break;
		case Tweet.CLIENT_MOBILE:
			mTvFrom.setText(R.string.from_mobile);
			break;
		case Tweet.CLIENT_ANDROID:
			mTvFrom.setText(R.string.from_android);
			break;
		case Tweet.CLIENT_IPHONE:
			mTvFrom.setText(R.string.from_iphone);
			break;
		case Tweet.CLIENT_WINDOWS_PHONE:
			mTvFrom.setText(R.string.from_windows_phone);
			break;
		case Tweet.CLIENT_WECHAT:
			mTvFrom.setText(R.string.from_wechat);
			break;
		}

		mTvCommentCount.setText(getString(R.string.comment_count,
				mTweet.getCommentCount()));

		fillWebViewBody();
	}
	
	/**
	 * 填充webview内容
	 */
	private void fillWebViewBody() {
		StringBuffer body = new StringBuffer();
		body.append(UIHelper.WEB_STYLE + UIHelper.WEB_LOAD_IMAGES);
		String tweetBody = TextUtils.isEmpty(mTweet.getImgSmall()) ? mTweet
				.getBody() : mTweet.getBody() + "<br/><img src=\""
				+ mTweet.getImgSmall() + "\">";
		body.append(setHtmlCotentSupportImagePreview(tweetBody));
		UIHelper.addWebImageShow(getActivity(), mContent);
		mContent.loadDataWithBaseURL(null, body.toString(), "text/html", "utf-8", null);
	}
	
	/**
	 * 添加图片放大支持
	 * @param body
	 * @return
	 */
	private String setHtmlCotentSupportImagePreview(String body) {
		// 过滤掉 img标签的width,height属性
		body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
		body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
		return body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
				"$1$2\" onClick=\"javascript:mWebViewImageListener.showImagePreview('"
						+ mTweet.getImgBig() + "')\"");
	}

	private void sendRequestData() {
		mState = STATE_REFRESH;
		mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
		OSChinaApi.getTweetDetail(mTweetId, mDetailHandler);
	}

	private void sendRequestCommentData() {
		OSChinaApi.getCommentList(mTweetId, CommentList.CATALOG_TWEET,
				mCurrentPage, mCommentListHandler);
	}

	@Override
	public void setEmojiFragment(EmojiFragment fragment) {
		mEmojiFragment = fragment;
		mEmojiFragment.setEmojiTextListener(this);
	}

	@Override
	public void onSendClick(String text) {
		if (!TDevice.hasInternet()) {
			AppContext.showToastShort(R.string.tip_network_error);
			return;
		}
		if (!AppContext.getInstance().isLogin()) {
			UIHelper.showLoginActivity(getActivity());
			mEmojiFragment.hideKeyboard();
			return;
		}
		if (TextUtils.isEmpty(text)) {
			AppContext.showToastShort(R.string.tip_comment_content_empty);
			mEmojiFragment.requestFocusInput();
			return;
		}

		handleComment(text);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Comment comment = (Comment) mAdapter.getItem(position - 1);
		if (comment == null)
			return;
		mEmojiFragment.setTag(comment);
		mEmojiFragment.setInputHint("回复" + comment.getAuthor() + ":");
		mEmojiFragment.requestFocusInput();
	}

	@Override
	public void onMoreClick(final Comment comment) {
	}

	private void handleComment(String text) {
		showWaitDialog(R.string.progress_submit);
		if (!AppContext.getInstance().isLogin()) {
			UIHelper.showLoginActivity(getActivity());
			return;
		}
		if (mEmojiFragment.getInputTag() != null) {
			Comment comment = (Comment) mEmojiFragment.getInputTag();
			OSChinaApi
					.replyComment(mTweetId, CommentList.CATALOG_TWEET, comment
							.getId(), comment.getAuthorId(), AppContext
							.getInstance().getLoginUid(), text, mCommentHandler);
		} else {
			OSChinaApi.publicComment(CommentList.CATALOG_TWEET, mTweetId,
					AppContext.getInstance().getLoginUid(), text, 0,
					mCommentHandler);
		}

	}

	private void handleDeleteComment(Comment comment) {
		if (!AppContext.getInstance().isLogin()) {
			UIHelper.showLoginActivity(getActivity());
			return;
		}
		AppContext.showToastShort(R.string.deleting);
		OSChinaApi.deleteComment(mTweetId, CommentList.CATALOG_TWEET,
				comment.getId(), comment.getAuthorId(),
				new DeleteOperationResponseHandler(comment));
	}

	class DeleteOperationResponseHandler extends OperationResponseHandler {

		DeleteOperationResponseHandler(Object... args) {
			super(args);
		}

		@Override
		public void onSuccess(int code, ByteArrayInputStream is, Object[] args) {
			try {
				Result res = XmlUtils.toBean(ResultBean.class, is).getResult();
				if (res.OK()) {
					AppContext.showToastShort(R.string.delete_success);
					mAdapter.removeItem(args[0]);
					setTweetCommentCount(-1);
				} else {
					AppContext.showToastShort(res.getErrorMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(code, e.getMessage(), args);
			}
		}

		@Override
		public void onFailure(int code, String errorMessage, Object[] args) {
			AppContext.showToastShort(R.string.delete_faile);
		}
	}

	private void setTweetCommentCount(int addCount) {
		mAdapter.notifyDataSetChanged();
		if (mTweet.getCommentCount() + addCount == -1) {
			return;
		}
		mTweet.setCommentCount(mTweet.getCommentCount() + addCount);
		mTvCommentCount.setText(getString(R.string.comment_count,
				mTweet.getCommentCount()));
	}

	protected void requestTweetData(boolean refresh) {
		String key = getCacheKey();
		if (TDevice.hasInternet()
				&& (!CacheManager.isReadDataCache(getActivity(), key) || refresh)) {
			sendRequestData();
		} else {
			readCacheData(key);
		}
	}

	private String getCacheKey() {
		return CACHE_KEY_PREFIX + mTweetId;
	}

	private void readCacheData(String cacheKey) {
		new CacheTask(getActivity()).execute(cacheKey);
	}

	private class CacheTask extends AsyncTask<String, Void, Tweet> {
		private WeakReference<Context> mContext;

		private CacheTask(Context context) {
			mContext = new WeakReference<Context>(context);
		}

		@Override
		protected Tweet doInBackground(String... params) {
			if (mContext.get() != null) {
				Serializable seri = CacheManager.readObject(mContext.get(),
						params[0]);
				if (seri == null) {
					return null;
				} else {
					return (Tweet) seri;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Tweet tweet) {
			super.onPostExecute(tweet);
			if (tweet != null) {
				executeOnLoadDataSuccess(tweet);
			} else {
				executeOnLoadDataError(null);
			}
			executeOnLoadFinish();
		}
	}

	private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
		private WeakReference<Context> mContext;
		private Serializable seri;
		private String key;

		private SaveCacheTask(Context context, Serializable seri, String key) {
			mContext = new WeakReference<Context>(context);
			this.seri = seri;
			this.key = key;
		}

		@Override
		protected Void doInBackground(Void... params) {
			CacheManager.saveObject(mContext.get(), seri, key);
			return null;
		}
	}

	private AsyncHttpResponseHandler mDetailHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				mTweet = XmlUtils.toBean(TweetDetail.class,
						new ByteArrayInputStream(arg2)).getTweet();
				if (mTweet != null && mTweet.getId() > 0) {
					executeOnLoadDataSuccess(mTweet);
					new SaveCacheTask(getActivity(), mTweet, getCacheKey())
							.execute();
				} else {
					throw new RuntimeException("load detail error");
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			readCacheData(getCacheKey());
		}
	};

	private void executeOnLoadDataSuccess(Tweet tweet) {
		mTweet = tweet;
		if (mTweet != null && mTweet.getId() > 0) {
			fillUI();
			mCurrentPage = 0;

			mState = STATE_REFRESH;
			mCurrentPage = 0;
			mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
			requestTweetCommentData(true);
		} else {
			throw new RuntimeException("load detail error");
		}
	}

	private void executeOnLoadFinish() {
		mState = STATE_NONE;
	}

	private void executeOnLoadDataError(Object object) {
		mEmptyView.setErrorType(EmptyLayout.NETWORK_ERROR);
	}

	protected void requestTweetCommentData(boolean refresh) {
		String key = getCacheCommentKey();
		if (TDevice.hasInternet()
				&& (!CacheManager.isReadDataCache(getActivity(), key) || refresh)) {
			sendRequestCommentData();
		} else {
			readCacheCommentData(key);
		}
	}

	private String getCacheCommentKey() {
		return CACHE_KEY_TWEET_COMMENT + mTweetId + "_" + mCurrentPage;
	}

	private void readCacheCommentData(String cacheKey) {
		new CacheCommentTask(getActivity()).execute(cacheKey);
	}

	private class CacheCommentTask extends AsyncTask<String, Void, CommentList> {
		private WeakReference<Context> mContext;

		private CacheCommentTask(Context context) {
			mContext = new WeakReference<Context>(context);
		}

		@Override
		protected CommentList doInBackground(String... params) {
			if (mContext.get() != null) {
				Serializable seri = CacheManager.readObject(mContext.get(),
						params[0]);
				if (seri == null) {
					return null;
				} else {
					return (CommentList) seri;
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(CommentList list) {
			super.onPostExecute(list);
			if (list != null) {
				executeOnLoadCommentDataSuccess(list);
			} else {
				executeOnLoadCommentDataError(null);
			}
			executeOnLoadCommentFinish();
		}
	}

	private AsyncHttpResponseHandler mCommentListHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				CommentList list = XmlUtils.toBean(CommentList.class,
						new ByteArrayInputStream(arg2));
				executeOnLoadCommentDataSuccess(list);
				new SaveCacheTask(getActivity(), list, getCacheCommentKey())
						.execute();
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
		}

		public void onFinish() {
			mState = STATE_NONE;
		}
	};

	private void executeOnLoadCommentDataSuccess(CommentList list) {
		if (mState == STATE_REFRESH)
			mAdapter.clear();
		List<Comment> data = list.getCommentlist();
		mAdapter.addData(data);
		mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
		if (data.size() == 0 && mState == STATE_REFRESH) {
			mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
		} else if (data.size() < TDevice.getPageSize()) {
			if (mState == STATE_REFRESH)
				mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
			else
				mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
		} else {
			mAdapter.setState(ListBaseAdapter.STATE_LOAD_MORE);
		}
	}

	private void executeOnLoadCommentFinish() {
		mState = STATE_NONE;
	}

	private void executeOnLoadCommentDataError(Object object) {
		mEmptyView.setErrorType(EmptyLayout.NETWORK_ERROR);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (position - 1 == -1) {
			return false;
		}
		final Comment item = (Comment) mAdapter.getItem(position - 1);
		if (item == null)
			return false;
		int itemsLen = item.getAuthorId() == AppContext.getInstance()
				.getLoginUid() ? 2 : 1;
		String[] items = new String[itemsLen];
		items[0] = getResources().getString(R.string.copy);
		if (itemsLen == 2) {
			items[1] = getResources().getString(R.string.delete);
		}
		final CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(getActivity());
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.setItemsWithoutChk(items, new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				dialog.dismiss();
				if (position == 0) {
					TDevice.copyTextToBoard(HTMLSpirit.delHTMLTag(item
							.getContent()));
				} else if (position == 1) {
					handleDeleteComment(item);
				}
			}
		});
		dialog.show();
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}
}
