package net.oschina.app.improve.detail.apply;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.ApplyUser;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.simple.UserRelation;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/12/27.
 */

class ApplyPresenter implements ApplyContract.Presenter {
    private final ApplyContract.View mView;
    private final ApplyContract.EmptyView mEmptyView;
    private final long mSourceId;
    private final Type mGsonType;
    private String mPageToken;
    private String mFilter;

    ApplyPresenter(ApplyContract.View mView, ApplyContract.EmptyView mEmptyView, long sourceId) {
        this.mView = mView;
        this.mSourceId = sourceId;
        this.mEmptyView = mEmptyView;
        mGsonType = new TypeToken<ResultBean<PageBean<ApplyUser>>>() {
        }.getType();
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getApplyUsers(mSourceId, null, mFilter, new TextHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
                mView.onComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
                if (TextUtils.isEmpty(mPageToken))
                    mEmptyView.showGetApplyUserError("网络错误");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<PageBean<ApplyUser>> resultBean = new Gson().fromJson(responseString, mGsonType);
                    List<ApplyUser> items;
                    if (resultBean != null) {
                        if (resultBean.isSuccess()) {
                            mPageToken = resultBean.getResult().getNextPageToken();
                            items = resultBean.getResult().getItems();
                            mView.onRefreshSuccess(items);
                            if (items.size() < 20)
                                mView.showMoreMore();
                            mEmptyView.showGetApplyUserSuccess();
                        } else {
                            mView.showNetworkError(R.string.tip_network_error);
                            if (TextUtils.isEmpty(mPageToken))
                                mEmptyView.showGetApplyUserError(resultBean.getMessage());
                            if (!TextUtils.isEmpty(mFilter)) {
                                mEmptyView.showSearchError(resultBean.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getApplyUsers(mSourceId, mPageToken, mFilter, new TextHttpResponseHandler() {
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
                    ResultBean<PageBean<ApplyUser>> resultBean = new Gson().fromJson(responseString, mGsonType);
                    List<ApplyUser> items;
                    if (resultBean != null) {
                        if (resultBean.isSuccess()) {
                            mPageToken = resultBean.getResult().getNextPageToken();
                            items = resultBean.getResult().getItems();
                            mView.onLoadMoreSuccess(items);
                            if (items.size() < 20)
                                mView.showMoreMore();
                        } else {
                            mView.showNetworkError(R.string.tip_network_error);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void addRelation(long authorId, final int position) {
        OSChinaApi.addUserRelationReverse(authorId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showAddRelationError();
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<UserRelation>>() {
                    }.getType();

                    ResultBean<UserRelation> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean != null && resultBean.isSuccess()) {
                        int relation = resultBean.getResult().getRelation();
                        mView.showAddRelationSuccess(relation, position);
                    } else {
                        mView.showAddRelationError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showAddRelationError();
                }
            }
        });
    }

    void setFilter(String mFilter) {
        this.mFilter = mFilter;
        if (TextUtils.isEmpty(this.mFilter))
            mPageToken = null;
    }
}
