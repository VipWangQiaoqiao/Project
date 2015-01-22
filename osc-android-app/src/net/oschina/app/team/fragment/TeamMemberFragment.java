package net.oschina.app.team.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.fragment.MyInformationFragment;
import net.oschina.app.team.adapter.TeamMemberAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamList;
import net.oschina.app.team.bean.TeamMember;
import net.oschina.app.team.bean.TeamMembers;
import net.oschina.app.util.TLog;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.PreferenceHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 团队成员界面
 * 
 * @author kymjs (kymjs123@gmail.com)
 * 
 */
public class TeamMemberFragment extends BaseFragment {

    @InjectView(R.id.fragment_team_grid)
    GridView mGrid;

    private Activity aty;
    private Team team;
    private List<TeamMember> datas = null;

    public static final String TEAM_MEMBER_FILE = "TeamMemberFragment_cache_file";
    public static final String TEAM_MEMBER_KEY = "TeamMemberFragment_key";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            int index = bundle.getInt(MyInformationFragment.TEAM_LIST_KEY, 0);
            String cache = PreferenceHelper.readString(getActivity(),
                    MyInformationFragment.TEAM_LIST_FILE,
                    MyInformationFragment.TEAM_LIST_KEY);
            team = TeamList.toTeamList(cache).get(index);
        } else {
            team = new Team();
            TLog.log(getClass().getSimpleName(), "team对象初始化异常");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_team_member,
                container, false);
        aty = getActivity();
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void initView(View view) {
        mGrid.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ((TeamMemberAdapter) parent.getAdapter()).onItemClick(position);
            }
        });
    }

    @Override
    public void initData() {
        OSChinaApi.getTeamMemberList(team.getId(),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        InputStream is = new ByteArrayInputStream(arg2);
                        datas = XmlUtils.toBean(TeamMembers.class, is)
                                .getList();
                        mGrid.setAdapter(new TeamMemberAdapter(aty, datas, team));
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                            Throwable arg3) {
                        AppContext.showToast("成员信息获取失败");
                    }
                });
    }
}
