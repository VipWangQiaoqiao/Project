package net.oschina.app.ui;

import net.oschina.app.R;
import net.oschina.app.bean.Constants;
import net.oschina.app.interf.BaseViewInterface;
import net.oschina.app.service.NoticeUtils;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;
import net.oschina.app.widget.BadgeView;
import net.oschina.app.widget.MyFragmentTabHost;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

@SuppressLint("InflateParams")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks,
		OnTabChangeListener, BaseViewInterface, View.OnClickListener {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	@InjectView(android.R.id.tabhost)
	MyFragmentTabHost mTabHost;

	// private Version mVersion;
	private BadgeView mBvTweet;

	private BroadcastReceiver mNoticeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			int atmeCount = intent.getIntExtra("atmeCount", 0);// @我
			int msgCount = intent.getIntExtra("msgCount", 0);// 留言
			int reviewCount = intent.getIntExtra("reviewCount", 0);// 评论
			int newFansCount = intent.getIntExtra("newFansCount", 0);// 新粉丝
			int activeCount = atmeCount + reviewCount + msgCount;// +
																	// newFansCount;//
																	// 信息总数

			TLog.log("@me:" + atmeCount + " msg:" + msgCount + " review:"
					+ reviewCount + " newFans:" + newFansCount + " active:"
					+ activeCount);

			if (activeCount > 0) {
				mBvTweet.setText(activeCount + "");
				mBvTweet.show();
			} else {
				mBvTweet.hide();
			}
		}
	};

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@InjectView(R.id.quick_option_iv)
	View mAddBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		initView();
	}

	@Override
	public void initView() {
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		if (android.os.Build.VERSION.SDK_INT > 10) {
			mTabHost.getTabWidget().setShowDividers(0);
		}

		initTabs();

		// 中间按键图片触发
		mAddBt.setOnClickListener(this);

		mTabHost.setCurrentTab(0);
		mTabHost.setOnTabChangedListener(this);

		IntentFilter filter = new IntentFilter(Constants.INTENT_ACTION_NOTICE);
		registerReceiver(mNoticeReceiver, filter);

		NoticeUtils.bindToService(this);
		UIHelper.sendBroadcastForNotice(this);
	}

	@Override
	protected void onDestroy() {
		NoticeUtils.unbindFromService(this);
		unregisterReceiver(mNoticeReceiver);
		mNoticeReceiver = null;
		NoticeUtils.tryToShutDown(this);
		super.onDestroy();
	}

	@Override
	public void initData() {

	}

	private void initTabs() {
		MainTab[] tabs = MainTab.values();
		final int size = tabs.length;
		for (int i = 0; i < size; i++) {
			MainTab mainTab = tabs[i];
			TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));
			View indicator = LayoutInflater.from(
					getApplicationContext()).inflate(R.layout.tab_indicator,
					null);
			TextView title = (TextView) indicator.findViewById(R.id.tab_title);
			Drawable drawable = this.getResources().getDrawable(
					mainTab.getResIcon());
			title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
			if (i == 2) {
				indicator.setVisibility(View.INVISIBLE);
				mTabHost.setNoTabChangedTag(getString(mainTab.getResName()));
			}
			title.setText(getString(mainTab.getResName()));
			tab.setIndicator(indicator);
			tab.setContent(new TabContentFactory() {

				@Override
				public View createTabContent(String tag) {
					return new View(MainActivity.this);
				}
			});

			mTabHost.addTab(tab, mainTab.getClz(), null);

			if (mainTab.equals(MainTab.ME)) {
				View cn = indicator.findViewById(R.id.tab_mes);
				mBvTweet = new BadgeView(MainActivity.this, cn);
				mBvTweet.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
				mBvTweet.setBackgroundResource(R.drawable.notification_bg);
				mBvTweet.setGravity(Gravity.CENTER);
				mBvTweet.setTextColor(Color.WHITE);
			}
		}
	}	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getBooleanExtra("NOTICE", false)) {
			mTabHost.setCurrentTab(3);
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabChanged(String tabId) {
		final int size = mTabHost.getTabWidget().getTabCount();
		for (int i = 0; i < size; i++) {
			View v = mTabHost.getTabWidget().getChildAt(i);
			if (i == mTabHost.getCurrentTab()) {
				v.setSelected(true);
			} else {
				v.setSelected(false);
			}
		}
		supportInvalidateOptionsMenu();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		// 点击了快速操作按钮
		case R.id.quick_option_iv:
			showQuickOption();
			break;

		default:
			break;
		}
	}

	// 显示快速操作界面
	private void showQuickOption() {
		final QuickOptionDialog dialog = new QuickOptionDialog(
				MainActivity.this);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		// Intent intent = new Intent(MainActivity.this,
		// QuickOptionActivity.class);
		// startActivity(intent);
	}
}
