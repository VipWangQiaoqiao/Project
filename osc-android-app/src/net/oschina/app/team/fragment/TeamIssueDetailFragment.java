package net.oschina.app.team.fragment;

import java.util.List;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BaseActivity;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.emoji.EmojiFragment;
import net.oschina.app.emoji.EmojiFragment.EmojiTextListener;
import net.oschina.app.interf.EmojiFragmentControl;
import net.oschina.app.team.bean.Author;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamIssue;
import net.oschina.app.team.bean.TeamIssueCatalog;
import net.oschina.app.team.bean.TeamIssueDetail;
import net.oschina.app.team.bean.TeamRepliesList;
import net.oschina.app.team.bean.TeamReply;
import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TypefaceUtils;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.viewpagerfragment.NewsViewPagerFragment;
import net.oschina.app.widget.AvatarView;

import org.apache.http.Header;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.loopj.android.http.AsyncHttpResponseHandler;

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

    @InjectView(R.id.ll_issue_project)
    View mProjectView;
    @InjectView(R.id.tv_issue_project)
    TextView mTvProject;
    @InjectView(R.id.tv_issue_state_title)
    TextView mTvStateTitle;
    @InjectView(R.id.tv_issue_title)
    TextView mTvTitle;
    @InjectView(R.id.tv_issue_touser)
    TextView mTvToUser;
    @InjectView(R.id.tv_issue_cooperate_user)
    TextView mTvCooperateUser;
    @InjectView(R.id.tv_issue_die_time)
    TextView mTvDieTime;
    @InjectView(R.id.tv_issue_state)
    TextView mTvState;
    @InjectView(R.id.ll_issue_labels)
    LinearLayout mLLlabels;
    @InjectView(R.id.tv_issue_attachments)
    TextView mTvAttachments;
    @InjectView(R.id.tv_issue_relations)
    TextView mTvRelations;
    @InjectView(R.id.tv_issue_child)
    TextView mTvIssueChild;

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

	TypefaceUtils.setTypeface((TextView) view
		.findViewById(R.id.tv_issue_fa_touser));
	TypefaceUtils.setTypeface((TextView) view
		.findViewById(R.id.tv_issue_fa_cooperate_user));
	TypefaceUtils.setTypeface((TextView) view
		.findViewById(R.id.tv_issue_fa_die_time));
	TypefaceUtils.setTypeface((TextView) view
		.findViewById(R.id.tv_issue_fa_state));
	TypefaceUtils.setTypeface((TextView) view
		.findViewById(R.id.tv_issue_fa_labels));
	TypefaceUtils.setTypeface((TextView) view
		.findViewById(R.id.tv_issue_fa_child));
	TypefaceUtils.setTypeface((TextView) view
		.findViewById(R.id.tv_issue_fa_relations));
	TypefaceUtils.setTypeface((TextView) view
		.findViewById(R.id.tv_issue_fa_attachments));
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
	    TeamIssueDetail teamIssueDetail = XmlUtils.toBean(
		    TeamIssueDetail.class, arg2);
	    if (teamIssueDetail != null) {
		fillUI(teamIssueDetail.getTeamIssue());
		requestIssueComments();
	    }
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2,
		Throwable arg3) {
	    // TODO Auto-generated method stub

	}

	public void onStart() {
	    showWaitDialog("加载中...");
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
	if (teamIssue == null)
	    return;
	this.mTeamIssue = teamIssue;
	if (mTeamIssue.getProject() != null
		&& mTeamIssue.getProject().getGit() != null) {
	    mProjectView.setVisibility(View.VISIBLE);
	    String pushState = mTeamIssue.getGitpush() == TeamIssue.TEAM_ISSUE_GITPUSHED ? "-未同步"
		    : "";
	    mTvProject.setText(mTeamIssue.getProject().getGit().getName()
		    + pushState);
	} else {
	    mProjectView.setVisibility(View.GONE);
	}

	TypefaceUtils.setTypeface(mTvStateTitle,
		mTeamIssue.getIssueStateFaTextId());

	if (mTeamIssue.getState().equals("closed")) {
	    mTvTitle.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // 中划线
	}

	mTvTitle.setText(mTeamIssue.getTitle());

	if (mTeamIssue.getToUser() != null
		&& !TextUtils.isEmpty(mTeamIssue.getToUser().getName())) {
	    mTvToUser.setText(mTeamIssue.getToUser().getName());
	} else {
	    mTvToUser.setText("未指派");
	}

	if (!TextUtils.isEmpty(mTeamIssue.getDeadlineTime())) {
	    mTvDieTime.setText(mTeamIssue.getDeadlineTimeText());
	}

	if (mTeamIssue.getAttachments().getTotalCount() != 0) {
	    mTvAttachments.setText(mTeamIssue.getAttachments().getTotalCount()
		    + "");
	} else {
	    mTvAttachments.setText("暂无附件");
	}

	if (mTeamIssue.getRelations().getTotalCount() != 0) {
	    mTvRelations
		    .setText(mTeamIssue.getRelations().getTotalCount() + "");
	} else {
	    mTvRelations.setText("暂无关联");
	}

	if (mTeamIssue.getChildIssues().getTotalCount() != 0) {
	    String childIssueState = mTeamIssue.getChildIssues()
		    .getTotalCount()
		    + "个子任务，"
		    + mTeamIssue.getChildIssues().getClosedCount() + "个已完成";
	    mTvIssueChild.setText(childIssueState);
	} else {
	    mTvIssueChild.setText("暂无子任务");
	}

	setChildIssues(mTeamIssue.getChildIssues().getChildIssues());

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
	showWaitDialog("提交评论中...");
	OSChinaTeamApi.pubTeamTweetReply(mTeam.getId(),
		TeamReply.REPLY_PUB_TYPE_ISSUE, mTeamIssue.getId(), text,
		new AsyncHttpResponseHandler() {

		    @Override
		    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			// TODO Auto-generated method stub
			TeamReply reply = new TeamReply();
			Author author = new Author();
			author.setId(AppContext.getInstance().getLoginUid());
			author.setName(AppContext.getInstance().getLoginUser()
				.getName());
			reply.setAuthor(author);
			addComment(reply);
		    }

		    @Override
		    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
			    Throwable arg3) {
			// TODO Auto-generated method stub
			AppContext.showToast(new String(arg2));

		    }

		    @Override
		    public void onFinish() {
			// TODO Auto-generated method stub
			super.onFinish();
			hideWaitDialog();
		    }
		});
    }

    private AsyncHttpResponseHandler mChangeIssueHandler = new AsyncHttpResponseHandler() {

	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	    // TODO Auto-generated method stub
	    AppContext.showToast(new String(arg2));
	}

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2,
		Throwable arg3) {
	    // TODO Auto-generated method stub
	    AppContext.showToast(new String(arg2));
	}
    };

    @Override
    @OnClick({ R.id.ll_issue_state_title, R.id.ll_issue_touser,
	    R.id.ll_issue_cooperate_user, R.id.ll_issue_die_time,
	    R.id.ll_issue_state, R.id.ll_issue_child })
    public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.ll_issue_state_title:
	case R.id.ll_issue_state:
	    changeIssueState();
	    break;
	case R.id.ll_issue_touser:

	    break;
	case R.id.ll_issue_cooperate_user:

	    break;
	case R.id.ll_issue_die_time:

	    break;
	case R.id.ll_issue_child:
	    if (mLLChildIssues.getVisibility() == View.GONE) {
		mLLChildIssues.setVisibility(View.VISIBLE);
	    } else {
		mLLChildIssues.setVisibility(View.GONE);
	    }
	    break;

	default:
	    break;
	}
    }

    private void changeIssueState() {
	mTeamIssue.setState("opened");
	OSChinaTeamApi.changeIssueState(mTeam.getId(), mTeamIssue, "state",
		mChangeIssueHandler);
    }

    @InjectView(R.id.ll_issue_childs)
    LinearLayout mLLChildIssues;

    private void setChildIssues(List<TeamIssue> list) {
	if (list == null || list.isEmpty())
	    return;

	for (TeamIssue teamIssue : list) {
	    addChildIssue(teamIssue);
	}
    }

    private void addChildIssue(TeamIssue teamIssue) {
	if (teamIssue == null)
	    return;
	View cell = LayoutInflater.from(getActivity()).inflate(
		R.layout.list_cell_team_child_issue, null, false);
	AvatarView avatarView = (AvatarView) cell.findViewById(R.id.iv_avatar);
	avatarView.setAvatarUrl(teamIssue.getToUser().getPortrait());
	TextView content = (TextView) cell.findViewById(R.id.tv_content);
	content.setText(teamIssue.getTitle());
	if (teamIssue.getState().equalsIgnoreCase("closed")) {
	    content.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
	    TypefaceUtils.setTypeface(
		    (TextView) cell.findViewById(R.id.tv_state),
		    R.string.fa_check_circle_o);
	} else {
	    TypefaceUtils.setTypeface(
		    (TextView) cell.findViewById(R.id.tv_state),
		    R.string.fa_circle_o);
	}
	mLLChildIssues.addView(cell);
    }

    @InjectView(R.id.ll_issue_comments)
    LinearLayout mLLComments;

    // 请求任务的评论
    private void requestIssueComments() {
	OSChinaTeamApi.getTeamReplyList(mTeam.getId(), mTeamIssue.getId(),
		TeamReply.REPLY_TYPE_ISSUE, 0, new AsyncHttpResponseHandler() {

		    @Override
		    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
			// TODO Auto-generated method stub
			TeamRepliesList list = XmlUtils.toBean(
				TeamRepliesList.class, arg2);
			if (list != null && !list.getList().isEmpty()) {
			    fillComments(list.getList());
			}
		    }

		    @Override
		    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
			    Throwable arg3) {
			// TODO Auto-generated method stub

		    }
		});
    }

    private void fillComments(List<TeamReply> list) {
	if (list == null || list.isEmpty())
	    return;
	for (TeamReply teamReply : list) {
	    addComment(teamReply);
	}
    }

    private void addComment(final TeamReply reply) {
	View cell = LayoutInflater.from(getActivity()).inflate(
		R.layout.list_cell_team_reply, null, false);
	AvatarView avatarView = (AvatarView) cell.findViewById(R.id.iv_avatar);
	avatarView.setAvatarUrl(reply.getAuthor().getPortrait());
	TextView name = (TextView) cell.findViewById(R.id.tv_name);
	name.setText(reply.getAuthor().getName());
	TextView content = (TextView) cell.findViewById(R.id.tv_content);
	content.setText(HTMLUtil.delHTMLTag(reply.getContent()));
	TextView time = (TextView) cell.findViewById(R.id.tv_time);
	time.setText(StringUtils.friendly_time(reply.getCreateTime()));
	mLLComments.addView(cell);

	cell.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		// TODO Auto-generated method stub
		emojiFragment.reset();
		emojiFragment.appendInputText("回复 @"
			+ reply.getAuthor().getName() + ": ");
	    }
	});
    }
}
