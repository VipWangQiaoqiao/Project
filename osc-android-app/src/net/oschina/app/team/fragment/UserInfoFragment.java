package net.oschina.app.team.fragment;

import java.io.InputStream;

import net.oschina.app.R;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.team.adapter.TeamMemberAdapter;
import net.oschina.app.team.bean.TeamMember;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.util.TLog;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 用户个人信息界面
 * 
 * @author kymjs
 * 
 */
public class UserInfoFragment extends BaseListFragment {

    TextView mTvName;
    TextView mTvUserName;
    TextView mTvEmail;
    TextView mTvJoinDate;
    TextView mTvAddress;

    private Activity aty;
    private TeamMember teamMember;
    private String teamId = "0";

    protected static final String TAG = UserInfoFragment.class.getSimpleName();
    private static final String CACHE_KEY_PREFIX = "DynamicFragment_list";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getBundleExtra(
                SimpleBackActivity.BUNDLE_KEY_ARGS);
        if (bundle != null) {
            teamMember = (TeamMember) bundle
                    .getSerializable(TeamMemberAdapter.TEAM_MEMBER_KEY);
            teamId = bundle.getString(TeamMemberAdapter.TEAM_ID_KEY);
        } else {
            teamMember = new TeamMember();
            TLog.log(TAG, "数据初始化异常");
        }
        aty = getActivity();
    }

    @Override
    public void initView(View view) {
        View headview = View.inflate(aty, R.layout.fragment_team_userinfo_head,
                null);
        mTvName = (TextView) headview.findViewById(R.id.fragment_team_name);
        mTvUserName = (TextView) headview
                .findViewById(R.id.fragment_team_username);
        mTvEmail = (TextView) headview.findViewById(R.id.fragment_team_email);
        mTvJoinDate = (TextView) headview
                .findViewById(R.id.fragment_team_joindate);
        mTvAddress = (TextView) headview
                .findViewById(R.id.fragment_team_address);
        mListView.addHeaderView(headview);

        mTvName.setText(teamMember.getName());
        mTvUserName.setText(teamMember.getOscName());
        mTvEmail.setText(teamMember.getTeamEmail());
        mTvJoinDate.setText(teamMember.getJoinTime());
        mTvAddress.setText(teamMember.getLocation());
    }

    @Override
    protected ListBaseAdapter getListAdapter() {
        return null;
    }

    @Override
    protected String getCacheKeyPrefix() {
        return CACHE_KEY_PREFIX + "_" + mCurrentPage;
    }

    @Override
    protected ListEntity parseList(InputStream is) throws Exception {
        return null;
    }

    @Override
    protected void sendRequestData() {}
}
