package net.oschina.app.fragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.Notice;
import net.oschina.app.ui.MainActivity;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.BadgeView;

/** 
 * 发现页面
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年11月4日 下午3:34:07 
 * 
 */

public class ExploreFragment extends BaseFragment {
	
	@InjectView(R.id.rl_active)View mRlActive;
	
	@InjectView(R.id.tv_mes) View mMesView;
	
	private static BadgeView mMesCount; 
	
	private BroadcastReceiver mNoticeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			TLog.log("NOTICE", "动态收到广播");
			setNotice();
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_explore, null);
		ButterKnife.inject(this, view);
		initView(view);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_NOTICE);
		getActivity().registerReceiver(mNoticeReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mNoticeReceiver);
		mNoticeReceiver = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		setNotice();
	}
	
	private void setNotice() {
		if (MainActivity.mNotice != null) {

			Notice notice = MainActivity.mNotice;
			int atmeCount = notice.getAtmeCount();// @我
			int msgCount = notice.getMsgCount();// 留言
			int reviewCount = notice.getReviewCount();// 评论
			int newFansCount = notice.getNewFansCount();// 新粉丝
			int activeCount = atmeCount + reviewCount + msgCount + newFansCount;// 信息总数

			if (activeCount > 0) {
				mMesCount.setText(activeCount + "");
				mMesCount.show();
			} else {
				mMesCount.hide();
			}

		} else {
			mMesCount.hide();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.rl_active:
			UIHelper.showMyActive(getActivity());
			break;
		default:
			break;
		}
	}

	@Override
	public void initView(View view) {
		mRlActive.setOnClickListener(this);
		
		mMesCount = new BadgeView(getActivity(), mMesView);
		mMesCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		mMesCount.setBadgePosition(BadgeView.POSITION_CENTER);
		mMesCount.setBackgroundResource(R.drawable.notification_bg);
	}

	@Override
	public void initData() {

	}

}
