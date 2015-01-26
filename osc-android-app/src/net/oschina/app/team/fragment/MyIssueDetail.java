package net.oschina.app.team.fragment;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.fragment.MyInformationFragment;
import net.oschina.app.team.adapter.TeamIssueAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamIssue;
import net.oschina.app.team.bean.TeamIssueList;
import net.oschina.app.team.bean.TeamList;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.util.TLog;
import net.oschina.app.util.XmlUtils;

import org.kymjs.kjframe.utils.PreferenceHelper;

import android.os.Bundle;

public class MyIssueDetail extends BaseListFragment {

    protected static final String TAG = TeamIssueFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "my_issue_";

    public static final String MY_ISSUEDETAIL_KEY = "MyIssueDetail";

    private Team mTeam;
    private String type = "all";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getBundleExtra(
                SimpleBackActivity.BUNDLE_KEY_ARGS);
        if (bundle != null) {
            type = bundle.getString(MY_ISSUEDETAIL_KEY);
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
    protected ListBaseAdapter getListAdapter() {
        return new TeamIssueAdapter();
    }

    /**
     * 获取当前展示页面的缓存数据
     */
    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + AppContext.getInstance().getLoginUid() + "_"
                + mTeam.getId() + mCurrentPage;
    }

    @Override
    protected ListEntity<TeamIssue> parseList(InputStream is) throws Exception {
        TeamIssueList<TeamIssue> list = XmlUtils
                .toBean(TeamIssueList.class, is);
        return list;
    }

    @Override
    protected ListEntity<TeamIssue> readList(Serializable seri) {
        return ((TeamIssueList) seri);
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.getMyIssue(mTeam.getId() + "", AppContext.getInstance()
                .getLoginUid() + "", mCurrentPage, type, mHandler);
    }
}
