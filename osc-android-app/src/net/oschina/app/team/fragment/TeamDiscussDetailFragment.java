package net.oschina.app.team.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import com.baidu.mapapi.map.Text;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BeseHaveHeaderListFragment;
import net.oschina.app.team.adapter.TeamReplyAdapter;
import net.oschina.app.team.bean.TeamDiscuss;
import net.oschina.app.team.bean.TeamDiscussDetail;
import net.oschina.app.team.bean.TeamRepliesList;
import net.oschina.app.team.bean.TeamReply;
import net.oschina.app.util.XmlUtils;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * TeamDiscussDetailFragment.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 *
 * @data 2015-2-2 下午6:14:15
 */
public class TeamDiscussDetailFragment extends
	BeseHaveHeaderListFragment<TeamReply, TeamDiscuss> {
    
    private int mTeamId;
    
    private int mDiscussId;
    
    private TextView mTvTitle;
    
    private TextView mTvAuthor;
    
    private TextView mTvTime;
    
    private TextView mTvAnswerVote;
    
    private WebView mWebView;
    
    @Override
    protected void sendRequestData() {
	OSChinaTeamApi.getTeamReplyList(mTeamId, mDiscussId, TeamReply.REPLY_TYPE_DISCUSS,
		mCurrentPage, mHandler);
    }
    
    @Override
    public void onSendClick(String text) {
	// TODO Auto-generated method stub
	
    }

    @Override
    protected void requestDetailData(boolean isRefresh) {
	OSChinaTeamApi.getTeamDiscussDetail(mTeamId, mDiscussId, mDetailHandler);
    }

    @Override
    protected View initHeaderView() {
	// TODO Auto-generated method stub
	Intent args = getActivity().getIntent();
	if (args != null) {
	    mTeamId = args.getIntExtra("teamid", 0);
	    mDiscussId = args.getIntExtra("discussid", 0);
	}
	View headerView = LayoutInflater.from(getActivity()).inflate(
		R.layout.fragment_team_discuss_detail, null);
	mTvTitle = findHeaderView(headerView, R.id.tv_title);
	mTvAuthor = findHeaderView(headerView, R.id.tv_author);
	mTvTime = findHeaderView(headerView, R.id.tv_time);
	mTvAnswerVote = findHeaderView(headerView, R.id.tv_answer_vote);
	mWebView = findHeaderView(headerView, R.id.webview);
	return headerView;
    }

    @Override
    protected String getDetailCacheKey() {
	// TODO Auto-generated method stub
	return "team_discuss_detail_" + mTeamId + mDiscussId;
    }

    @Override
    protected void executeOnLoadDetailSuccess(TeamDiscuss detailBean) {
	// TODO Auto-generated method stub
	mTvTitle.setText(detailBean.getTitle());
	mTvAuthor.setText(detailBean.getAuthor().getName());
	mTvTime.setText(detailBean.getCreateTime());
	mTvAnswerVote.setText(detailBean.getVoteUp() + "赞/" + detailBean.getAnswerCount() + "回");
	mWebView.loadDataWithBaseURL(null, detailBean.getBody(),
		"text/html", "utf-8", null);
    }

    @Override
    protected TeamDiscuss getDetailBean(ByteArrayInputStream is) {
	// TODO Auto-generated method stub
	return XmlUtils.toBean(TeamDiscussDetail.class, is).getDiscuss();
    }

    @Override
    protected TeamReplyAdapter getListAdapter() {
	// TODO Auto-generated method stub
	return new TeamReplyAdapter();
    }

    @Override
    protected TeamRepliesList parseList(InputStream is) throws Exception {
	// TODO Auto-generated method stub
	return XmlUtils.toBean(TeamRepliesList.class, is);
    }

    @Override
    protected TeamRepliesList readList(Serializable seri) {
	// TODO Auto-generated method stub
	return  (TeamRepliesList) seri;
    }

    @Override
    protected String getCacheKeyPrefix() {
	// TODO Auto-generated method stub
	return "team_discuss_reply" + mTeamId + "_" + mDiscussId;
   }

}

