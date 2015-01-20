package net.oschina.app.team.viewpagefragment;

import java.io.ByteArrayInputStream;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamCatalog;
import net.oschina.app.team.bean.TeamCatalogList;
import net.oschina.app.team.bean.TeamProject;
import net.oschina.app.team.fragment.TeamIssueListFragment;
import net.oschina.app.team.fragment.TeamProjectSelectPopupWindow;
import net.oschina.app.team.fragment.TeamProjectSelectPopupWindow.TeamProjectPopupWindowCallBack;
import net.oschina.app.team.ui.TeamMainActivity;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.XmlUtils;

/** 
 * Team 任务列表viewpager
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月14日 下午2:18:25 
 * 
 */

public class TeamIssueViewPageFragment extends BaseViewPagerFragment {
	
	private Team mTeam;
	
	private TeamCatalogList mCatalogList;
	
	private int mTeamId;
	
	private int mProjectId;
	
	private TeamProjectSelectPopupWindow mProjectsDialog;
	
	private AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
		
		@Override
		public void onCancel() {
			super.onCancel();
		}

		@Override
		public void onFinish() {
			super.onFinish();
			mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
		}

		@Override
		public void onStart() {
			super.onStart();
			mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			TeamCatalogList catalogList = XmlUtils.toBean(TeamCatalogList.class, new ByteArrayInputStream(arg2));
			if (catalogList != null) {
				mCatalogList = catalogList;
				mTabsAdapter.removeAll();
				addCatalogList();
			} else {
				onFailure(arg0, arg1, arg2, null);
			}
		}
		
		@Override
		public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
			mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
		}
	};
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.team_issue_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.team_issue_project_list:
			showProjectsSelectDialog();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private TeamProjectPopupWindowCallBack mCallBack = new TeamProjectPopupWindowCallBack() {
		
		@Override
		public void callBack(TeamProject teamProject) {
			mProjectId = teamProject.getGit().getId();
			sendRequestCatalogList();
		}
	};
	
	private void showProjectsSelectDialog() {
		if (mProjectsDialog == null) {
			mProjectsDialog = new TeamProjectSelectPopupWindow(getActivity(), mTeam, mCallBack);
		}
		mProjectsDialog.showAsDropDown(((BaseActivity)getActivity()).getActionBar().getCustomView());
	}
	
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
        setHasOptionsMenu(true);
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		sendRequestCatalogList();
	}
	
	private void sendRequestCatalogList() {
		OSChinaTeamApi.getTeamCatalogIssueList(253900, mTeamId, mProjectId, "", handler);
	}
	
	private void addCatalogList() {
		// 加入一个为指定列表
		if (mCatalogList != null) {
			mCatalogList.getList().add(0, getUnCatalog());
		}
		
		if (mCatalogList != null && !mCatalogList.getList().isEmpty()
				&& mTabsAdapter != null) {
			for (TeamCatalog catalog : mCatalogList.getList()) {
				mTabsAdapter.addTab(catalog.getTitle(), catalog.getTitle(), TeamIssueListFragment.class, null);
			}
			mTabsAdapter.notifyDataSetChanged();
		}
	}
	
	private TeamCatalog getUnCatalog() {
		TeamCatalog catalog = new TeamCatalog();
		catalog.setTitle("未指定列表");
		return catalog;
	}
	
	@Override
	public void onClick(View v) {

	}

	@Override
	public void initView(View view) {

	}

	@Override
	public void initData() {

	}
}
