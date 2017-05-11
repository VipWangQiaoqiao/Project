package net.oschina.app.improve.git.gist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Gist;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 代码片段
 * Created by haibin on 2017/5/10.
 */

class GistPresenter implements GistContract.Presenter {
    private final GistContract.View mView;
    private int mPage;

    GistPresenter(GistContract.View mView) {
        this.mView = mView;
        mPage = 1;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        API.getGists("", "", 1, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<Gist>>>() {
                    }.getType();
                    ResultBean<List<Gist>> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        List<Gist> list = bean.getResult();
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
        API.getGists("", "", mPage, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<List<Gist>>>() {
                    }.getType();
                    ResultBean<List<Gist>> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        List<Gist> list = bean.getResult();
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
