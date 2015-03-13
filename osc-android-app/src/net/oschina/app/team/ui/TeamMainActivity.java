package net.oschina.app.team.ui;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.fragment.MyInformationFragment;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamList;
import net.oschina.app.team.fragment.TeamDiaryPagerFragment;
import net.oschina.app.team.fragment.TeamDiscussFragment;
import net.oschina.app.team.viewpagefragment.TeamIssueViewPageFragment;
import net.oschina.app.team.viewpagefragment.TeamMainViewPagerFragment;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 某个团队主界面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月13日 下午3:36:56
 * 
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
    
    @InjectView(R.id.error_layout)
    EmptyLayout mErrorLayout;
    
    @Override
    protected boolean hasBackButton() {
	return true;
    }

    @Override
    protected int getLayoutId() {
	return R.layout.activity_team_main;
    }

    @Override
    public void onClick(View v) {
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
	return true;
    }

    @Override
    public void initView() {
	ButterKnife.inject(this);
	mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
	mErrorLayout.setErrorMessage("获取团队中...");
	mErrorLayout.setOnClickListener(new View.OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		
	    }
	});
	initSpinner();
	requestTeamList();
	
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
    
    private Spinner mSpinner;
    private SpinnerAdapter adapter;
    
    private List<String> teamName = new ArrayList<String>();
    private List<Team> teamDatas = new ArrayList<Team>();
    
    private void initSpinner() {
	mSpinner = getSpinner();
	adapter = new SpinnerAdapter(this, teamName);
	mSpinner.setAdapter(adapter);
	mSpinner.setVisibility(View.GONE);
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
    
    private void requestTeamList() {
	// 初始化团队列表数据
        String cache = PreferenceHelper.readString(this,
                MyInformationFragment.TEAM_LIST_FILE,
                MyInformationFragment.TEAM_LIST_KEY);
        if (!StringUtils.isEmpty(cache)) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            teamDatas = TeamList.toTeamList(cache);
            setTeamDataState();
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            
            OSChinaApi.teamList(new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                    TeamList datas = XmlUtils.toBean(TeamList.class, arg2);
                    teamDatas.clear();
                    teamDatas.addAll(datas.getList());
                    setTeamDataState();
                }

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                        Throwable arg3) {
                    AppContext.showToast("网络不好，请稍后重试");
                }
            });
        }
        
    }
    
    private void setTeamDataState() {
	if (teamDatas.isEmpty()) {
	    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
	    String msg = getResources().getString(R.string.team_empty);
	    mErrorLayout.setErrorMessage(msg);
	} else {
	    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
	    mSpinner.setVisibility(View.VISIBLE);
	}
	for (Team team : this.teamDatas) {
	    teamName.add(team.getName());
	}
	adapter.notifyDataSetChanged();
    }
    
    public class SpinnerAdapter extends BaseAdapter {
	
	private List<String> teams;
	
	private Context context;
	
	public SpinnerAdapter(Context context, List<String> teams) {
	    this.teams = teams;
	    this.context = context;
	}

	@Override
	public int getCount() {
	    // TODO Auto-generated method stub
	    return teams.size();
	}
	
	@Override
	public void notifyDataSetChanged() {
	    // TODO Auto-generated method stub
	    super.notifyDataSetChanged();
	}

	@Override
	public String getItem(int position) {
	    // TODO Auto-generated method stub
	    return teams.get(position);
	}

	@Override
	public long getItemId(int position) {
	    // TODO Auto-generated method stub
	    return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    // TODO Auto-generated method stub
	    View cell = LayoutInflater.from(context).inflate(R.layout.list_cell_team, null);
	    String team = getItem(position);
	    TextView tv = (TextView) cell.findViewById(R.id.tv_name);
	    if (team != null) {
		tv.setText(team);
	    }
	    tv.setTextColor(R.color.white);
	    return cell;
	}
	
    }
}
