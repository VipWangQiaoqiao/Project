package net.oschina.app.improve.git.feature;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Project;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/9.
 */

class FeaturePresenter implements FeatureContract.Presenter {
    private final FeatureContract.View mView;
    private int mPage;

    FeaturePresenter(FeatureContract.View mView) {
        this.mView = mView;
        mPage = 1;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        API.getFeatureProjects(1, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<Project>>>() {
                    }.getType();
                    ResultBean<List<Project>> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        List<Project> list = bean.getResult();
                        mView.onRefreshSuccess(list);
                        if (list.size() != 0 && list.size() < 20) {
                            mView.showMoreMore();
                        }
                        mPage = 2;
                    } else {
                        mView.showNetworkError(R.string.state_network_error);
                    }
                    mView.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.state_network_error);
                    mView.onComplete();
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        API.getFeatureProjects(mPage, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<Project>>>() {
                    }.getType();
                    ResultBean<List<Project>> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        List<Project> list = bean.getResult();
                        mView.onLoadMoreSuccess(list);
                        if (list.size() != 0 && list.size() < 20) {
                            mView.showMoreMore();
                        }
                        ++mPage;
                    } else {
                        mView.showNetworkError(R.string.state_network_error);
                    }
                    mView.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.state_network_error);
                    mView.onComplete();
                }
            }
        });
    }
}
