package net.oschina.app.util;

import java.io.ByteArrayInputStream;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Update;
import net.oschina.app.ui.dialog.CommonDialog;
import net.oschina.app.ui.dialog.DialogHelper;
import net.oschina.app.ui.dialog.WaitDialog;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 更新管理类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年11月18日 下午4:21:00
 * 
 */

public class UpdateManager {

	private Update mUpdate;

	private Context mContext;
	
	private boolean isShow = false;
	
	private WaitDialog _waitDialog;

	private AsyncHttpResponseHandler mCheckUpdateHandle = new AsyncHttpResponseHandler() {

		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2,
				Throwable arg3) {
			hideCheckDialog();
			if (isShow) {
				showFaileDialog();
			}
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			hideCheckDialog();
			mUpdate = XmlUtils.toBean(Update.class,
					new ByteArrayInputStream(arg2));
			
			onFinshCheck();
		}
	};

	public UpdateManager(Context context, boolean isShow) {
		this.mContext = context;
		this.isShow = isShow;
	}
	
	public boolean haveNew() {
		if (this.mUpdate == null) {
			return false;
		}
		boolean haveNew = false;
		int curVersionCode = TDevice.getVersionCode(AppContext
				.getInstance().getPackageName());
		if (curVersionCode < mUpdate.getUpdate().getAndroid()
				.getVersionCode()) {
			haveNew = true;
		}
		return haveNew;
	}

	public void checkUpdate() {
		if (isShow) {
			showCheckDialog();
		}
		OSChinaApi.checkUpdate(mCheckUpdateHandle);
	}
	
	private void onFinshCheck() {
		if (haveNew()) {
			showUpdateInfo();
		} else {
			if (isShow) {
				showLatestDialog();
			}
		}
	}

	private void showCheckDialog() {
		if (_waitDialog == null) {
			_waitDialog = DialogHelper.getWaitDialog((Activity) mContext, "正在获取新版本信息...");
		}
		_waitDialog.show();
	}

	private void hideCheckDialog() {
		if (_waitDialog != null) {
			_waitDialog.dismiss();
		}
	}
	
	private void showUpdateInfo() {
		if (mUpdate == null) {
			return;
		}
		CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(mContext);
		dialog.setTitle("发现新版本");
		dialog.setMessage(mUpdate.getUpdate().getAndroid().getUpdateLog());
		dialog.setNegativeButton(R.string.cancle, null);
		dialog.setPositiveButton("更新版本", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UIHelper.openDownLoadService(mContext, mUpdate.getUpdate().getAndroid().getDownloadUrl(), mUpdate.getUpdate().getAndroid().getVersionName());
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private void showLatestDialog() {
		CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(mContext);
		dialog.setMessage("已经是最新版本了");
		dialog.setPositiveButton("", null);
		dialog.show();
	}
	
	private void showFaileDialog() {
		CommonDialog dialog = DialogHelper.getPinterestDialogCancelable(mContext);
		dialog.setMessage("网络异常，无法获取新版本信息");
		dialog.setPositiveButton("", null);
		dialog.show();
	}
}
