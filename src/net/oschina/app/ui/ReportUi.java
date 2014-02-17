package net.oschina.app.ui;

import java.awt.event.KeyAdapter;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.AppManager;
import net.oschina.app.R;
import net.oschina.app.bean.Report;
import net.oschina.app.bean.Result;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
/**
 * 举报操作窗口
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @version 1.0
 * @created 2014-02-13
 */
public class ReportUi extends BaseActivity {
	private AppContext ac;
	private TextView mLink;
	private Spinner mReason;
	private TextView mOtherReason;

	private Button mPublish;
	private ImageButton mClose;
	private ProgressDialog mProgress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		this.initView();
		initData();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().finishActivity(this);
	}
	
	private void initView() {
		ac = (AppContext)getApplication();
		mLink = (TextView) findViewById(R.id.report_link);
		mReason = (Spinner) findViewById(R.id.report_reason);
		mOtherReason = (TextView) findViewById(R.id.report_other_reson);
		mPublish = (Button) findViewById(R.id.report_publish);
		mClose = (ImageButton) findViewById(R.id.report_close_button);
		
		mClose.setOnClickListener(UIHelper.finish(this));
		mReason.setOnItemSelectedListener(reasonListener);
		mPublish.setOnClickListener(publishListener);
		mOtherReason.addTextChangedListener(otherReasonListener);
	}
	
	private void initData() {
		Intent data = this.getIntent();
		mLink.setText(data.getStringExtra(Report.REPORT_LINK));
	}
	
	private OnItemSelectedListener reasonListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (position == 4) {
				mOtherReason.setVisibility(View.VISIBLE);
				mPublish.setTag(null);
			} else {
				mOtherReason.setVisibility(View.GONE);
				mPublish.setTag(1);
			}
		}
		public void onNothingSelected(AdapterView<?> parent) {
		}
	};
	
	private TextWatcher otherReasonListener = new TextWatcher() {
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		public void afterTextChanged(Editable s) {
			if (StringUtils.isEmpty(mOtherReason.getText() + "")) {
				mPublish.setTag(null);
			} else {
				mPublish.setTag(1);
			}
		}
	};
	
	private OnClickListener publishListener = new OnClickListener() {
		public void onClick(View v) {
			if (mPublish.getTag() == null) {
				return;
			}
			final Report report = new Report();
			report.setLinkAddress(mLink.getText() + "");
			report.setReportId(ac.getLoginUid());
			if (mReason.getSelectedItemPosition() == 4) {
				report.setReason(mOtherReason.getText() + "");
			} else {
				report.setReason(mReason.getSelectedItem().toString());
			}
			mProgress = ProgressDialog.show(v.getContext(), null, "举报信息发送中···",true,true); 			
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(mProgress!=null)mProgress.dismiss();
					if(msg.what == 1){
						Result res = (Result)msg.obj;
						UIHelper.ToastMessage(ReportUi.this, res.getErrorMessage());
						if(res.OK()){
							//发送通知广播
							if(res.getNotice() != null){
								UIHelper.sendBroadCast(ReportUi.this, res.getNotice());
							}
							finish();
						}
					}
					else {
						((AppException)msg.obj).makeToast(ReportUi.this);
					}
				}
			};
			new Thread(){
				public void run() {
					Message msg = new Message();
					Result res = new Result();
					try {
						res = ac.report(report);
						msg.what = 1;
						msg.obj = res;
		            } catch (AppException e) {
		            	e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
		            }
					handler.sendMessage(msg);
				}
			}.start();
		}
	};
}
