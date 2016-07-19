package net.oschina.app.improve.tweet.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.TweetLikeUserList;
import net.oschina.app.bean.User;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.TweetLike;
import net.oschina.app.improve.tweet.adapter.TweetLikeUsersAdapter;
import net.oschina.app.improve.tweet.adapter.XTweetLikeUsersAdapter;
import net.oschina.app.improve.tweet.contract.TweetDetailContract;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹详情, 点赞列表
 * Created by thanatos
 * on 16/6/13.
 */
public class XListTweetLikeUsersFragment extends BaseRecyclerViewFragment<TweetLike> implements TweetDetailContract.IThumbupView {

    private int pageNum = 0;
    private TweetDetailContract.Operator mOperator;
    private TweetDetailContract.IAgencyView mAgencyView;
    private TextHttpResponseHandler reqHandler;

    public static XListTweetLikeUsersFragment instantiate(TweetDetailContract.Operator operator, TweetDetailContract.IAgencyView mAgencyView) {
        XListTweetLikeUsersFragment fragment = new XListTweetLikeUsersFragment();
        fragment.mOperator = operator;
        fragment.mAgencyView = mAgencyView;
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOperator = (TweetDetailContract.Operator) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reqHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mRefreshLayout.onComplete();
                mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ResultBean<PageBean<TweetLike>> result = AppContext.createGson().fromJson(
                        responseString, new TypeToken<ResultBean<PageBean<TweetLike>>>(){}.getType());
                if (result.isSuccess()){
                    setListData(result.getResult().getItems());
                    onRequestSuccess(1);
                    onRequestFinish();
                    if (mAdapter.getCount() < 20 && mAgencyView != null)
                        mAgencyView.resetLikeCount(mAdapter.getCount());
                }else{
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        };
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
        return new XTweetLikeUsersAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return null;
    }

    @Override
    public void onLoadMore() {
        requestData(pageNum);
    }

    @Override
    protected void requestData() {
        requestData(0);
    }

    @Override
    protected void onRequestSuccess(int code) {
        super.onRequestSuccess(code);
        if (mIsRefresh) pageNum = 0;
        ++pageNum;
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        TweetLike liker = mAdapter.getItem(position);
        UIHelper.showUserCenter(getContext(), liker.getAuthor().getId(), liker.getAuthor().getName());
    }

    private void requestData(int pageNum) {
        OSChinaApi.getTweetLikeList(mOperator.getTweetDetail().getId(), reqHandler);
    }

    private void setListData(List<TweetLike> users) {
        if (mIsRefresh) {
            //cache the time
            mAdapter.clear();
            mAdapter.addAll(users);
            mRefreshLayout.setCanLoadMore(true);
        } else {
            mAdapter.addAll(users);
        }
        if (users.size() < 20) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
        }
    }

    @Override
    public void onLikeSuccess(boolean isUp, User user) {
        onRefreshing();
    }
}
