package net.oschina.app.team.fragment;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.fragment.MyInformationFragment;
import net.oschina.app.team.adapter.TeamIssueAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamIssue;
import net.oschina.app.team.bean.TeamIssueList;
import net.oschina.app.team.bean.TeamList;
import net.oschina.app.team.viewpagefragment.MyIssuePagerfragment;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.util.TLog;
import net.oschina.app.util.XmlUtils;

import org.kymjs.kjframe.utils.PreferenceHelper;

import android.os.Bundle;

/**
 * 我的任务列表界面
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class MyIssueDetail extends BaseListFragment<TeamIssue> {

    protected static final String TAG = TeamIssueFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "my_issue_";

    private Team mTeam;
    private String type = "all";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString(MyIssuePagerfragment.MY_ISSUEDETAIL_KEY,
                    "all");
            bundle = bundle.getBundle(SimpleBackActivity.BUNDLE_KEY_ARGS);
        }
        if (bundle != null) {
            int index = bundle.getInt(MyInformationFragment.TEAM_LIST_KEY, 0);
            String cache = PreferenceHelper.readString(getActivity(),
                    MyInformationFragment.TEAM_LIST_FILE,
                    MyInformationFragment.TEAM_LIST_KEY);
            List<Team> teams = TeamList.toTeamList(cache);
            if (teams.size() > index) {
                mTeam = teams.get(index);
            }
        }
        if (mTeam == null) {
            mTeam = new Team();
            TLog.log(getClass().getSimpleName(), "team对象初始化异常");
        }
    }

    @Override
    protected TeamIssueAdapter getListAdapter() {
        return new TeamIssueAdapter();
    }

    /**
     * 获取当前展示页面的缓存数据
     */
    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + AppContext.getInstance().getLoginUid() + "_"
                + mTeam.getId() + mCurrentPage + type;
    }

    @Override
    protected TeamIssueList parseList(InputStream is) throws Exception {
        TeamIssueList list = XmlUtils.toBean(TeamIssueList.class, is);
        return list;
    }

    @Override
    protected TeamIssueList readList(Serializable seri) {
        return ((TeamIssueList) seri);
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getMyIssue(mTeam.getId() + "", AppContext.getInstance()
                .getLoginUid() + "", mCurrentPage, type, mHandler);
    }
}
