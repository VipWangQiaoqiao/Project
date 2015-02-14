package net.oschina.app.team.ui;

import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.fragment.TeamDiaryPagerFragment;
import net.oschina.app.team.fragment.TeamDiscussFragment;
import net.oschina.app.team.viewpagefragment.TeamIssueViewPageFragment;
import net.oschina.app.team.viewpagefragment.TeamMainViewPagerFragment;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 某个团队主界面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月13日 下午3:36:56
 * 
 *          功能需求：动态切换团队的主页、任务、讨论、周报
 * 
 */

public class TeamMainActivity extends BaseActivity {

    public final static String BUNDLE_KEY_TEAM = "bundle_key_team";

    public final static String BUNDLE_KEY_PROJECT = "bundle_key_project";

    public final static String BUNDLE_KEY_ISSUE_CATALOG = "bundle_key_catalog_list";

    private FragmentManager mFragmentManager;

    static final String CONTENTS[] = { "main", "issue", "discuss", "diary" };

    static final String fragments[] = {
	    TeamMainViewPagerFragment.class.getName(),
	    TeamIssueViewPageFragment.class.getName(),
	    TeamDiscussFragment.class.getName(),
	    TeamDiaryPagerFragment.class.getName() };

    private int mCurrentContentIndex = -1;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.team_menu_item_main)
    View mMenuMain;
    @InjectView(R.id.team_menu_item_issue)
    View mMenuIssue;
    @InjectView(R.id.team_menu_item_discuss)
    View mMenuDiscuss;
    @InjectView(R.id.team_menu_item_diary)
    View mMenuDiary;

    @Override
    protected boolean hasBackButton() {
	return true;
    }

    @Override
    protected int getLayoutId() {
	return R.layout.activity_team_main;
    }

    @Override
    @OnClick({ R.id.team_menu_item_main, R.id.team_menu_item_issue,
	    R.id.team_menu_item_discuss, R.id.team_menu_item_diary,
	    R.id.team_main_menu })
    public void onClick(View v) {
	mDrawerLayout.closeDrawers();
	switch (v.getId()) {
	case R.id.team_menu_item_main:
	    switchContent(0);
	    break;
	case R.id.team_menu_item_issue:
	    switchContent(1);
	    break;
	case R.id.team_menu_item_discuss:
	    switchContent(2);
	    break;
	case R.id.team_menu_item_diary:
	    switchContent(3);
	    break;
	default:
	    break;
	}
    }

    @Override
    protected boolean haveSpinner() {
	return false;
    }

    @Override
    public void initView() {
	ButterKnife.inject(this);

	mFragmentManager = getSupportFragmentManager();
	switchContent(0);
    }

    private Team mTeam;

    @Override
    public void initData() {
	Intent intent = getIntent();
	if (intent != null) {
	    mTeam = (Team) intent.getSerializableExtra(BUNDLE_KEY_TEAM);
	    if (mTeam != null) {

		setActionBarTitle(mTeam.getName());
	    }
	}
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
}
