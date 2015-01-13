package net.oschina.app.team.ui;

import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.fragment.TweetsFragment;
import net.oschina.app.viewpagefragment.NewsViewPagerFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

/**
 * 某个团队主界面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月13日 下午3:36:56
 * 
 *          功能需求：动态切换团队的主页、任务、讨论、周报
 * 
 */

public class TeamMainActivity extends BaseActivity implements OnNavigationListener {

	private FragmentManager mFragmentManager;

	static final String CONTENTS[] = { "任务", "讨论" };

	static final String fragments[] = { NewsViewPagerFragment.class.getName(),
			TweetsFragment.class.getName() };

	final String TITLES[] = { "任务", "讨论" };

	private int mCurrentContentIndex = -1;

	@Override
	protected boolean hasBackButton() {
		return true;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_team_main;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void initView() {
		mFragmentManager = getSupportFragmentManager();
		switchContent(0);
		SpinnerAdapter adapter = ArrayAdapter.createFromResource(this,
				R.array.avatar_option, android.R.layout.simple_spinner_dropdown_item);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActionBar.setListNavigationCallbacks(adapter, this);
	}

	@Override
	public void initData() {

	}

	/**
	 * 
	 * @param pos
	 */
	private void switchContent(int pos) {
		String tag = CONTENTS[pos];
		String mCurrentContentTag = CONTENTS[pos];
		if (pos == mCurrentContentIndex)
			return;

		FragmentTransaction ft = mFragmentManager.beginTransaction();
		if (mCurrentContentTag != null) {
			Fragment fragment = mFragmentManager
					.findFragmentByTag(mCurrentContentTag);
			if (fragment != null) {
				ft.remove(fragment);
			}
		}
		ft.replace(R.id.main_content,
				Fragment.instantiate(this, fragments[pos]), tag);
		ft.commit();

		mCurrentContentIndex = pos;

	}

	@Override
	public boolean onNavigationItemSelected(int arg0, long arg1) {
		switchContent(arg0);
		return false;
	}
}
