package net.oschina.app.ui;

import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.interf.BaseViewInterface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.widget.DrawerLayout;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks, OnTabChangeListener, BaseViewInterface {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	
	private FragmentTabHost mTabHost;
	
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
		
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		if (android.os.Build.VERSION.SDK_INT > 10) {
			mTabHost.getTabWidget().setShowDividers(0);
		}

		initTabs();

		mTabHost.setCurrentTab(0);
		mTabHost.setOnTabChangedListener(this);
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
			
			View indicator = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tab_indicator, null);
			ImageView icon = (ImageView) indicator.findViewById(R.id.tab_icon);
			icon.setImageResource(mainTab.getResIcon());
			TextView title = (TextView) indicator.findViewById(R.id.tab_titile);
			title.setText(getString(mainTab.getResName()));
			tab.setIndicator(indicator);
			tab.setContent(new TabContentFactory() {

				@Override
				public View createTabContent(String tag) {
					return new View(MainActivity.this);
				}
			});

			mTabHost.addTab(tab, mainTab.getClz(), null);
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabChanged(String arg0) {
		
	}

}
