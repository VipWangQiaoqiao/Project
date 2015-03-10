package net.oschina.app.team.fragment;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.team.adapter.DiaryDetailAdapter;
import net.oschina.app.team.bean.TeamDiary;
import net.oschina.app.team.bean.TeamDiaryDetailBean;
import net.oschina.app.ui.SimpleBackActivity;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.XmlUtils;
import net.oschina.app.widget.AvatarView;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.KJLoger;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 周报详情<br>
 * 逻辑介绍：用Listview来显示评论内容，在ListView的HeadView中添加本周报的详细内容与周报列表的item。
 * 周报的详细内容通过动态添加addView的方式
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class TeamDiaryDetail extends BaseFragment {

    private static final String CACHE_KEY_PREFIX = "team_diary_detail_";

    @InjectView(R.id.listview)
    ListView mList;
    @InjectView(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwiperefreshlayout;
    @InjectView(R.id.error_layout)
    EmptyLayout mErrorLayout;

    private TeamDiary diaryData;
    private int teamid;
    private Activity aty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = View.inflate(getActivity(),
                R.layout.fragment_pull_refresh_listview, null);
        aty = getActivity();
        ButterKnife.inject(this, rootView);
        initData();
        initView(rootView);
        return rootView;
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = aty.getIntent().getBundleExtra(
                SimpleBackActivity.BUNDLE_KEY_ARGS);
        if (bundle != null) {
            teamid = bundle.getInt(TeamDiaryPagerFragment.TEAMID_KEY);
            diaryData = (TeamDiary) bundle
                    .getSerializable(TeamDiaryPagerFragment.DIARYDETAIL_KEY);
        } else {
            diaryData = new TeamDiary();
            Log.e("debug", getClass().getSimpleName() + "diaryData初始化异常");
        }
        KJLoger.debug("TeamDiaryDetail=81===id=：" + diaryData.getId());
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mList.setDivider(null);
        mList.addHeaderView(initHeaderView());
        mList.addFooterView(initFooterView());

        mSwiperefreshlayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mState == STATE_REFRESH) {
                    return;
                } else {
                    // 设置顶部正在刷新
                    setSwipeRefreshLoadingState(mSwiperefreshlayout);
                    /* !!! 设置耗时操作 !!! */
                    initCommitLayout();
                }
            }
        });
        mSwiperefreshlayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        initListData();
        initCommitLayout();
    }

    /**
     * 设置顶部正在加载的状态
     */
    private void setSwipeRefreshLoadingState(
            SwipeRefreshLayout mSwipeRefreshLayout) {
        mState = STATE_REFRESH;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /**
     * 设置顶部加载完毕的状态
     */
    private void setSwipeRefreshLoadedState(
            SwipeRefreshLayout mSwipeRefreshLayout) {
        mState = STATE_NOMORE;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    /**
     * 初始化头部周报Title
     * 
     * @return
     */
    private View initHeaderView() {
        View headerView = inflateView(R.layout.item_team_dynamic);
        AvatarView headImg = (AvatarView) headerView
                .findViewById(R.id.event_listitem_userface);
        TextView userName = (TextView) headerView
                .findViewById(R.id.event_listitem_username);
        TextView content = (TextView) headerView
                .findViewById(R.id.event_listitem_content);
        TextView time = (TextView) headerView
                .findViewById(R.id.event_listitem_date);
        headImg.setAvatarUrl(diaryData.getAuthor().getPortrait());
        userName.setText(diaryData.getAuthor().getName());
        content.setText(diaryData.getTitle());
        time.setText(diaryData.getCreateTime());
        return headerView;
    }

    private View initFooterView() {
        LinearLayout layout = new LinearLayout(aty);
        return layout;
    }

    private void initListData() {
        OSChinaApi.getDiaryDetail(teamid, diaryData.getId(),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    }

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                        TeamDiaryDetailBean data = XmlUtils.toBean(
                                TeamDiaryDetailBean.class, arg2);
                        mList.setAdapter(new DiaryDetailAdapter(aty, data
                                .getTeamDiary().getDetail()));
                        mErrorLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                            Throwable arg3) {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        mErrorLayout.setErrorMessage("网络不好，请稍后重试");
                    }
                });
    }

    private void initCommitLayout() {

    }
}
