package net.oschina.app.improve.git.comment;

import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Project;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/14.
 */

public class CommentPresenter implements CommentContract.Presenter {
    private final CommentContract.View mView;
    private final Project mProject;

    public CommentPresenter(CommentContract.View mView, Project mProject) {
        this.mView = mView;
        this.mProject = mProject;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        API.getProjectComments(mProject.getId(), "null", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }

    @Override
    public void onLoadMore() {

    }
}
