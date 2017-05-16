package net.oschina.app.improve.git.gist.detail;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Gist;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * 代码片段详情
 * Created by haibin on 2017/5/10.
 */

class GistDetailPresenter implements GistDetailContract.Presenter {
    private final GistDetailContract.View mView;
    private final GistDetailContract.EmptyView mEmptyView;

    GistDetailPresenter(GistDetailContract.View mView, GistDetailContract.EmptyView mEmptyView) {
        this.mView = mView;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getGistDetail(String id) {
        API.getGistDetail(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
                mEmptyView.showGetDetailFailure(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<Gist>() {
                    }.getType();
                    Gist bean = new Gson().fromJson(responseString, type);
                    if (bean != null) {
                        mView.showGetDetailSuccess(bean, R.string.get_project_detail_success);
                        mEmptyView.showGetDetailSuccess(EmptyLayout.HIDE_LAYOUT);
                    } else {
                        mView.showNetworkError(R.string.state_network_error);
                        mEmptyView.showGetDetailFailure(EmptyLayout.NETWORK_ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.state_network_error);
                    mEmptyView.showGetDetailFailure(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }

    @Override
    public void getCommentCount(String id) {
        API.getGistCommentCount(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // TODO: 2017/5/11
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<GistDetailPresenter.CommentCount>>() {
                    }.getType();
                    ResultBean<GistDetailPresenter.CommentCount> bean = new Gson().fromJson(responseString, type);
                    mView.showGetCommentCountSuccess(bean.getResult().commentCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void changeConfig(boolean isLandscape) {
        if (isLandscape)
            mView.showLandscape();
        else
            mView.showPortrait();
    }

    private class CommentCount {
        private int commentCount;

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }
    }
}
