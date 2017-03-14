package net.oschina.app.improve.git.detail;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Project;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/9.
 */

class ProjectDetailPresenter implements ProjectDetailContract.Presenter {
    private final ProjectDetailContract.View mView;
    private TextHttpResponseHandler mHandler;

    ProjectDetailPresenter(ProjectDetailContract.View mView) {
        this.mView = mView;
        initHandler();
        this.mView.setPresenter(this);
    }

    private void initHandler() {
        mHandler = new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
                throwable.printStackTrace();
                Log.e("re","   --    " + responseString + " --  " + statusCode + "  ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("detail", responseString);
                try {
                    Type type = new TypeToken<ResultBean<Project>>() {
                    }.getType();
                    ResultBean<Project> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.isSuccess()) {
                        mView.showGetDetailSuccess(bean.getResult(), R.string.get_project_detail_success);
                    } else {
                        mView.showNetworkError(R.string.state_network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.state_network_error);
                }
            }
        };
    }

    @Override
    public void getProjectDetail(long id) {
        API.getProjectDetail(id, mHandler);
    }

    @Override
    public void getProjectDetail(String name, String pathWithNamespace) {
        API.getProjectDetail(pathWithNamespace + "%2F" + name, mHandler);
    }
}
