package net.oschina.app.team.fragment;

import java.io.InputStream;
import android.os.Bundle;
import net.oschina.app.AppContext;
import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.team.adapter.TeamIssueAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamIssueList;
import net.oschina.app.team.bean.TeamProject;
import net.oschina.app.team.ui.TeamMainActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.XmlUtils;

/** 
 * 任务列表界面
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月14日 下午5:15:46 
 * 
 */

public class TeamIssueListFragment extends BaseListFragment {

	private String CACHE_KEY_PREFIX = "issue_list_";
	
	private Team mTeam;
	
	private TeamProject mTeamProject;
	
	private int mTeamId;
	
	private int mProjectId;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
        	Team team = (Team) bundle.getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
        	if (team != null) {
        		mTeam = team;
        		mTeamId = StringUtils.toInt(mTeam.getId());
        	}
        }
    }
	
    @Override
    protected ListBaseAdapter getListAdapter() {
        return new TeamIssueAdapter();
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mTeamId + "_" + mProjectId + "_" + mCurrentPage;
    }

    @Override
    protected ListEntity parseList(InputStream is) throws Exception {
    	TeamIssueList list = XmlUtils.toBean(TeamIssueList.class, is);
        return list;
    }

    @Override
    protected void sendRequestData() {
    	OSChinaTeamApi.getTeamIssueList(mTeamId, mProjectId, "", 0, "all", "", mCurrentPage, AppContext.PAGE_SIZE, mHandler);
    }
}
