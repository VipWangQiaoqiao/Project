package net.oschina.app.improve.git.code;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.CodeDetail;
import net.oschina.app.improve.git.bean.Project;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/13.
 */

class CodeDetailPresenter implements CodeDetailContract.Presenter {
    private final CodeDetailContract.View mView;
    private String mFileName;
    private Project mProject;
    private String mBranch;

    CodeDetailPresenter(CodeDetailContract.View mView,
                        Project project,
                        String mFileName,
                        String mBranch) {
        this.mView = mView;
        this.mProject = project;
        this.mFileName = mFileName;
        this.mBranch = mBranch;
        this.mView.setPresenter(this);
    }

    @Override
    public void getCodeDetail() {
        API.getCodeDetail(mProject.getId(), mFileName, mBranch, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<CodeDetail>() {
                    }.getType();
                    CodeDetail bean = new Gson().fromJson(responseString, type);
                    if (bean != null) {
                        mView.showGetCodeSuccess(bean);
                    } else {
                        mView.showGetCodeFailure(R.string.get_code_failure);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.state_network_error);
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


    @Override
    public String getShareUrl() {
        return String.format("https://git.oschina.net/%s/blob/%s/%s",
                mProject.getPathWithNamespace(),
                mBranch,
                mFileName);
    }
}
