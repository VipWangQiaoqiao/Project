package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.MyInformation;
import net.oschina.app.bean.User;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.AvatarView;

/**
 * 登录用户中心页面
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年10月30日 下午4:05:47 
 * 
 */

public class MyInformationFragment extends BaseFragment {
	
	@InjectView(R.id.iv_avatar)AvatarView mIvAvatar;
	@InjectView(R.id.iv_gender)ImageView mIvGender;
	@InjectView(R.id.tv_name) TextView mTvName;
	@InjectView(R.id.tv_sigin) TextView mTvSign;
	@InjectView(R.id.tv_favorite) TextView mTvFavorite;
	@InjectView(R.id.tv_following) TextView mTvFollowing;
	@InjectView(R.id.tv_follower) TextView mTvFollower;
//	@InjectView(R.id.tv_join_time) TextView mTvJoinTime;
//	@InjectView(R.id.tv_location) TextView mTvLocation;
//	@InjectView(R.id.tv_development_platform) TextView mTvDevelopmentPlatform;
//	@InjectView(R.id.tv_academic_focus) TextView mTvAcademicFocus;
	@InjectView(R.id.error_layout) EmptyLayout mEmptyView;
	
	@InjectView(R.id.error_layout) EmptyLayout mErrorLayout;
	
	private boolean mIsWatingLogin;
	
	private User mInfo;
	private AsyncTask<String, Void, User> mCacheTask;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Constants.INTENT_ACTION_LOGOUT)) {
				if (mErrorLayout != null) {
					mIsWatingLogin = true;
					mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
					mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
				}
			} else if (action.equals(Constants.INTENT_ACTION_USER_CHANGE)) {
				requestData(true);
			}
		}
	};

	private AsyncHttpResponseHandler mHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			try {
				mInfo = XmlUtils.toBean(MyInformation.class, new ByteArrayInputStream(arg2)).getUser();
				if (mInfo != null) {
					fillUI();
					mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
					new SaveCacheTask(getActivity(), mInfo, getCacheKey())
							.execute();
				} else {
					onFailure(arg0, arg1, arg2, new Throwable());
				}
			} catch (Exception e) {
				e.printStackTrace();
				onFailure(arg0, arg1, arg2, e);
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			mEmptyView.setErrorType(EmptyLayout.NETWORK_ERROR);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_LOGOUT);
		filter.addAction(Constants.INTENT_ACTION_USER_CHANGE);
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my_information, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		requestData(false);
		return view;
	}
	
	@Override
	public void initView(View view) {
		mIvAvatar.setOnClickListener(this);
		mEmptyView.setOnLayoutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (AppContext.getInstance().isLogin()) {
					requestData(true);
				} else {
					UIHelper.showLoginActivity(getActivity());
				}
			}
		});
		
		view.findViewById(R.id.rl_user_center).setOnClickListener(this);
		view.findViewById(R.id.ly_favorite).setOnClickListener(this);
		view.findViewById(R.id.ly_following).setOnClickListener(this);
		view.findViewById(R.id.ly_follower).setOnClickListener(this);
		view.findViewById(R.id.rl_blog).setOnClickListener(this);
		view.findViewById(R.id.rl_qrcode).setOnClickListener(this);
	}

	private void fillUI() {
		ImageLoader.getInstance().displayImage(
				AvatarView.getLargeAvatar(mInfo.getPortrait()), mIvAvatar);
		mTvName.setText(mInfo.getName());
		mIvGender
				.setImageResource(StringUtils.toInt(mInfo.getGender()) == 1 ? R.drawable.userinfo_icon_male
						: R.drawable.userinfo_icon_female);
		mTvFavorite.setText(String.valueOf(mInfo.getFavoritecount()));
		mTvFollowing.setText(String.valueOf(mInfo.getFollowerscount()));
		mTvFollower.setText(String.valueOf(mInfo.getFanscount()));

//		mTvJoinTime.setText(mInfo.getJointime());
//		mTvLocation.setText(mInfo.getFrom());
		mTvSign.setText(mInfo.getFrom());
//		mTvDevelopmentPlatform.setText(mInfo.getDevplatform());
//		mTvAcademicFocus.setText(mInfo.getExpertise());
	}
	
	private void handleLogout() {
//		CommonDialog dialog = DialogHelper
//				.getPinterestDialogCancelable(getActivity());
//		dialog.setMessage(R.string.message_logout);
//		dialog.setPositiveButton(R.string.ok,
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						AppContext.instance().Logout();
//						AppContext.showToastShort(R.string.tip_logout_success);
//						getActivity().finish();
//					}
//				});
//		dialog.setNegativeButton(R.string.cancle, null);
//		dialog.show();
	}

	private void requestData(boolean refresh) {
		
		if (AppContext.getInstance().isLogin()) {
			mIsWatingLogin = false;
			mEmptyView.setErrorType(EmptyLayout.NETWORK_LOADING);
			String key = getCacheKey();
			if (TDevice.hasInternet()
					&& (!CacheManager.isReadDataCache(getActivity(), key) || refresh)) {
				sendRequestData();
			} else {
				readCacheData(key);
			}
		} else {
			mIsWatingLogin = true;
			mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
			mErrorLayout.setErrorMessage(getString(R.string.unlogin_tip));
		}
	}

	private void readCacheData(String key) {
		cancelReadCacheTask();
		mCacheTask = new CacheTask(getActivity()).execute(key);
	}

	private void cancelReadCacheTask() {
		if (mCacheTask != null) {
			mCacheTask.cancel(true);
			mCacheTask = null;
		}
	}

	private void sendRequestData() {
		int uid = AppContext.getInstance().getLoginUid();
		OSChinaApi.getMyInformation(uid, mHandler);
	}

	private String getCacheKey() {
		return "my_information" + AppContext.getInstance().getLoginUid();
	}

	private class CacheTask extends AsyncTask<String, Void, User> {
		private WeakReference<Context> mContext;

		private CacheTask(Context context) {
			mContext = new WeakReference<Context>(context);
		}

		@Override
		protected User doInBackground(String... params) {
			Serializable seri = CacheManager.readObject(mContext.get(),
					params[0]);
			if (seri == null) {
				return null;
			} else {
				return (User) seri;
			}
		}

		@Override
		protected void onPostExecute(User info) {
			super.onPostExecute(info);
			mInfo = info;
			if (mInfo != null) {
				fillUI();
				mEmptyView.setErrorType(EmptyLayout.HIDE_LAYOUT);
			} else {
				mEmptyView.setErrorType(EmptyLayout.NETWORK_ERROR);
			}
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
	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.iv_avatar:
			UIHelper.showUserAvatar(getActivity(), mInfo.getPortrait());
			break;
		case R.id.ly_follower:
			UIHelper.showFriends(getActivity(), AppContext.getInstance().getLoginUid(), 1);
			break;
		case R.id.ly_following:
			UIHelper.showFriends(getActivity(), AppContext.getInstance().getLoginUid(), 0);
			break;
		case R.id.ly_favorite:
			UIHelper.showUserFavorite(getActivity(),AppContext.getInstance().getLoginUid());
			break;
		case R.id.rl_blog:
			UIHelper.showUserBlog(getActivity(), AppContext.getInstance().getLoginUid());
			break;
		case R.id.rl_qrcode:
			
			break;
		case R.id.rl_user_center:
			UIHelper.showUserCenter(getActivity(), AppContext.getInstance().getLoginUid(), mInfo.getName());
			break;
		default:
			break;
		}
	}

	@Override
	public void initData() {

	}
}
