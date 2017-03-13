package net.oschina.app.improve.git.tree;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Project;
import net.oschina.app.improve.git.bean.Tree;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/13.
 */

class TreePresenter implements TreeContract.Presenter {
    private final TreeContract.View mView;
    private Project mProject;
    private String mBranch;
    private List<String> mPaths;

    TreePresenter(TreeContract.View mView, Project project) {
        this.mView = mView;
        this.mProject = project;
        mBranch = "master";
        mPaths = new ArrayList<>();
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        API.getCodeTree(mProject.getId(), getPath(), mBranch, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("responseString", responseString);
                try {
                    Type type = new TypeToken<List<Tree>>() {
                    }.getType();
                    List<Tree> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.size() != 0) {
                        mView.onRefreshSuccess(bean);
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
        // TODO: 2017/3/13
    }

    @Override
    public void getBranch() {
        // TODO: 2017/3/13
    }

    public String getPath() {
        StringBuilder sb = new StringBuilder();
        for (String s : mPaths) {
            sb.append(s + "/");
        }
        return sb.toString();
    }

    boolean isCanBack() {
        return mPaths.size() == 0;
    }

    @Override
    public Project getProject() {
        return mProject;
    }
}
