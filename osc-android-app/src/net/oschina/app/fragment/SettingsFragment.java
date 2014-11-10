package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.apache.http.Header;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.AppManager;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.Update;
import net.oschina.app.ui.dialog.CommonDialog;
import net.oschina.app.ui.dialog.DialogHelper;
import net.oschina.app.util.FileUtils;
import net.oschina.app.util.MethodsCompat;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.togglebutton.ToggleButton;
import net.oschina.app.widget.togglebutton.ToggleButton.OnToggleChanged;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class SettingsFragment extends BaseFragment {
	
	@InjectView(R.id.tb_loading_img) ToggleButton mTbLoadImg;
	@InjectView(R.id.tv_cache_size) TextView mTvCacheSize;
	
	@InjectView(R.id.pb_loading) ProgressBar mPbCheckLoading;
	@InjectView(R.id.tv_version) TextView mTvVersionStatus;
	
	private boolean mIsCheckingUpdate = true;
	
	private AsyncHttpResponseHandler mCheckUpdateHandle = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			if (getActivity() == null || !getActivity().isFinishing()) {
				mIsCheckingUpdate = false;
				mPbCheckLoading.setVisibility(View.GONE);
				AppContext.showToast("未能获取到新版本信息");
			}
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			if (getActivity() == null || !getActivity().isFinishing()) { 
				mIsCheckingUpdate = false;
				mPbCheckLoading.setVisibility(View.GONE);
				Update update = XmlUtils.toBean(Update.class, new ByteArrayInputStream(arg2));
				mTvVersionStatus.setText(update.getUpdate().getAndroid().getVersionName());
				mTvVersionStatus.setVisibility(View.VISIBLE);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, container,
				false);
		ButterKnife.inject(this, view);
		initView(view);
		initData();
		return view;
	}
	
	@Override
	public void initView(View view) {
		mTbLoadImg.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				AppContext.setLoadImage(on);
			}
		});
		
		view.findViewById(R.id.rl_loading_img).setOnClickListener(this);
		view.findViewById(R.id.rl_notification_settings).setOnClickListener(this);
		view.findViewById(R.id.rl_clean_cache).setOnClickListener(this);
		view.findViewById(R.id.rl_check_update).setOnClickListener(this);
		view.findViewById(R.id.rl_feedback).setOnClickListener(this);
		view.findViewById(R.id.rl_grade).setOnClickListener(this);
		view.findViewById(R.id.rl_about).setOnClickListener(this);
		view.findViewById(R.id.rl_exit).setOnClickListener(this);
	}

	public void initData() {
		if (AppContext.get(AppConfig.KEY_LOAD_IMAGE, true))
			mTbLoadImg.setToggleOn();
		else
			mTbLoadImg.setToggleOff();
		
		caculateCacheSize();
		
		mIsCheckingUpdate = true;
		
		checkUpdate();
	}

	/**
	 * 计算缓存的大小
	 */
	private void caculateCacheSize() {
		long fileSize = 0;
		String cacheSize = "0KB";
		File filesDir = getActivity().getFilesDir();
		File cacheDir = getActivity().getCacheDir();

		fileSize += FileUtils.getDirSize(filesDir);
		fileSize += FileUtils.getDirSize(cacheDir);
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			File externalCacheDir = MethodsCompat
					.getExternalCacheDir(getActivity());
			fileSize += FileUtils.getDirSize(externalCacheDir);
		}
		if (fileSize > 0)
			cacheSize = FileUtils.formatFileSize(fileSize);
		mTvCacheSize.setText(cacheSize);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.rl_loading_img:
			mTbLoadImg.toggle();
			break;
		case R.id.rl_notification_settings:
			UIHelper.showSettingNotification(getActivity());
			break;
		case R.id.rl_clean_cache:
			onClickCleanCache();
			break;
		case R.id.rl_check_update:
			if (!mIsCheckingUpdate) {
				checkUpdate();
			}
			break;
		case R.id.rl_feedback:
			showFeedBack();
			break;
		case R.id.rl_grade:
			TDevice.openAppInMarket(getActivity());
			break;
		case R.id.rl_about:
			break;
		case R.id.rl_exit:
			onClickExit();
			break;
		default:
			break;
		}
		
	}
	
	private void onClickCleanCache() {
		CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(getActivity());
		dialog.setMessage(R.string.clean_cache_mes);
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						UIHelper.clearAppCache(getActivity());
						mTvCacheSize.setText("0KB");
						dialog.dismiss();
					}
				});
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.show();
	}
	
	private void checkUpdate() {
		mIsCheckingUpdate = true;
		mPbCheckLoading.setVisibility(View.VISIBLE);
		mTvVersionStatus.setVisibility(View.GONE);
		OSChinaApi.checkUpdate(mCheckUpdateHandle);
	}
	
	private void showFeedBack() {
		TDevice.sendEmail(getActivity(), "用户反馈-OSC Android客户端", "", "apposchina@gmail.com");
	}
	
	private void onClickExit() {
		final CommonDialog dialog = DialogHelper
				.getPinterestDialogCancelable(getActivity());
		
		OnClickListener click = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int id = v.getId();
				dialog.dismiss();
				switch (id) {
				case R.id.rl_app_exit:
					AppContext.set(AppConfig.KEY_NOTIFICATION_DISABLE_WHEN_EXIT, false);
					AppManager.getAppManager().AppExit(getActivity());
					getActivity().finish();
					break;
				case R.id.rl_loginout:
					AppContext.getInstance().Logout();
					AppContext.showToastShort(R.string.tip_logout_success);
					break;
				default:
					break;
				}
			}
		};
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_setting_exit, null);
		view.findViewById(R.id.rl_app_exit).setOnClickListener(click);
		view.findViewById(R.id.rl_loginout).setOnClickListener(click);
		if (!AppContext.getInstance().isLogin()) {
			view.findViewById(R.id.rl_loginout).setVisibility(View.GONE);
			view.findViewById(R.id.v_line).setVisibility(View.GONE);
		}
		dialog.setContent(view);
		dialog.show();
	}
}
