package net.oschina.app.base;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import net.oschina.app.R;
import net.oschina.app.bean.Entity;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.internal.widget.ListPopupWindow;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ZoomButtonsController;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

public class BaseDetailFragment extends BaseFragment implements
		OnItemClickListener {
	
	public static final String INTENT_ACTION_COMMENT_CHANGED = "INTENT_ACTION_COMMENT_CHAGED";
	
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	private ListPopupWindow mMenuWindow;
	protected EmptyLayout mEmptyLayout;
	protected WebView mWebView;

	protected WebViewClient mWebClient = new WebViewClient() {

		private boolean receivedError = false;

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			receivedError = false;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			//UIHelper.showUrlRedirect(view.getContext(), url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (mEmptyLayout != null) {
				if (receivedError) {
					mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
				} else {
					mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
				}
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			receivedError = true;
		}
	};

	@SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void initWebView(WebView webView) {
		WebSettings settings = webView.getSettings();
		settings.setDefaultFontSize(15);
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		int sysVersion = Build.VERSION.SDK_INT;
		if (sysVersion >= 11) {
			settings.setDisplayZoomControls(false);
		} else {
			ZoomButtonsController zbc = new ZoomButtonsController(webView);
			zbc.getZoomControls().setVisibility(View.GONE);
		}
		
		//UIHelper.addWebImageShow(getActivity(), webView);
	}

	public class JavaScriptInterface {

		private Context ctx;

		public JavaScriptInterface(Context ctx) {
			this.ctx = ctx;
		}

		public void onImageClick(String bigImageUrl) {
			if (bigImageUrl != null) {
				// 显示图片
				//UIHelper.showImagePreview(ctx, new String[] { bigImageUrl });
			}
		}
	}

	protected void recycleWebView() {
		if (mWebView != null) {
			mWebView.setVisibility(View.GONE);
			mWebView.removeAllViews();
			mWebView.destroy();
			mWebView = null;
		}
	}

	protected boolean shouldRegisterCommentChangedReceiver() {
		return true;
	}

//	protected void onCommentChanged(int opt, int id, int catalog,
//			boolean isBlog, Comment comment) {
//	}

	private CommentChangeReceiver mReceiver;
	private AsyncTask<String, Void, Entity> mCacheTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if (shouldRegisterCommentChangedReceiver()) {
			mReceiver = new CommentChangeReceiver();
			IntentFilter filter = new IntentFilter(
					INTENT_ACTION_COMMENT_CHANGED);
			getActivity().registerReceiver(mReceiver, filter);
		}

		mController.getConfig().closeToast();
	}

	protected boolean hasReportMenu() {
		return false;
	}

	@Override
	public void onDestroyView() {
		recycleWebView();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		cancelReadCache();
		recycleWebView();
		if (mReceiver != null) {
			getActivity().unregisterReceiver(mReceiver);
		}
		super.onDestroy();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		requestData(false);
	}

	protected String getCacheKey() {
		return null;
	}

	protected Entity parseData(InputStream is) throws Exception {
		return null;
	}

	protected Entity readData(Serializable seri) {
		return null;
	}

	protected void sendRequestData() {
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

	private void readCacheData(String cacheKey) {
		cancelReadCache();
		mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
	}

	private void cancelReadCache() {
		if (mCacheTask != null) {
			mCacheTask.cancel(true);
			mCacheTask = null;
		}
	}

	private class CacheTask extends AsyncTask<String, Void, Entity> {
		private WeakReference<Context> mContext;

		private CacheTask(Context context) {
			mContext = new WeakReference<Context>(context);
		}

		@Override
		protected Entity doInBackground(String... params) {
			if (mContext.get() != null) {
				Serializable seri = CacheManager.readObject(mContext.get(),
						params[0]);
				if (seri == null) {
					return null;
				} else {
					return readData(seri);
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Entity entity) {
			super.onPostExecute(entity);
			if (entity != null) {
				executeOnLoadDataSuccess(entity);
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
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				Entity entity = parseData(new ByteArrayInputStream(arg2));
				if (entity != null) {
					executeOnLoadDataSuccess(entity);
					saveCache(entity);
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

	private boolean mIsFavorited;

	protected void saveCache(Entity entity) {
		new SaveCacheTask(getActivity(), entity, getCacheKey()).execute();
	}

	protected void executeOnLoadDataSuccess(Entity entity) {

	}

	protected void executeOnLoadDataError(String object) {
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
		mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mState = STATE_REFRESH;
				mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
				requestData(true);
			}
		});
	}

	protected void executeOnLoadFinish() {
	}

	protected void onFavoriteChanged(boolean flag) {
	}

//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.detail_menu, menu);
//		MenuItem item = menu.findItem(R.id.detail_menu_favorite);
//		if (AppContext.instance().isLogin()
//				&& mIsFavorited) {
//			item.setIcon(R.drawable.actionbar_unfavorite_icon);
//			item.setTitle(R.string.detail_menu_unfavorite);
//		} else {
//			item.setIcon(R.drawable.actionbar_favorite_icon);
//			item.setTitle(R.string.detail_menu_favorite);
//		}
//		
//		MenuItem share = menu.findItem(R.id.detail_menu_share);
//		MenuItem more = menu.findItem(R.id.detail_menu_more);
//		if(hasReportMenu()) {
//			more.setVisible(true);
//			share.setVisible(false);
//		} else {
//			more.setVisible(false);
//			share.setVisible(true);
//		}
//	}

	protected int getFavoriteTargetId() {
		return -1;
	}

	protected int getFavoriteTargetType() {
		return -1;
	}

	protected String getShareUrl() {
		return "";
	}

	protected String getShareTitle() {
		return getString(R.string.share_title);
	}

	protected String getShareContent() {
		return "";
	}

	private void showMoreOptionMenu(View view) {
		mMenuWindow = new ListPopupWindow(getActivity());
		mMenuWindow.setModal(true);
		mMenuWindow.setContentWidth(getResources().getDimensionPixelSize(
				R.dimen.popo_menu_dialog_width));
		mMenuWindow.setOnItemClickListener(this);
		mMenuWindow.setAnchorView(view);
		mMenuWindow.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == 0) {
			//handleFavoriteOrNot();
			handleShare();
		} else if (position == 1) {
			onReportMenuClick();
		} else if (position == 2) {

		}
		if (mMenuWindow != null) {
			mMenuWindow.dismiss();
			mMenuWindow = null;
		}
	}

	protected void onReportMenuClick() {
	}

	protected void handleFavoriteOrNot() {
		
	}

	protected void handleShare() {
		
	}

	class CommentChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
//			int opt = intent.getIntExtra(Comment.BUNDLE_KEY_OPERATION, 0);
//			int id = intent.getIntExtra(Comment.BUNDLE_KEY_ID, 0);
//			int catalog = intent.getIntExtra(Comment.BUNDLE_KEY_CATALOG, 0);
//			boolean isBlog = intent.getBooleanExtra(Comment.BUNDLE_KEY_BLOG,
//					false);
//			Comment comment = intent
//					.getParcelableExtra(Comment.BUNDLE_KEY_COMMENT);
//			onCommentChanged(opt, id, catalog, isBlog, comment);
		}
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
