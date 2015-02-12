package net.oschina.app.team.fragment;

import org.apache.http.Header;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamIssue;
import net.oschina.app.team.bean.TeamIssueCatalog;
import net.oschina.app.team.bean.TeamIssueDetail;
import net.oschina.app.team.bean.TeamProject;
import net.oschina.app.util.XmlUtils;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TeamIssueDetailFragmentNew.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 * 
 * @data 2015-2-12 下午3:44:47
 */
public class TeamIssueDetailFragment extends BaseFragment implements
	EmojiTextListener, EmojiFragmentControl {

    private Team mTeam;

    private TeamIssue mTeamIssue;

    private TeamIssueCatalog mCatalog;
    
    @InjectView(R.id.tv_issue_title) TextView mTvTitle;
    @InjectView(R.id.tv_issue_touser) TextView mTvToUser;
    @InjectView(R.id.tv_issue_cooperate_user) TextView mTvCooperateUser;
    @InjectView(R.id.tv_issue_die_time) TextView mTvDieTime;
    @InjectView(R.id.tv_issue_state) TextView mTvState;
    @InjectView(R.id.ll_issue_labels) LinearLayout mLLlabels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	View root = inflater.inflate(R.layout.fragment_team_issue_detail,
		container, false);
	Intent args = getActivity().getIntent();
	if (args != null) {
	    mTeam = (Team) args.getSerializableExtra("team");
	    mTeamIssue = (TeamIssue) args.getSerializableExtra("issue");
	    mCatalog = (TeamIssueCatalog) args
		    .getSerializableExtra("issue_catalog");
	}
	if (mCatalog != null) {

	    ((BaseActivity) getActivity()).setActionBarTitle(mCatalog
		    .getTitle());
	}
	initView(root);
	initData();
	return root;
    }

    @Override
    public void initView(View view) {
	ButterKnife.inject(this, view);
    }

    @Override
    public void initData() {
	// TODO Auto-generated method stub
	super.initData();
	requestDetail();
    }

    private AsyncHttpResponseHandler mDetailHandler = new AsyncHttpResponseHandler() {

	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	    // TODO Auto-generated method stub
	    TeamIssueDetail teamIssueDetail = XmlUtils.toBean(TeamIssueDetail.class, arg2);
	    if (teamIssueDetail != null) {
		fillUI(teamIssueDetail.getTeamIssue());
	    }
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2,
		Throwable arg3) {
	    // TODO Auto-generated method stub

	}
	
	public void onStart() {
	    showWaitDialog("");
	};
	public void onFinish() {
	    hideWaitDialog();
	};
    };

    private void requestDetail() {
	OSChinaTeamApi.getTeamIssueDetail(mTeam.getId(), mTeamIssue.getId(),
		mDetailHandler);
    }
    
    private void fillUI(TeamIssue teamIssue) {
	if (teamIssue == null) return;
	this.mTeamIssue = teamIssue;
	mTvTitle.setText(mTeamIssue.getTitle());
	
	if (mTeamIssue.getToUser() != null
		&& !TextUtils.isEmpty(mTeamIssue.getToUser().getName())) {
	    mTvToUser.setText(mTeamIssue.getToUser().getName());
	} else {
	    mTvToUser.setText("未指派");
	}
	mTvState.setText(mTeamIssue.getIssueStateText());
	setLabels(mTeamIssue);
    }
    
    private void setLabels(TeamIssue issue) {
	if (issue.getLabels() == null || issue.getLabels().isEmpty()) {
	    mLLlabels.setVisibility(View.GONE);
	} else {
	    for (TeamIssue.Label label : issue.getLabels()) {
		TextView text = (TextView) LayoutInflater.from(getActivity())
			.inflate(R.layout.team_issue_lable, null, false);
		text.setText(label.getName());
		int color = Color.parseColor(label.getColor());
		text.setBackgroundColor(color);
		mLLlabels.addView(text);
	    }
	}
    }


    private EmojiFragment emojiFragment;

    @Override
    public void setEmojiFragment(EmojiFragment fragment) {
	// TODO Auto-generated method stub
	this.emojiFragment = fragment;
	emojiFragment.setEmojiTextListener(this);
    }

    @Override
    public void onSendClick(String text) {
	// TODO Auto-generated method stub

    }

    @Override
    @OnClick({R.id.rl_issue_touser, R.id.rl_issue_cooperate_user, R.id.rl_issue_die_time,
	R.id.rl_issue_state, R.id.rl_issue_child})
    public void onClick(View v) {
	// TODO Auto-generated method stub
	super.onClick(v);
    }
}
