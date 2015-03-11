package net.oschina.app.team.fragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaTeamApi;
import net.oschina.app.base.BeseHaveHeaderListFragment;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.ResultBean;
import net.oschina.app.team.adapter.TeamReplyAdapter;
import net.oschina.app.team.bean.TeamDiscuss;
import net.oschina.app.team.bean.TeamDiscussDetail;
import net.oschina.app.team.bean.TeamRepliesList;
import net.oschina.app.team.bean.TeamReply;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TLog;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
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
	OSChinaTeamApi.getTeamReplyList(mTeamId, mDiscussId,
		TeamReply.REPLY_TYPE_DISCUSS, mCurrentPage, mHandler);
    }

    private AsyncHttpResponseHandler mReplyHandler = new AsyncHttpResponseHandler() {

	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
	    // TODO Auto-generated method stub
	    Result res = XmlUtils.toBean(ResultBean.class, arg2).getResult();
	    if (res.OK()) {
		AppContext.showToast("评论成功");
		mEmojiFragment.reset();
		onRefresh();
	    } else {
		AppContext.showToast(res.getErrorMessage());
	    }
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

	@Override
	public void onStart() {
	    // TODO Auto-generated method stub
	    super.onStart();
	    showWaitDialog();
	}
    };

    @Override
    public void onSendClick(String text) {
	if (TextUtils.isEmpty(text)) {
	    AppContext.showToast("请先输入评论内容...");
	    return;
	}
	if (!AppContext.getInstance().isLogin()) {
	    UIHelper.showLoginActivity(getActivity());
	    return;
	}
	int uid = AppContext.getInstance().getLoginUid();
	OSChinaTeamApi.pubTeamDiscussReply(uid, mTeamId, mDiscussId, text, mReplyHandler);
    }

    @Override
    protected void requestDetailData(boolean isRefresh) {
	OSChinaTeamApi
		.getTeamDiscussDetail(mTeamId, mDiscussId, mDetailHandler);
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
	mTvTime.setText(StringUtils.friendly_time(detailBean.getCreateTime()));
	mTvAnswerVote.setText(detailBean.getVoteUp() + "赞/"
		+ detailBean.getAnswerCount() + "回");
	UIHelper.initWebView(mWebView);
	UIHelper.addWebImageShow(getActivity(), mWebView);
	mWebView.loadDataWithBaseURL(null, UIHelper.WEB_STYLE + detailBean.getBody(), "text/html",
		"utf-8", null);
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
	return (TeamRepliesList) seri;
    }

    @Override
    protected String getCacheKeyPrefix() {
	// TODO Auto-generated method stub
	return "team_discuss_reply" + mTeamId + "_" + mDiscussId;
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub
	TeamReply reply = mAdapter.getItem(position - 1);
	if (reply == null) return;
	mEmojiFragment.reset();
	mEmojiFragment.appendInputText("回复 @" + reply.getAuthor().getName() + ": ");
    }

}
