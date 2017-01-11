package net.oschina.app.improve.tweet.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.User;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetLike;
import net.oschina.app.improve.tweet.adapter.TweetLikeUsersAdapter;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

/**
 * 动弹详情, 点赞列表
 * Created by thanatos
 * on 16/6/13.
 */
public class ListTweetLikeUsersFragment extends BaseRecyclerViewFragment<TweetLike> implements TweetDetailContract.IThumbupView {

    private TweetDetailContract.Operator mOperator;
    private TweetDetailContract.IAgencyView mAgencyView;

    public static ListTweetLikeUsersFragment instantiate(TweetDetailContract.Operator operator, TweetDetailContract.IAgencyView mAgencyView) {
        ListTweetLikeUsersFragment fragment = new ListTweetLikeUsersFragment();
        fragment.mOperator = operator;
        fragment.mAgencyView = mAgencyView;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOperator = (TweetDetailContract.Operator) context;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mOperator.onScroll();
                }
            }
        });
    }

    @Override
    protected BaseRecyclerAdapter<TweetLike> getRecyclerAdapter() {
        return new TweetLikeUsersAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<TweetLike>>>() {
        }.getType();
    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }

    @Override
    protected void requestData() {
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.getTweetLikeList(mOperator.getTweetDetail().getId(), token, mHandler);
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        if (mAdapter.getCount() < 20 && mAgencyView != null)
            mAgencyView.resetLikeCount(mAdapter.getCount());
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        TweetLike liker = mAdapter.getItem(position);
        if (liker == null) return;
        UIHelper.showUserCenter(getContext(), liker.getAuthor().getId(), liker.getAuthor().getName());
    }

    @Override
    public void onLikeSuccess(boolean isUp, User user) {
        onRefreshing();
    }
}
