package com.zbar.lib;

import java.io.IOException;
import org.apache.http.Header;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.ui.dialog.CommonDialog;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.ClipboardManager;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import net.oschina.app.bean.BarCode;
import net.oschina.app.bean.SingInResult;

/**
 * 二维码扫描界面
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月31日 上午10:44:54
 *
 */
public class CaptureActivity extends BaseActivity implements Callback {

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	
	@InjectView(R.id.capture_containter)
	RelativeLayout mContainer = null;
	
	@InjectView(R.id.capture_crop_layout)
	RelativeLayout mCropLayout = null;
	
	@InjectView(R.id.capture_flash)
	ImageView mFlash;
	
	@InjectView(R.id.capture_scan_line)
	ImageView mQrLineView;
	
	@InjectView(R.id.capture_loading)
	View mLoading;
	
	private boolean isNeedCapture = false;
	
	public boolean isNeedCapture() {
		return isNeedCapture;
	}
	
	@Override
	protected boolean hasActionBar() {
		return false;
	}

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_qr_scan;
	}

	@Override
	protected boolean hasBackButton() {
		return true;
	}

	@Override
	protected int getActionBarTitle() {
		return R.string.actionbar_title_qr_scan;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.inject(this);
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		
		mFlash.setOnClickListener(this);
		
		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
		
	}
	
	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
			mFlash.setBackgroundResource(R.drawable.flash_open);
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
			mFlash.setBackgroundResource(R.drawable.flash_default);
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.capture_flash:
			light();
			break;

		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(final String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		mLoading.setVisibility(View.VISIBLE);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mLoading.setVisibility(View.GONE);
				handleText(result);
			}
		}, 800);
	}
	
	private void handleText(String text) {
		if (StringUtils.isUrl(text)) {
			showUrlOption(text);
		} else {
			handleOtherText(text);
		}
	}
	
	private void showUrlOption(final String url) {
		if (url.contains("oschina.net")) {
			UIHelper.showUrlRedirect(CaptureActivity.this, url);
			finish();
			return;
		}
		CommonDialog dialog = new CommonDialog(CaptureActivity.this);
		dialog.setMessage("可能存在风险，是否打开链接?<br/>" + url);
		dialog.setNegativeButton("打开", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UIHelper.showUrlRedirect(CaptureActivity.this, url);
				dialog.dismiss();
				finish();
			}
		});
		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.show();
	}
	
	private void handleOtherText(final String text) {
		// 判断是否符合基本的json格式
		if (!text.matches("^\\{.*")) {
			showCopyTextOption(text);
		} else {
			try {
				BarCode barcode = BarCode.parse(text);
				int type = barcode.getType();
				switch (type) {
				case BarCode.SIGN_IN:// 签到
					handleSignIn(barcode);
					break;
				default:
					break;
				}
			} catch (AppException e) {
				showCopyTextOption(text);
			}
		}
	}
	
	private void handleSignIn(BarCode barCode) {
		if (barCode.isRequireLogin() && !AppContext.getInstance().isLogin()) {
			showLogin();
			return;
		}
		showWaitDialog("正在签到...");
		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				try {
					SingInResult res = SingInResult.parse(new String(arg2));
					if (res.isOk()) {
						getMesDialog(res.getMessage()).show();
					} else {
						getMesDialog(res.getErrorMes()).show();
					}
				} catch (AppException e) {
					e.printStackTrace();
					onFailure(arg0, arg1, arg2, e);
				}
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				hideWaitDialog();
				getMesDialog(arg3.getMessage()).show();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				hideWaitDialog();
			}
		};
		OSChinaApi.singnIn(barCode.getUrl(), handler);
	}
	
	private CommonDialog getMesDialog(String mes) {
		CommonDialog dialog = new CommonDialog(CaptureActivity.this);
		dialog.setMessage(mes);
		dialog.setPositiveButton("", null);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				finish();
			}
		});
		return dialog;
	}
	
	private void showLogin() {
		CommonDialog dialog = new CommonDialog(CaptureActivity.this);
		dialog.setMessage("需要先登录");
		dialog.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UIHelper.showLoginActivity(CaptureActivity.this);
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private void showCopyTextOption(final String text) {
		CommonDialog dialog = new CommonDialog(CaptureActivity.this);
		dialog.setMessage(text);
		dialog.setNegativeButton("复制", new DialogInterface.OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				cbm.setText(text);
				AppContext.showToast("复制成功");
				dialog.dismiss();
				finish();
			}
		});
		dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		dialog.show();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			setNeedCapture(false);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return handler;
	}

	private void initBeepSound() {
		mediaPlayer = MediaPlayer.create(this, R.raw.qr_sacn);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(beepListener);
	}
	
	private static final long VIBRATE_DURATION = 30L;

	private void playBeepSoundAndVibrate() {
		if (mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.release();
		}
	};

	@Override
	public void initView() {
		
	}

	@Override
	public void initData() {
		
	}
}