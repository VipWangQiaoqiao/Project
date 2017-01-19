package net.oschina.app.improve.user.event;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/1/18.
 */

class UserEventPresenter implements UserEventContract.Presenter {
    private final UserEventContract.View mView;
    private final UserEventContract.EmptyView mEmptyView;
    private Type mGsonType;
    private long mAuthorId;
    private String mAuthorName;
    private String mNextPageToken;

    UserEventPresenter(UserEventContract.View mView,
                       UserEventContract.EmptyView mEmptyView,
                       long mAuthorId,
                       String mAuthorName) {
        this.mView = mView;
        this.mEmptyView = mEmptyView;
        this.mAuthorId = mAuthorId;
        this.mAuthorName = mAuthorName;
        mGsonType = new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getUserEvent(mAuthorId, mAuthorName, null, new TextHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                mView.onComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
                if (!TextUtils.isEmpty(mNextPageToken))
                    mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<SubBean>> resultBean = new Gson().fromJson(responseString, mGsonType);
                    if (resultBean != null) {
                        List<SubBean> items;
                        if (resultBean.isSuccess()) {
                            mNextPageToken = resultBean.getResult().getNextPageToken();
                            items = resultBean.getResult().getItems();
                            mView.onRefreshSuccess(items);
                            if (items.size() < 20)
                                mView.showMoreMore();
                            mEmptyView.hideEmptyLayout();
                        } else {
                            mView.showNetworkError(R.string.tip_network_error);
                            if (!TextUtils.isEmpty(mNextPageToken))
                                mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
                        }
                    } else {
                        mView.showNetworkError(R.string.tip_network_error);
                        if (!TextUtils.isEmpty(mNextPageToken))
                            mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.tip_network_error);
                    if (!TextUtils.isEmpty(mNextPageToken))
                        mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getUserEvent(mAuthorId, mAuthorName, mNextPageToken, new TextHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                mView.onComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<SubBean>> resultBean = new Gson().fromJson(responseString, mGsonType);
                    if (resultBean != null) {
                        List<SubBean> items;
                        if (resultBean.isSuccess()) {
                            mNextPageToken = resultBean.getResult().getNextPageToken();
                            items = resultBean.getResult().getItems();
                            mView.onLoadMoreSuccess(items);
                            if (items.size() < 20)
                                mView.showMoreMore();
                        } else {
                            mView.showNetworkError(R.string.tip_network_error);
                        }
                    } else {
                        mView.showNetworkError(R.string.tip_network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.tip_network_error);
                }
            }
        });
    }
}
