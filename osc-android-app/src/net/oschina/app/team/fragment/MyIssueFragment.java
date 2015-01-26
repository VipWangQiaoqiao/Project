package net.oschina.app.team.fragment;

import java.io.ByteArrayInputStream;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.fragment.MyInformationFragment;
import net.oschina.app.team.bean.MyIssueState;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamList;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.SystemTool;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Team任务界面
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class MyIssueFragment extends BaseFragment {

    @InjectView(R.id.team_myissue_ing)
    RelativeLayout mRlIng;
    @InjectView(R.id.team_myissue_outdate)
    RelativeLayout mRlWill;
    @InjectView(R.id.team_myissue_ed)
    RelativeLayout mRlEd;
    @InjectView(R.id.team_myissue_all)
    RelativeLayout mRlAll;
    @InjectView(R.id.myissue_title)
    LinearLayout mLlTitle;

    @InjectView(R.id.team_myissue_ing_num)
    TextView mTvIng;
    @InjectView(R.id.team_myissue_outdate_num)
    TextView mTvOutdate;
    @InjectView(R.id.team_myissue_ed_num)
    TextView mTvEd;
    @InjectView(R.id.team_myissue_all_num)
    TextView mTvAll;

    @InjectView(R.id.team_myissue_name)
    TextView mTvName;
    @InjectView(R.id.team_myissue_date)
    TextView mTvDate;

    private Team team;
    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getActivity().getIntent().getExtras();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_team_issue,
                container, false);
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void initView(View view) {
        mRlIng.setOnClickListener(this);
        mRlWill.setOnClickListener(this);
        mRlEd.setOnClickListener(this);
        mRlAll.setOnClickListener(this);

        mTvName.setText(AppContext.getInstance().getLoginUser().getName()
                + "的任务");
        mTvDate.setText(SystemTool.getDataTime("yyyy年MM月dd日"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.team_myissue_ing:
            bundle.putString(MyIssueDetail.MY_ISSUEDETAIL_KEY, "opened");
            UIHelper.showSimpleBack(getActivity(),
                    SimpleBackPage.MY_ISSUE_PAGER, bundle);
            break;
        case R.id.team_myissue_ed:
            bundle.putString(MyIssueDetail.MY_ISSUEDETAIL_KEY, "closed");
            UIHelper.showSimpleBack(getActivity(),
                    SimpleBackPage.MY_ISSUE_PAGER, bundle);
            break;
        case R.id.team_myissue_outdate:
            bundle.putString(MyIssueDetail.MY_ISSUEDETAIL_KEY, "outdate");
            UIHelper.showSimpleBack(getActivity(),
                    SimpleBackPage.MY_ISSUE_PAGER, bundle);
            break;
        case R.id.team_myissue_all:
            bundle.putString(MyIssueDetail.MY_ISSUEDETAIL_KEY, "all");
            UIHelper.showSimpleBack(getActivity(),
                    SimpleBackPage.MY_ISSUE_PAGER, bundle);
            break;
        }
    }

    @Override
    public void initData() {
        OSChinaApi.getMyIssueState(team.getId() + "", AppContext.getInstance()
                .getLoginUid() + "", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                MyIssueState data = XmlUtils.toBean(MyIssueState.class,
                        new ByteArrayInputStream(arg2));
                mTvIng.setText(data.getOpened());
                mTvOutdate.setText(data.getOutdate());
                mTvEd.setText(data.getClosed());
                mTvAll.setText(data.getAll());
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {}
        });
    }
}
