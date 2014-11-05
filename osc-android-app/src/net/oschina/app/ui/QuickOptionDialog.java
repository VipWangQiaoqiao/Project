package net.oschina.app.ui;

import com.zbar.lib.CaptureActivity;

import net.oschina.app.R;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.fragment.TweetPubFragment;
import net.oschina.app.util.UIHelper;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class QuickOptionDialog extends Dialog implements
		android.view.View.OnClickListener {

	public interface OnQuickOptionformClick {
		void onQuickOptionClick(int id);
	}

	private OnQuickOptionformClick mListener;

	private QuickOptionDialog(Context context, boolean flag,
			OnCancelListener listener) {
		super(context, flag, listener);
	}

	@SuppressLint("InflateParams")
	private QuickOptionDialog(Context context, int defStyle) {
		super(context, defStyle);
		View contentView = getLayoutInflater().inflate(
				R.layout.quick_option_dialog, null);
		contentView.findViewById(R.id.ly_quick_option_text).setOnClickListener(
				this);
		contentView.findViewById(R.id.ly_quick_option_album)
				.setOnClickListener(this);
		contentView.findViewById(R.id.ly_quick_option_photo)
				.setOnClickListener(this);
		contentView.findViewById(R.id.ly_quick_option_voice)
				.setOnClickListener(this);
		contentView.findViewById(R.id.ly_quick_option_scan).setOnClickListener(
				this);
		contentView.findViewById(R.id.ly_quick_option_note).setOnClickListener(
				this);
		contentView.findViewById(R.id.iv_close).setOnClickListener(
				this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		contentView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				QuickOptionDialog.this.dismiss();
				return true;
			}
		});
		super.setContentView(contentView);
	}

	public QuickOptionDialog(Context context) {
		this(context, R.style.quick_option_dialog);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// getWindow().setGravity(Gravity.BOTTOM);

		WindowManager m = getWindow().getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.width = d.getWidth();
		getWindow().setAttributes(p);
	}

	public void setOnQuickOptionformClickListener(OnQuickOptionformClick lis) {
		mListener = lis;
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.iv_close:
			dismiss();
			break;
		case R.id.ly_quick_option_text:
			onClickTweetPub(R.id.ly_quick_option_text);
			break;
		case R.id.ly_quick_option_album:
			onClickTweetPub(R.id.ly_quick_option_album);
			break;
		case R.id.ly_quick_option_photo:
			onClickTweetPub(R.id.ly_quick_option_photo);
			break;
		case R.id.ly_quick_option_voice:
			break;
		case R.id.ly_quick_option_scan:
			UIHelper.showScanActivity(getContext());
			break;
		case R.id.ly_quick_option_note:
			break;
		default:
			break;
		}
		if (mListener != null) {
			mListener.onQuickOptionClick(id);
		}
		dismiss();
	}
	
	private void onClickTweetPub(int id) {
		Bundle bundle = new Bundle();
		int type = -1;
		switch (id) {
		case R.id.ly_quick_option_album:
			type = TweetPubFragment.ACTION_TYPE_ALBUM;
			break;
		case R.id.ly_quick_option_photo:
			type = TweetPubFragment.ACTION_TYPE_PHOTO;
			break;
		default:
			break;
		}
		bundle.putInt(TweetPubFragment.ACTION_TYPE, type);
		UIHelper.showSimpleBack(getContext(), SimpleBackPage.TWEET_PUB, bundle);
	}
}
