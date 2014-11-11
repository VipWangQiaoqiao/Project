package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.CommentAdapter;
import net.oschina.app.adapter.CommentAdapter.OnOperationListener;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Comment;
import net.oschina.app.bean.CommentList;
import net.oschina.app.bean.Entity;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetDetail;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.service.PublicCommentTask;
import net.oschina.app.service.ServerTaskUtils;
import net.oschina.app.ui.ImagePreviewActivity;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.AvatarView;
import net.oschina.app.widget.MyLinkMovementMethod;
import net.oschina.app.widget.MyURLSpan;
import net.oschina.app.widget.TweetTextView;

import org.apache.http.Header;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetDetailFragment1 extends BaseFragment implements
		EmojiTextListener, EmojiFragmentControl,OnScrollListener ,OnOperationListener,OnItemClickListener{
	
	private final static String AT_HOST_PRE = "http://my.oschina.net";
	private final static String MAIN_HOST = "http://www.oschina.net";
	
	private static final String TWEET_CACHE_KEY = "tweet_";
	private static final String CACHE_KEY_TWEET_COMMENT = "tweet_comment_";
	protected static final String TAG = TweetDetailFragment1.class.getSimpleName();
	private ParserTask mParserTask;
	
	ListView mListView;
	AvatarView face;
	TextView author;
	TextView time;
	TweetTextView content;
	public ImageView image;
	
	private int mCurrentPage = 0;
	private int mTweetId;
	private Tweet mTweet;
	private EmojiFragment mEmojiFragment;
	private EmptyLayout mErrorLayout;
	private CommentAdapter commentAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tweet_detail, container, false);

		mTweetId = getActivity().getIntent().getIntExtra("tweet_id", 0);
		ButterKnife.inject(this, view);
		initView(view);
		requestTweetData(true);
		return view;
	}
	
	protected void requestTweetData(boolean refresh){
		String key = getCacheKey();
		if(TDevice.hasInternet() && (!CacheManager.isReadDataCache(getActivity(),key) || refresh)){
			sendRequestData();
		}
	}
	
	@Override
	public void initView(View view) {
		mListView = (ListView) view.findViewById(R.id.tweet_detail_listview);
		mErrorLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(commentreply);
		View header = LayoutInflater.from(getActivity()).inflate(R.layout.tweet_listview_head, null);
		face = (AvatarView) header.findViewById(R.id.iv_tweet_detail_face);
		author = (TextView) header.findViewById(R.id.tv_tweet_detail_name);
		time = (TextView) header.findViewById(R.id.tv_tweet_detail_time);
		content = (TweetTextView) header.findViewById(R.id.tv_tweet_detail_item);
		image = (ImageView) header.findViewById(R.id.iv_tweet_detail_image);
		mListView.addHeaderView(header,null,false);
		commentAdapter = new CommentAdapter(this, true);
		mListView.setAdapter(commentAdapter);
	}
	OnItemClickListener commentreply = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final Comment comment = (Comment) commentAdapter.getItem(position-1);
			if (comment == null)
				return;
			if(!AppContext.getInstance().isLogin()){
				UIHelper.showLoginActivity(getActivity());
			}
			mEmojiFragment.setTag(comment);
			mEmojiFragment.setInputHint("@" + comment.getAuthor() + " ");
			mEmojiFragment.requestFocusInput();
		}
	};
	
	private String getCacheKey() {
		return new StringBuilder(TWEET_CACHE_KEY).append(mTweetId).toString();
	}
	
	/*
	 * 解析动弹详情
	 */
	protected Entity parseData(InputStream is) throws Exception {
		return XmlUtils.toBean(TweetDetail.class, is).getTweet();
	}
	
	protected void sendRequestData() {
		mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		OSChinaApi.getTweetDetail(mTweetId, mDetailHandler);
	}
	
	private AsyncHttpResponseHandler mDetailHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				Entity entity = parseData(new ByteArrayInputStream(arg2));

				if (entity != null && entity.getId() > 0) {
					executeOnLoadTweetDetailDataSuccess(entity);
					new SaveCacheTask(getActivity(), entity, getCacheKey()).execute();
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
				executeOnLoadTweetDetailDataSuccess(tweet);
			} else {
				executeOnLoadDataError(null);
			}
			executeOnLoadFinish();
		}
	}
	
	private void executeOnLoadDataError(Object object) {
		mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
	}
	private void executeOnLoadFinish() {
		mState = STATE_NONE;
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
	
	protected void requestTweetComment(boolean reflesh) {
		String key = getCacheCommentKey() ;
		if(TDevice.hasInternet() && (!CacheManager.isReadDataCache(getActivity(), key) || reflesh)){
			sendRequestCommentData();
		}
	}
	
	protected ListEntity parseList(InputStream is) throws Exception {
		CommentList list = XmlUtils.toBean(CommentList.class, is);
		return list;
	}
	
	private void sendRequestCommentData() {
		OSChinaApi.getCommentList(mTweetId, CommentList.CATALOG_TWEET, mCurrentPage, mCommentHandler);
	}
	
	private AsyncHttpResponseHandler mCommentHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				byte[] responseBytes) {
			if (isAdded()) {
				if (mState == STATE_REFRESH) {
					onRefreshNetworkSuccess();
				}
				executeParserTask(responseBytes);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			if (isAdded()) {
				readCacheData(getCacheKey());
			}
		}
	};
	
	private void executeParserTask(byte[] data) {
		cancelParserTask();
		mParserTask = new ParserTask(data);
		mParserTask.execute();
	}
	
	private void cancelParserTask() {
		if (mParserTask != null) {
			mParserTask.cancel(true);
			mParserTask = null;
		}
	}
	
	protected void onRefreshNetworkSuccess() {
	}
	
	private void executeOnLoadCommentDataSuccess(List<?> data) {
		if (mState == STATE_REFRESH)
			commentAdapter.clear();
		commentAdapter.addData(data);
		mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
		if (data.size() == 0 && mState == STATE_REFRESH) {
			mErrorLayout.setErrorType(EmptyLayout.NODATA);
		} else if (data.size() < getPageSize()) {
			if (mState == STATE_REFRESH)
				commentAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
			else
				commentAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
		} else {
			commentAdapter.setState(ListBaseAdapter.STATE_LOAD_MORE);
		}
	}
	
	private int getPageSize() {
			return TDevice.getPageSize();
	}

	private String getCacheCommentKey() {
		return CACHE_KEY_TWEET_COMMENT + mTweetId + "_" + mCurrentPage;
	}
	

	protected void executeOnLoadTweetDetailDataSuccess(Entity entity) {
		mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
		mTweet = (Tweet) entity;
		if(mTweet != null && mTweet.getId() >0){
			fillUI();
			requestTweetComment(true);
		}
	}
	
	private String modifyPath(String message) {
		message = message.replaceAll("(<a[^>]+href=\")/([\\S]+)\"", "$1"
				+ AT_HOST_PRE + "/$2\"");
		message = message.replaceAll(
				"(<a[^>]+href=\")http://m.oschina.net([\\S]+)\"", "$1"
						+ MAIN_HOST + "$2\"");
		return message;
	}

	private void fillUI() {
		face.setUserInfo(mTweet.getAuthorid(), mTweet.getAuthor());
		face.setAvatarUrl(mTweet.getPortrait());
		author.setText(mTweet.getAuthor());
		time.setText(StringUtils.friendly_time(mTweet.getPubDate()));
		content.setMovementMethod(MyLinkMovementMethod.a());
		content.setFocusable(false);
		content.setDispatchToParent(true);
		content.setLongClickable(false);
		Spanned span = Html.fromHtml(modifyPath(mTweet.getBody()));
		content.setText(span);
		MyURLSpan.parseLinkText(content, span);
		
		image.setVisibility(AvatarView.GONE);
		if (mTweet.getImgSmall() != null && !TextUtils.isEmpty(mTweet.getImgSmall())) {
			image.setVisibility(AvatarView.VISIBLE);
			ImageLoader.getInstance().displayImage(mTweet.getImgSmall(),
					image);
			image.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ImagePreviewActivity.showImagePrivew(getActivity(), 0, new String[] {mTweet.getImgBig()});
				}
			});
		}
	}

	@Override
	public void setEmojiFragment(EmojiFragment fragment) {
		mEmojiFragment = fragment;
		mEmojiFragment.setEmojiTextListener(this);
		mEmojiFragment.setButtonMoreVisibility(View.GONE);
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
		task.setCatalog(CommentList.CATALOG_TWEET);
		task.setIsPostToMyZone(0);
		task.setUid(AppContext.getInstance().getLoginUid());
		ServerTaskUtils.pubTweetComment(getActivity(), task);
		mEmojiFragment.reset();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (commentAdapter == null || commentAdapter.getCount() == 0) {
			return;
		}
		// 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
		if (mState == STATE_LOADMORE || mState == STATE_REFRESH) {
			return;
		}
		// 判断是否滚动到底部
		boolean scrollEnd = false;
		try {
			if (view.getPositionForView(commentAdapter.getFooterView()) == view
					.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}

		if (mState == STATE_NONE && scrollEnd) {
			if (commentAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE) {
				mCurrentPage++;
				mState = STATE_LOADMORE;
				requestTweetComment(true);
			}
		}
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onMoreClick(Comment comment) {}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void initData() {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
	
	class ParserTask extends AsyncTask<Void, Void, String> {

		private byte[] reponseData;
		private boolean parserError;
		private List<?> list;

		public ParserTask(byte[] data) {
			this.reponseData = data;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				ListEntity data = parseList(new ByteArrayInputStream(
						reponseData));
				new SaveCacheTask(getActivity(), data, getCacheKey()).execute();
				list = data.getList();
			} catch (Exception e) {
				e.printStackTrace();
				parserError = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (parserError) {
				readCacheData(getCacheKey());
			} else {
				executeOnLoadCommentDataSuccess(list);
				executeOnLoadFinish();
			}
		}
	}
	
}

