package net.oschina.app.team.ui;

import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.team.bean.Team;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


/**
 * 团队新动态
 * TeamNewActiveFragment.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 *
 * @data 2015-3-6 下午6:39:53
 */
public class TeamNewActiveActivity extends BaseActivity {
    
    private Team mTeam;
    
    private MenuItem mMenuSend;
    
    @Override
    protected int getLayoutId() {
        // TODO Auto-generated method stub
        return R.layout.activity_team_active_pub;
    }
    
    @Override
    protected boolean hasBackButton() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void initView() {
	// TODO Auto-generated method stub
	setActionBarTitle(R.string.team_new_active);
    }

    @Override
    public void initData() {
	// TODO Auto-generated method stub
	//mTeam = (Team) getIntent().getExtras().getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
	getMenuInflater().inflate(R.menu.pub_new_team_active_menu, menu);
	mMenuSend = menu.findItem(R.id.public_menu_send);
        return super.onCreateOptionsMenu(menu);
    }
    
}

