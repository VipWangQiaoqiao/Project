package net.oschina.app.team.fragment;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.fragment.MyInformationFragment;
import net.oschina.app.team.adapter.DynamicAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamActive;
import net.oschina.app.team.bean.TeamActives;
import net.oschina.app.team.bean.TeamList;
import net.oschina.app.ui.DetailActivity;
import net.oschina.app.util.TLog;
import net.oschina.app.util.XmlUtils;

import org.kymjs.kjframe.utils.PreferenceHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;

/**
 * Team动态界面
 * 
 * @author kymjs (kymjs123@gmail.com)
 * 
 */
public class DynamicFragment extends BaseListFragment<TeamActive> {

    public final static String BUNDLE_KEY_UID = "UID";

    public static final String DYNAMIC_FRAGMENT_KEY = "DynamicFragment";
    public static final String DYNAMIC_FRAGMENT_TEAM_KEY = "DynamicFragment_teamid";
    protected static final String TAG = DynamicFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "DynamicFragment_list";

    private Activity aty;
    private Team team;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            int index = bundle.getInt(MyInformationFragment.TEAM_LIST_KEY, 0);
            String cache = PreferenceHelper.readString(getActivity(),
                    MyInformationFragment.TEAM_LIST_FILE,
                    MyInformationFragment.TEAM_LIST_KEY);
            List<Team> teams = TeamList.toTeamList(cache);
            if (teams.size() > index) {
                team = teams.get(index);
            }
        }
        if (team == null) {
            team = new Team();
            TLog.log(getClass().getSimpleName(), "team对象初始化异常");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        aty = getActivity();
        return view;
    }

    @Override
    protected DynamicAdapter getListAdapter() {
        return new DynamicAdapter(aty);
    }

    @Override
    protected String getCacheKeyPrefix() {
        String str = CACHE_KEY_PREFIX + "_" + team.getId() + "_" + mCurrentPage;
        return str;
    }

    @Override
    protected TeamActives parseList(InputStream is) throws Exception {
        TeamActives list = XmlUtils.toBean(TeamActives.class, is);
        if (list.getList() == null) {
            list.setActives(new ArrayList<TeamActive>());
        }
        return list;
    }

    @Override
    protected TeamActives readList(Serializable seri) {
        return (TeamActives) seri;
    }

    @Override
    protected void sendRequestData() {
        OSChinaApi.teamDynamic(team, mCurrentPage, mHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Adapter adapter = parent.getAdapter();
        if (adapter != null && adapter instanceof DynamicAdapter) {
            Intent intent = new Intent(aty, DetailActivity.class);
            TeamActive data = ((DynamicAdapter) parent.getAdapter())
                    .getItem(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable(DYNAMIC_FRAGMENT_KEY, data);
            bundle.putInt(DYNAMIC_FRAGMENT_TEAM_KEY, team.getId());
            // UIHelper.showSimpleBack(aty, SimpleBackPage.DYNAMIC_DETAIL,
            // bundle);
            bundle.putInt(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                    DetailActivity.DISPLAY_TEAM_TWEET_DETAIL);
            intent.putExtras(bundle);
            aty.startActivity(intent);
        }
    }
}
