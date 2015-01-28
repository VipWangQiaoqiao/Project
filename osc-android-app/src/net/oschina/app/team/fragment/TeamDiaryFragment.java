package net.oschina.app.team.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.team.adapter.TeamDiaryAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamDiary;
import net.oschina.app.team.bean.TeamDiaryList;
import net.oschina.app.team.ui.TeamMainActivity;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.XmlUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

/**
 * team周报列表界面
 * 
 * @author fireant(http://my.oschina.net/u/253900)
 * 
 */
public class TeamDiaryFragment extends BaseListFragment<TeamDiary> {

    protected static final String TAG = TeamDiaryFragment.class
            .getSimpleName();
    private static final String CACHE_KEY_PREFIX = "team_diary_list_";

    private Team mTeam;

    private int mTeamId;
    
    private int year;
    
    private int week;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            Team team = (Team) bundle
                    .getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
            if (team != null) {
                mTeam = team;
                mTeamId = StringUtils.toInt(mTeam.getId());
            }
        }
    }

    @Override
    protected TeamDiaryAdapter getListAdapter() {
        return new TeamDiaryAdapter();
    }

    /**
     * 获取当前展示页面的缓存数据
     */
    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + mTeamId + "_" + mCurrentPage;
    }

    @Override
    protected TeamDiaryList parseList(InputStream is)
            throws Exception {
        TeamDiaryList list = XmlUtils.toBean(
        		TeamDiaryList.class, is);
        return list;
    }

    @Override
    protected TeamDiaryList readList(Serializable seri) {
        return ((TeamDiaryList) seri);
    }

    @Override
    protected void sendRequestData() {
        OSChinaTeamApi.getTeamDiaryList(0, mTeamId, 2015, 2, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {

    }

}