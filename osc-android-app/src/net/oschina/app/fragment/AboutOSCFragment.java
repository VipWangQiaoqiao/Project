package net.oschina.app.fragment;

import java.io.ByteArrayInputStream;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.Update;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutOSCFragment extends BaseFragment {

	@InjectView(R.id.pb_loading)
	ProgressBar mPbCheckLoading;

	@InjectView(R.id.tv_version)
	TextView mTvVersionStatus;
	
	@InjectView(R.id.tv_version_name)
	TextView mTvVersionName;

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
				Update update = XmlUtils.toBean(Update.class,
						new ByteArrayInputStream(arg2));
				int curVersionCode = TDevice.getVersionCode(AppContext
						.getInstance().getPackageName());
				if (curVersionCode < update.getUpdate().getAndroid()
						.getVersionCode()) {
					mTvVersionStatus.setText("发现新版本");
					mTvVersionStatus.setTextColor(getResources().getColor(
							R.color.red));
					Drawable drawable = getResources().getDrawable(
							R.drawable.notification_bg);
					mTvVersionStatus.setCompoundDrawablesWithIntrinsicBounds(
							drawable, null, null, null);
					mTvVersionStatus.setTag(true);
				} else {
					mTvVersionStatus.setText("已经是最新版");
				}
				mTvVersionStatus.setVisibility(View.VISIBLE);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);
		ButterKnife.inject(this, view);
		initView(view);
		initData();
		return view;
	}

	@Override
	public void initView(View view) {
		view.findViewById(R.id.rl_check_update).setOnClickListener(this);
		view.findViewById(R.id.rl_feedback).setOnClickListener(this);
		view.findViewById(R.id.rl_grade).setOnClickListener(this);
		view.findViewById(R.id.rl_gitapp).setOnClickListener(this);
		view.findViewById(R.id.tv_oscsite).setOnClickListener(this);
		view.findViewById(R.id.tv_knowmore).setOnClickListener(this);
	}

	public void initData() {
		mIsCheckingUpdate = true;
		
		mTvVersionName.setText("V " + TDevice.getVersionName());
		
		checkUpdate();
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.rl_check_update:
			if (!mIsCheckingUpdate) {
				onClickUpdate();
			}
			break;
		case R.id.rl_feedback:
			showFeedBack();
			break;
		case R.id.rl_grade:
			TDevice.openAppInMarket(getActivity());
			break;
		case R.id.rl_gitapp:
			TDevice.gotoMarket(getActivity(), "net.oschina.gitapp");
			break;
		case R.id.tv_oscsite:
			UIHelper.openBrowser(getActivity(), "https://www.oschina.net");
			break;
		case R.id.tv_knowmore:
			UIHelper.openBrowser(getActivity(), "https://www.oschina.net/home/aboutosc");
			break;
		default:
			break;
		}
	}

	private void onClickUpdate() {
		if ((Boolean) mTvVersionStatus.getTag()) {
			AppContext.showToast("已经有新版本");
		} else {
			checkUpdate();
		}
	}

	private void checkUpdate() {
		mIsCheckingUpdate = true;
		mPbCheckLoading.setVisibility(View.VISIBLE);
		mTvVersionStatus.setVisibility(View.GONE);
		OSChinaApi.checkUpdate(mCheckUpdateHandle);
	}

	private void showFeedBack() {
		TDevice.sendEmail(getActivity(), "用户反馈-OSC Android客户端", "",
				"apposchina@gmail.com");
	}
}
