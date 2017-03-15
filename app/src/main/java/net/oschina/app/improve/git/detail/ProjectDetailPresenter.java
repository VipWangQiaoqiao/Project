package net.oschina.app.improve.git.detail;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/9.
 */

class ProjectDetailPresenter implements ProjectDetailContract.Presenter {
    private final ProjectDetailContract.View mView;
    private final ProjectDetailContract.EmptyView mEmptyView;
    private TextHttpResponseHandler mHandler;
    private Project mProject;

    ProjectDetailPresenter(ProjectDetailContract.View mView,
                           ProjectDetailContract.EmptyView mEmptyView,
                           Project project) {
        this.mView = mView;
        this.mEmptyView = mEmptyView;
        this.mProject = project;
        initHandler();
        this.mView.setPresenter(this);
    }

    private void initHandler() {
        mHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
                mEmptyView.showGetDetailFailure(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Project>>() {
                    }.getType();
                    ResultBean<Project> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        mView.showGetDetailSuccess(bean.getResult(), R.string.get_project_detail_success);
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
        };
    }

    @Override
    public void getProjectDetail(long id) {
        API.getProjectDetail(id, mHandler);
    }

    @Override
    public void getCommentCount(long id) {
        API.getProjectCommentCount(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // TODO: 2017/3/15
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<CommentCount>>() {
                    }.getType();
                    ResultBean<CommentCount> bean = new Gson().fromJson(responseString, type);
                    mView.showGetCommentCountSuccess(bean.getResult().commentCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void getProjectDetail(String name, String pathWithNamespace) {
        API.getProjectDetail(pathWithNamespace + "%2F" + name, mHandler);
    }

    @Override
    public String getShareUrl() {
        return String.format("https://git.oschina.net/%s/",
                mProject.getPathWithNamespace());
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
