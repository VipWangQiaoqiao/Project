package net.oschina.app.base;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import net.oschina.app.R;
import net.oschina.app.bean.Blog;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.TLog;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;

@SuppressLint("NewApi")
public abstract class BaseListFragment extends BaseTabFragment implements
		SwipeRefreshLayout.OnRefreshListener, OnItemClickListener,
		OnScrollListener {

	public static final String BUNDLE_KEY_CATALOG = "BUNDLE_KEY_CATALOG";
	public static final String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";
	public static final String BUNDLE_TWEET_TYPE = "BUNDLE_TWEET_TYPE";

	@InjectView(R.id.swiperefreshlayout)
	protected SwipeRefreshLayout mSwipeRefreshLayout;

	@InjectView(R.id.listview)
	protected ListView mListView;

	protected ListBaseAdapter mAdapter;

	@InjectView(R.id.error_layout)
	protected EmptyLayout mErrorLayout;

	protected int mStoreEmptyState = -1;

	protected int mCurrentPage = 0;

	protected int mCatalog = 1;
	
	protected String blogType;
	protected int tweetType = 0;
	
	private AsyncTask<String, Void, ListEntity> mCacheTask;
	private ParserTask mParserTask;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_pull_refresh_listview;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(getLayoutId(), container, false);
		// 通过注解绑定控件
		ButterKnife.inject(this, view);
		initView(view);
		return view;
	}

	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mCatalog = args.getInt(BUNDLE_KEY_CATALOG);
			blogType = args.getString(BUNDLE_BLOG_TYPE, blogType);
			tweetType = args.getInt(BUNDLE_KEY_CATALOG,tweetType);
		}
	}

	@Override
	public void initView(View view) {

		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.swiperefresh_color1, R.color.swiperefresh_color2,
				R.color.swiperefresh_color3, R.color.swiperefresh_color4);

		mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCurrentPage = 0;
				mState = STATE_REFRESH;
				mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
				requestData(true);
			}
		});

		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);

		if (mAdapter != null) {
			mListView.setAdapter(mAdapter);
			mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
		} else {
			mAdapter = getListAdapter();
			mListView.setAdapter(mAdapter);

			if (requestDataIfViewCreated()) {
				mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
				mCurrentPage = 0;
				mState = STATE_REFRESH;
				requestData(false);
			} else{
				mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
			}

		}
		if (mStoreEmptyState != -1) {
			mErrorLayout.setErrorType(mStoreEmptyState);
		}
			
	}

	@Override
	public void onDestroyView() {
		mStoreEmptyState = mErrorLayout.getErrorState();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		cancelReadCacheTask();
		cancelParserTask();
		super.onDestroy();
	}

	protected abstract ListBaseAdapter getListAdapter();

	// 下拉刷新数据
	@Override
	public void onRefresh() {
		// 设置顶部正在刷新
		setSwipeRefreshLoadingState();
		mCurrentPage = 0;
		mState = STATE_REFRESH;
		requestData(true);
	}

	protected boolean requestDataIfViewCreated() {
		return true;
	}

	protected String getCacheKeyPrefix() {
		return null;
	}

	protected ListEntity parseList(InputStream is) throws Exception {
		return null;
	}

	protected ListEntity readList(Serializable seri) {
		return null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	private String getCacheKey() {
		return new StringBuffer(getCacheKeyPrefix())
				.append("_").append(mCurrentPage).append("_")
				.append(TDevice.getPageSize()).toString();
	}

	protected void requestData(boolean refresh) {
		String key = getCacheKey();
		if (TDevice.hasInternet()
				&& (!CacheManager.isReadDataCache(getActivity(), key) || refresh)) {
			sendRequestData();
		} else {
			readCacheData(key);
		}
	}

	protected void sendRequestData() {
	}

	private void readCacheData(String cacheKey) {
		cancelReadCacheTask();
		mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
	}

	private void cancelReadCacheTask() {
		if (mCacheTask != null) {
			mCacheTask.cancel(true);
			mCacheTask = null;
		}
	}

	private class CacheTask extends AsyncTask<String, Void, ListEntity> {
		private WeakReference<Context> mContext;

		private CacheTask(Context context) {
			mContext = new WeakReference<Context>(context);
		}

		@Override
		protected ListEntity doInBackground(String... params) {
			Serializable seri = CacheManager.readObject(mContext.get(),
					params[0]);
			if (seri == null) {
				return null;
			} else {
				return readList(seri);
			}
		}

		@Override
		protected void onPostExecute(ListEntity list) {
			super.onPostExecute(list);
			if (list != null) {
				executeOnLoadDataSuccess(list.getList());
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

	protected AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

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

	protected void executeOnLoadDataSuccess(List<?> data) {
		if (mState == STATE_REFRESH)
			mAdapter.clear();
		mAdapter.addData(data);
		mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
		if (data.size() == 0 && mState == STATE_REFRESH) {
			mErrorLayout.setErrorType(EmptyLayout.NODATA);
		} else if (data.size() < getPageSize()) {
			if (mState == STATE_REFRESH)
				mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
			else
				mAdapter.setState(ListBaseAdapter.STATE_NO_MORE);
		} else {
			mAdapter.setState(ListBaseAdapter.STATE_LOAD_MORE);
		}
	}
	
	private int getPageSize() {
		if (blogType != null && blogType.equals(Blog.CATALOG_LATEST)) {
			return 19;
		} else {
			return TDevice.getPageSize();
		}
	}

	protected void onRefreshNetworkSuccess() {

	}

	protected void executeOnLoadDataError(String error) {
		if (mCurrentPage == 0) {
			mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
		} else {
			mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
			mAdapter.setState(ListBaseAdapter.STATE_NETWORK_ERROR);
			mAdapter.notifyDataSetChanged();
		}
	}

	// 完成刷新
	protected void executeOnLoadFinish() {
		setSwipeRefreshLoadedState();
		mState = STATE_NONE;
	}

	/** 设置顶部正在加载的状态 */
	private void setSwipeRefreshLoadingState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(true);
			// 防止多次重复刷新
			mSwipeRefreshLayout.setEnabled(false);
		}
	}

	/** 设置顶部加载完毕的状态 */
	private void setSwipeRefreshLoadedState() {
		if (mSwipeRefreshLayout != null) {
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(true);
		}
	}

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
				executeOnLoadDataSuccess(list);
				executeOnLoadFinish();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mAdapter == null || mAdapter.getCount() == 0) {
			return;
		}
		// 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
		if (mState == STATE_LOADMORE || mState == STATE_REFRESH) {
			return;
		}
		// 判断是否滚动到底部
		boolean scrollEnd = false;
		try {
			if (view.getPositionForView(mAdapter.getFooterView()) == view
					.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}

		if (mState == STATE_NONE && scrollEnd) {
			if (mAdapter.getState() == ListBaseAdapter.STATE_LOAD_MORE) {
				mCurrentPage++;
				mState = STATE_LOADMORE;
				requestData(true);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}
}