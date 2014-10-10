package net.oschina.app.ui;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.UserInformation;
import net.oschina.app.util.UIHelper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 侧滑菜单界面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午6:00:05
 * 
 */
public class NavigationDrawerFragment extends BaseFragment implements
		OnClickListener {
	
	public static final String INTENT_ACTION_USER_CHANGE = "INTENT_ACTION_USER_CHANGE";
	
	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private View mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;
	
	private BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//接收到变化后，更新用户资料
			setupUserView(true);
		}
	};

	@InjectView(R.id.menu_item_userinfo)
	View mMenu_item_userinfo;
	
	@InjectView(R.id.menu_user_info_layout)
	View mUser_info_layout;
	
	@InjectView(R.id.menu_user_info_login_tips_layout)
	View mUser_login_tips;
	
	@InjectView(R.id.menu_user_info_userface)
	ImageView mUser_face;
	
	@InjectView(R.id.menu_user_info_username)
	TextView mUser_name;
	
	@InjectView(R.id.menu_user_info_gender)
	ImageView mUser_gender;

	@InjectView(R.id.menu_item_team)
	View mMenu_item_team;

	@InjectView(R.id.menu_item_opensoft)
	View mMenu_item_opensoft;

	@InjectView(R.id.menu_item_note)
	View mMenu_item_note;

	@InjectView(R.id.menu_item_bookmarks)
	View mMenu_item_bookmarks;

	@InjectView(R.id.menu_item_setting)
	View mMenu_item_setting;

	@InjectView(R.id.menu_item_exit)
	View mMenu_item_exit;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}

		selectItem(mCurrentSelectedPosition);
		
		IntentFilter filter = new IntentFilter(INTENT_ACTION_USER_CHANGE);
		getActivity().registerReceiver(mUserChangeReceiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ButterKnife.reset(this);
		// 注销用户变化监听广播
		try {
			getActivity().unregisterReceiver(mUserChangeReceiver);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDrawerListView = inflater.inflate(R.layout.fragment_navigation_drawer,
				container, false);
		mDrawerListView.setOnClickListener(this);
		ButterKnife.inject(this, mDrawerListView);
		initView(mDrawerListView);
		initData();
		return mDrawerListView;
	}

	@Override
	public void onClick(View v) {
		int id =  v.getId();
		switch (id) {
		case R.id.menu_item_userinfo:
			onClickMenuItemUserInfo();
			break;
		case R.id.menu_item_team:
			break;
		case R.id.menu_item_opensoft:
			break;
		case R.id.menu_item_note:
			break;
		case R.id.menu_item_bookmarks:
			break;
		case R.id.menu_item_setting:
			break;
		case R.id.menu_item_exit:
			break;
		default:
			break;
		}
	}
	
	private void onClickMenuItemUserInfo() {
//		if (!AppContext.getInstance().isLogin()) {
//			UIHelper.showLoginActivity(getActivity());
//		} else {
//			AppContext.showToast("已经登录了");
//		}
		UIHelper.showLoginActivity(getActivity());
	}

	public void initView(View view) {

		mMenu_item_userinfo.setOnClickListener(this);
		mMenu_item_team.setOnClickListener(this);
		mMenu_item_opensoft.setOnClickListener(this);
		mMenu_item_note.setOnClickListener(this);
		mMenu_item_bookmarks.setOnClickListener(this);

		mMenu_item_setting.setOnClickListener(this);
		mMenu_item_exit.setOnClickListener(this);
	}

	public void initData() {
		setupUserView(AppContext.getInstance().isLogin());
	}
	
	private void setupUserView(final boolean reflash) {
		//判断是否已经登录，如果已登录则显示用户的头像与信息
		if(!AppContext.getInstance().isLogin()) {
			mUser_face.setImageResource(R.drawable.ic_launcher);
			mUser_name.setText("");
			mUser_info_layout.setVisibility(View.GONE);
			mUser_login_tips.setVisibility(View.VISIBLE);
			return;
		}
		
		mUser_info_layout.setVisibility(View.VISIBLE);
		mUser_login_tips.setVisibility(View.GONE);
		
		UserInformation user = AppContext.getInstance().getLoginUser();
		mUser_name.setText(user.getName());
		ImageLoader.getInstance().displayImage(user.getPortrait(), mUser_face);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!mUserLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.apply();
				}

				getActivity().supportInvalidateOptionsMenu(); // calls
																// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});

		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	public static interface NavigationDrawerCallbacks {
		void onNavigationDrawerItemSelected(int position);
	}
}