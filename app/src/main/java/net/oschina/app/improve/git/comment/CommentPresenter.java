package net.oschina.app.improve.git.comment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Comment;
import net.oschina.app.improve.git.bean.Project;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/14.
 */

class CommentPresenter implements CommentContract.Presenter {
    private final CommentContract.View mView;
    private final CommentContract.Action mAction;
    private final Project mProject;
    private String mToken;

    CommentPresenter(CommentContract.View mView, CommentContract.Action mAction, Project mProject) {
        this.mView = mView;
        this.mProject = mProject;
        this.mAction = mAction;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        API.getProjectComments(mProject.getId(), "null", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PageBean<Comment>>>() {
                    }.getType();
                    ResultBean<PageBean<Comment>> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        mToken = bean.getResult().getNextPageToken();
                        List<Comment> list = bean.getResult().getItems();
                        if (list != null) {
                            mView.onRefreshSuccess(list);
                            if (list.size() < 20) {
                                mView.showMoreMore();
                            }
                        } else {
                            mView.showMoreMore();
                        }
                    } else {
                        mView.showMoreMore();
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
        API.getProjectComments(mProject.getId(), mToken, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
                mView.onComplete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<PageBean<Comment>>>() {
                    }.getType();
                    ResultBean<PageBean<Comment>> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        mToken = bean.getResult().getNextPageToken();
                        List<Comment> list = bean.getResult().getItems();
                        if (list != null && list.size() != 0) {
                            mView.onLoadMoreSuccess(list);
                        } else {
                            mView.showMoreMore();
                        }
                    } else {
                        mView.showMoreMore();
                    }
                    mView.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showMoreMore();
                    mView.onComplete();
                }
            }
        });
    }

    @Override
    public void addComment(String content) {
        API.addProjectComment(mProject, content, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showAddCommentFailure(R.string.pub_comment_failed);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<Comment>>() {
                    }.getType();
                    ResultBean<Comment> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        mAction.showAddCommentSuccess(bean.getResult(), R.string.pub_comment_success);
                        mView.showAddCommentSuccess(bean.getResult(), R.string.pub_comment_success);
                    } else {
                        mAction.showAddCommentFailure(R.string.pub_comment_failed);
                        mView.showAddCommentFailure(R.string.pub_comment_failed);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showAddCommentFailure(R.string.pub_comment_failed);
                }
            }
        });
    }
}
