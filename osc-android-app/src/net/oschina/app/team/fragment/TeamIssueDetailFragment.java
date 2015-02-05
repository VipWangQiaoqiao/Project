package net.oschina.app.team.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BeseHaveHeaderListFragment;
import net.oschina.app.team.adapter.TeamReplyAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamIssue;
import net.oschina.app.team.bean.TeamIssueCatalog;
import net.oschina.app.team.bean.TeamIssueDetail;
import net.oschina.app.team.bean.TeamRepliesList;
import net.oschina.app.team.bean.TeamReply;
import net.oschina.app.util.TLog;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.togglebutton.ToggleButton;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TeamIssueDetailFragment.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 * 
 * @data 2015-1-30 下午2:16:36
 */
public class TeamIssueDetailFragment extends
	BeseHaveHeaderListFragment<TeamReply, TeamIssue> {

    private final String DETAIL_CACHE_KEY_PREFIX = "team_issue_detail_";

    private final String CACHE_KEY_PREFIX_COMMENT = "team_issue_reply_list_";

    private Team mTeam;

    private TeamIssue mTeamIssue;
    
    private TeamIssueCatalog mCatalog;

    private TextView mTvTitle;

    private WebView mWebView;

    private TextView mTvAuthor;

    private TextView mTvTo;

    private TextView mTvToUser;

    private TextView mTvTime;
    
    private Menu mMenu;
    
    private MenuInflater mMenuInflater;

    private LinearLayout mLLLabels;// 任务的标签

    @Override
    protected void sendRequestData() {
	OSChinaTeamApi.getTeamReplyList(mTeam.getId(), mTeamIssue.getId(),
		TeamReply.REPLY_TYPE_ISSUE, mCurrentPage, mHandler);
    }

    @Override
    protected String getCacheKeyPrefix() {
	// TODO Auto-generated method stub
	return CACHE_KEY_PREFIX_COMMENT + mTeamIssue.getId();
    }

    @Override
    protected TeamRepliesList parseList(InputStream is) throws Exception {
	TeamRepliesList list = XmlUtils.toBean(TeamRepliesList.class, is);
	return list;
    }

    @Override
    protected TeamRepliesList readList(Serializable seri) {
	return ((TeamRepliesList) seri);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
	    long id) {
	// TODO Auto-generated method stub

    }

    @Override
    protected void requestDetailData(boolean isRefresh) {
	// TODO Auto-generated method stub
	OSChinaTeamApi.getTeamIssueDetail(mTeam.getId(), mTeamIssue.getId(),
		mDetailHandler);
    }

    @Override
    protected View initHeaderView() {
	// TODO Auto-generated method stub
	Intent args = getActivity().getIntent();
	if (args != null) {
	    mTeam = (Team) args.getSerializableExtra("team");
	    mTeamIssue = (TeamIssue) args.getSerializableExtra("issue");
	    mCatalog = (TeamIssueCatalog) args.getSerializableExtra("issue_catalog");
	}
	if (mCatalog != null) {
	    
	    ((BaseActivity)getActivity()).setActionBarTitle(mCatalog.getTitle());
	}
	View headerView = LayoutInflater.from(getActivity()).inflate(
		R.layout.fragment_team_issue_detail, null);
	mTvTitle = findHeaderView(headerView, R.id.tv_issue_title);
	mWebView = findHeaderView(headerView, R.id.webview);
	mTvAuthor = findHeaderView(headerView, R.id.tv_issue_author);
	mTvTo = findHeaderView(headerView, R.id.tv_to);
	mTvToUser = findHeaderView(headerView, R.id.tv_issue_touser);
	mTvTime = findHeaderView(headerView, R.id.tv_issue_time);
	mLLLabels = findHeaderView(headerView, R.id.ll_team_issue_labels);
	setHasOptionsMenu(true);
	return headerView;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// TODO Auto-generated method stub
	inflater.inflate(R.menu.team_issue_detail_menu, menu);
	mMenu = menu;
	mMenuInflater = inflater;
	super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.team_issue_project_list:
	    break;

	default:
	    break;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getDetailCacheKey() {
	// TODO Auto-generated method stub
	return DETAIL_CACHE_KEY_PREFIX + mTeamIssue.getId();
    }

    @Override
    protected TeamIssue getDetailBean(ByteArrayInputStream is) {
	// TODO Auto-generated method stub
	return XmlUtils.toBean(TeamIssueDetail.class, is).getTeamIssue();
    }

    @Override
    protected TeamReplyAdapter getListAdapter() {
	// TODO Auto-generated method stub
	return new TeamReplyAdapter();
    }

    @Override
    protected void executeOnLoadDetailSuccess(TeamIssue detail) {
	
	// TODO Auto-generated method stub
	mTvTitle.setText(detail.getTitle());
	mTvTime.setText(detail.getAcceptTime());
	mTvAuthor.setText(detail.getAuthor().getName());
	if (detail.getToUser() != null
		&& !TextUtils.isEmpty(detail.getToUser().getName())) {
	    mTvToUser.setText(detail.getToUser().getName());
	} else {
	    mTvTo.setText("未指派");
	    mTvToUser.setVisibility(View.GONE);
	}
	if (detail.getState().equals("opened")) {
	} else {
	}
	mTvTime.setText(detail.getCreateTime());
	setLabels(detail);
	mWebView.loadDataWithBaseURL(null, detail.getDescription(),
		"text/html", "utf-8", null);
    }

    private void setLabels(TeamIssue issue) {
	if (issue.getLabels() == null || issue.getLabels().isEmpty()) {
	    mLLLabels.setVisibility(View.GONE);
	} else {
	    for (TeamIssue.Label label : issue.getLabels()) {
		TextView text = (TextView) LayoutInflater.from(getActivity())
			.inflate(R.layout.team_issue_lable, null, false);
		text.setText(label.getName());
		int color = Color.parseColor(label.getColor());
		text.setBackgroundColor(color);
		mLLLabels.addView(text);
	    }
	}
    }

    @Override
    public void onSendClick(String text) {

    }

}
