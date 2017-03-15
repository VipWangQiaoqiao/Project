package net.oschina.app.improve.git.branch;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.git.api.API;
import net.oschina.app.improve.git.bean.Branch;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/3/13.
 */

class BranchPresenter implements BranchContract.Presenter {
    private final BranchContract.View mView;

    BranchPresenter(BranchContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getBranches(long id) {
        API.getProjectBranches(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showGetBranchFailure(R.string.state_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<List<Branch>>() {
                    }.getType();
                    List<Branch> bean = new Gson().fromJson(responseString, type);
                    if (bean != null && bean.size() != 0) {
                        mView.showGetBranchSuccess(bean);
                    } else {
                        mView.showGetBranchFailure(R.string.state_network_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showNetworkError(R.string.state_network_error);
                }
            }
        });
    }
}
