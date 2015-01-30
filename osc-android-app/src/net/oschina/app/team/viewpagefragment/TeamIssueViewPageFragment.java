package net.oschina.app.team.viewpagefragment;

import java.io.ByteArrayInputStream;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamCatalog;
import net.oschina.app.team.bean.TeamCatalogList;
import net.oschina.app.team.fragment.IssueListFragment;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;

/** 
 * 
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月14日 下午2:18:25 
 * 
 */

public class TeamIssueViewPageFragment extends BaseViewPagerFragment {
	
	private Team mTeam;
	
	private TeamCatalogList mCatalogList;
	
	private int mProjectId = 442;
	
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
			mProjectId = 0;
			sendRequestCatalogList();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(true);
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

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		//adapter.addTab("未指定列表", "", IssueListFragment.class, null);
		sendRequestCatalogList();
	}
	
	private void sendRequestCatalogList() {
		OSChinaTeamApi.getTeamCatalogIssueList(253900, 12481, mProjectId, "", handler);
	}
	
	private void addCatalogList() {
		if (mCatalogList != null && !mCatalogList.getList().isEmpty()
				&& mTabsAdapter != null) {
			for (TeamCatalog catalog : mCatalogList.getList()) {
				mTabsAdapter.addTab(catalog.getTitle(), catalog.getTitle(), IssueListFragment.class, null);
			}
			mTabsAdapter.notifyDataSetChanged();
		}
	}
}
