package net.oschina.app.viewpagefragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.Constants;
import net.oschina.app.bean.Notice;
import net.oschina.app.fragment.ActiveFragment;
import net.oschina.app.fragment.MessageFragment;
import net.oschina.app.ui.MainActivity;
import net.oschina.app.util.TLog;
import net.oschina.app.widget.BadgeView;

/**
 * 个人主页页面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午2:21:52
 * 
 */
public class ActiveViewPagerFragment extends BaseViewPagerFragment {

	public static BadgeView mBvAtMe, mBvComment, mBvMsg;
	
	private BroadcastReceiver mNoticeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			setNotice();
		}
	};

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

			if (atmeCount > 0) {
				mBvAtMe.setText(atmeCount + "");
				mBvAtMe.show();
			} else {
				mBvAtMe.hide();
			}

			if (reviewCount > 0) {
				mBvComment.setText(reviewCount + "");
				mBvComment.show();
			} else {
				mBvComment.hide();
			}

			if (msgCount > 0) {
				mBvMsg.setText(msgCount + "");
				mBvMsg.show();
			} else {
				mBvMsg.hide();
			}
		} else {
			mBvAtMe.hide();
			mBvComment.hide();
			mBvMsg.hide();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_NOTICE);
		getActivity().registerReceiver(mNoticeReceiver, filter);
		mBvAtMe = new BadgeView(getActivity(), mTabStrip.getBadgeView(0));
		mBvAtMe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		mBvAtMe.setBadgePosition(BadgeView.POSITION_CENTER);
		mBvAtMe.setGravity(Gravity.CENTER);
		mBvAtMe.setBackgroundResource(R.drawable.notification_bg);

		mBvComment = new BadgeView(getActivity(), mTabStrip.getBadgeView(1));
		mBvComment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		mBvComment.setBadgePosition(BadgeView.POSITION_CENTER);
		mBvComment.setGravity(Gravity.CENTER);
		mBvComment.setBackgroundResource(R.drawable.notification_bg);

		mBvMsg = new BadgeView(getActivity(), mTabStrip.getBadgeView(2));
		mBvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
		mBvMsg.setBadgePosition(BadgeView.POSITION_CENTER);
		mBvMsg.setGravity(Gravity.CENTER);
		mBvMsg.setBackgroundResource(R.drawable.notification_bg);

		mTabStrip.getBadgeView(0).setVisibility(View.VISIBLE);

		mTabStrip.getBadgeView(1).setVisibility(View.VISIBLE);
		mTabStrip.getBadgeView(2).setVisibility(View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mNoticeReceiver);
		mNoticeReceiver = null;
	}

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(
				R.array.mymes_viewpage_arrays);
		adapter.addTab(title[0], "active_me", ActiveFragment.class,
				getBundle(ActiveList.CATALOG_ATME));
		adapter.addTab(title[1], "active_comment", ActiveFragment.class,
				getBundle(ActiveList.CATALOG_COMMENT));
		adapter.addTab(title[2], "active_mes", MessageFragment.class, null);
	}

	private Bundle getBundle(int catalog) {
		Bundle bundle = new Bundle();
		bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, catalog);
		return bundle;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void initView(View view) {

	}

	@Override
	public void initData() {

	}
}
