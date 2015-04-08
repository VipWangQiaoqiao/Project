package net.oschina.app.team.ui;

import java.util.ArrayList;
import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamList;
import net.oschina.app.team.viewpagefragment.TeamMainViewPagerFragment;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.PreferenceHelper;
import org.kymjs.kjframe.utils.StringUtils;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tencent.weibo.sdk.android.component.sso.tools.MD5Tools;

/**
 * 团队主界面
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月13日 下午3:36:56
 * 
 * 
 */

public class TeamMainActivity extends BaseActivity {

    private final String TEAM_LIST_FILE = "team_list_file";
    private final String TEAM_LIST_KEY = "team_list_key"
            + AppContext.getInstance().getLoginUid();

    public final static String BUNDLE_KEY_TEAM = "bundle_key_team";

    public final static String BUNDLE_KEY_PROJECT = "bundle_key_project";

    public final static String BUNDLE_KEY_ISSUE_CATALOG = "bundle_key_catalog_list";

    private final String tag = "team_view";

    private FragmentManager mFragmentManager;
    
    private int mCurrentContentIndex = -1;

    @InjectView(R.id.error_layout)
    EmptyLayout mErrorLayout;
    @InjectView(R.id.main_content)
    View container;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_team_main;
    }

    @Override
    public void onClick(View v) {}

    @Override
    protected boolean haveSpinner() {
        return true;
    }

    @Override
    public void initView() {
        ButterKnife.inject(this);
        // 隐藏actionbar的标题
        mActionBar.getCustomView().findViewById(R.id.tv_actionbar_title)
                .setVisibility(View.GONE);
        // ImageView back = (ImageView)
        // mActionBar.getCustomView().findViewById(R.id.btn_back);
        // back.setImageResource(R.drawable.abc_ab_bottom_solid_dark_holo);
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mErrorLayout.setErrorMessage("获取团队中...");
        mErrorLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestTeamList();
            }
        });
        initSpinner();
        requestTeamList();

        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void initData() {}

    private Spinner mSpinner;
    private SpinnerAdapter adapter;

    private final List<String> teamName = new ArrayList<String>();
    private List<Team> teamDatas = new ArrayList<Team>();

    private void initSpinner() {
        mSpinner = getSpinner();
        adapter = new SpinnerAdapter(this, teamName);
        mSpinner.setAdapter(adapter);
        mSpinner.setVisibility(View.GONE);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                // TODO Auto-generated method stub
                Team team = teamDatas.get(position);
                if (team != null) {
                    switchTeam(position);
                    adapter.setSelectIndex(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 
     * @param pos
     */
    private void switchTeam(int pos) {
        if (pos == mCurrentContentIndex)
            return;
        showWaitDialog("正在切换...");
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (tag != null) {
            Fragment fragment = mFragmentManager.findFragmentByTag(tag);
            if (fragment != null) {
                ft.remove(fragment);
            }
        }
        try {
            TeamMainViewPagerFragment fragment = TeamMainViewPagerFragment.class
                    .newInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_KEY_TEAM, teamDatas.get(pos));
            fragment.setArguments(bundle);
            ft.replace(R.id.main_content, fragment, tag);
            ft.commitAllowingStateLoss();
            mCurrentContentIndex = pos;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        hideWaitDialog();
    }

    private void requestTeamList() {
        // 初始化团队列表数据
        String cache = PreferenceHelper.readString(this, TEAM_LIST_FILE,
                TEAM_LIST_KEY, "");
        if (!StringUtils.isEmpty(cache)) {
           // mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            teamDatas = TeamList.toTeamList(cache);
            setTeamDataState();
        } else {
            //mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        OSChinaApi.teamList(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                TeamList datas = XmlUtils.toBean(TeamList.class, arg2);
                if (teamDatas.isEmpty() && datas != null) {
                    teamDatas.addAll(datas.getList());
                    setTeamDataState();
                } else {
                    if (teamDatas == null && datas == null) {
                	AppContext.showToast(new String(arg2));
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        mErrorLayout.setErrorMessage("获取团队失败");
                    }
                }
                
                if (datas != null) {
                    // 保存新的团队列表
                    PreferenceHelper.write(TeamMainActivity.this, TEAM_LIST_FILE,
                            TEAM_LIST_KEY, datas.toCacheData());
                }
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                    Throwable arg3) {
               //AppContext.showToast("网络不好，请稍后重试");
            }
        });
    }

    private void setTeamDataState() {
	if (teamDatas == null) {
	    teamDatas = new ArrayList<Team>();
	}
        if (teamDatas.isEmpty()) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
            String msg = getResources().getString(R.string.team_empty);
            mErrorLayout.setErrorMessage(msg);
            mErrorLayout.setErrorImag(R.drawable.page_icon_empty);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                }
            }, 800);
            mSpinner.setVisibility(View.VISIBLE);
            container.setVisibility(View.VISIBLE);
        }
        for (Team team : this.teamDatas) {
            teamName.add(team.getName());
        }
        adapter.notifyDataSetChanged();
    }

    public class SpinnerAdapter extends BaseAdapter {

        private final List<String> teams;

        private final Context context;

        private int selectIndex = 0;

        public void setSelectIndex(int index) {
            this.selectIndex = index;
        }

        public SpinnerAdapter(Context context, List<String> teams) {
            this.teams = teams;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.spinner_layout_head, null);
            }
            ((TextView) convertView).setText(getItem(position));

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.list_cell_team, null, false);
            }
            String team = getItem(position);
            TextView tv = (TextView) convertView.findViewById(R.id.tv_name);
            if (team != null) {
                tv.setText(team);
            }
            if (selectIndex != position) {
                tv.setTextColor(Color.parseColor("#acd4b3"));
            } else {
                tv.setTextColor(Color.parseColor("#6baf77"));
            }
            return convertView;
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

    }
}
