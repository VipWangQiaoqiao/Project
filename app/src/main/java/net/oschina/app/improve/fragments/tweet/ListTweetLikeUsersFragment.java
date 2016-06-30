package net.oschina.app.improve.fragments.tweet;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.TweetLikeUserList;
import net.oschina.app.bean.User;
import net.oschina.app.improve.adapter.base.BaseRecyclerAdapter;
import net.oschina.app.improve.adapter.tweet.TweetLikeUsersAdapter;
import net.oschina.app.improve.detail.contract.TweetDetailContract;
import net.oschina.app.improve.fragments.base.BaseRecyclerViewFragment;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹详情, 点赞列表
 * Created by thanatos on 16/6/13.
 */
public class ListTweetLikeUsersFragment extends BaseRecyclerViewFragment<User> implements TweetDetailContract.IThumbupView {

    private int pageNum = 0;
    private TweetDetailContract.Operator mOperator;
    private TweetDetailContract.IAgencyView mAgencyView;
    private AsyncHttpResponseHandler reqHandler;

    public static ListTweetLikeUsersFragment instantiate(TweetDetailContract.Operator operator, TweetDetailContract.IAgencyView mAgencyView) {
        ListTweetLikeUsersFragment fragment = new ListTweetLikeUsersFragment();
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

        reqHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                TweetLikeUserList data = XmlUtils.toBean(TweetLikeUserList.class, responseBody);
                setListData(data.getList());
                onRequestSuccess(1);
                onRequestFinish();
                if (mAdapter.getCount() < 20 && mAgencyView != null)
                    mAgencyView.resetLikeCount(mAdapter.getCount());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mRefreshLayout.onComplete();
                mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);
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
    protected BaseRecyclerAdapter<User> getRecyclerAdapter() {
        return new TweetLikeUsersAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<TweetLikeUserList>() {
        }.getType();
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
        if(mIsRefresh) pageNum = 0;
        ++pageNum;
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        User user = mAdapter.getItem(position);
        UIHelper.showUserCenter(getContext(), user.getId(), user.getName());
    }

    private void requestData(int pageNum) {
        OSChinaApi.getTweetLikeList(mOperator.getTweetDetail().getId(), pageNum, reqHandler);
    }

    private void setListData(List<User> users) {
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
