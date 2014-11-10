package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;
import com.umeng.socialize.utils.Log;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.TweetAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetsList;
import net.oschina.app.ui.NavigationDrawerFragment;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

/**
 * @author HuangWenwei
 * 
 * @date 2014年10月10日
 */
public class TweetsFragment extends BaseListFragment {

	protected static final String TAG = TweetsFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "tweetslist_";

	private boolean mIsWatingLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(
				NavigationDrawerFragment.INTENT_ACTION_USER_CHANGE);
		getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public void onResume() {
		if (mIsWatingLogin) {
			mCurrentPage = 0;
			mState = STATE_REFRESH;
			requestData(false);
		}
		super.onResume();
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new TweetAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX + tweetType;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		TweetsList list = XmlUtils.toBean(TweetsList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((TweetsList) seri);
	}

	@Override
	protected void sendRequestData() {
		OSChinaApi.getTweetList(tweetType, mCurrentPage, mHandler);
		Log.e("获取用户uid:", String.valueOf(tweetType));
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Tweet tweet = (Tweet) mAdapter.getItem(position);
		if (tweet != null)
			UIHelper.showTweetDetail(view.getContext(), tweet.getId());
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			setupContent();
		}
	};

	private void setupContent() {
		if (mErrorLayout != null) {
			mIsWatingLogin = true;
			mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
			mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
		}
	}

	@Override
	protected void requestData(boolean refresh) {
		if (tweetType > 0) {
			if (AppContext.getInstance().isLogin()) {
				mCatalog = AppContext.getInstance().getLoginUid();
				mIsWatingLogin = false;
				super.requestData(refresh);
			} else {
				mIsWatingLogin = true;
				mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
				mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
			}
		} else {
			mIsWatingLogin = false;
			super.requestData(refresh);
		}
	}

	@Override
	public void initView(View view) {
		super.initView(view);
		mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (AppContext.getInstance().isLogin()) {
					requestData(true);
				} else {
					UIHelper.showLoginActivity(getActivity());
				}
			}
		});
	}
}