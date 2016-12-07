package net.oschina.app.improve.detail.sign;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.SignUpEventOptions;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/12/5.
 */

public class SignUpPresenter implements SignUpContract.Presenter {
    private final SignUpContract.View mView;
    private List<SignUpEventOptions> mOptions;
    private final SignUpContract.EmptyView mEmptyView;

    public SignUpPresenter(SignUpContract.View mView, SignUpContract.EmptyView mEmptyView) {
        this.mView = mView;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getSignUpOptions(long sourceId) {
        OSChinaApi.getSignUpOptions(sourceId, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("res",responseString);
                Type type = new TypeToken<ResultBean<List<SignUpEventOptions>>>() {
                }.getType();
                ResultBean<List<SignUpEventOptions>> resultBean = AppOperator.createGson().fromJson(responseString, type);
                if (resultBean.isSuccess()) {
                    mOptions = resultBean.getResult();
                    mView.showGetSignUpOptionsSuccess(resultBean.getResult());
                    mEmptyView.hideEmptyLayout();
                } else {
                    mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                }
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                    mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                }
            }
        });
    }

    @Override
    public void signUpEvent(long sourceId) {
        if (!check())
            return;
        OSChinaApi.signUpEvent(sourceId, mOptions, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("res", responseString);
//                try {
//                    Type type = new TypeToken<ResultBean<List<SignUpEventOptions>>>() {
//                    }.getType();
//                    ResultBean<List<SignUpEventOptions>> resultBean = AppOperator.createGson().fromJson(responseString, type);
//                    if (resultBean.isSuccess()) {
//                        mOptions = resultBean.getResult();
//                        mView.showGetSignUpOptionsSuccess(resultBean.getResult());
//                        mEmptyView.hideEmptyLayout();
//                    } else {
//                        mEmptyView.showErrorLayout(EmptyLayout.NODATA);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    mEmptyView.showErrorLayout(EmptyLayout.NODATA);
//                }
            }
        });
    }

    private boolean check() {
        for (SignUpEventOptions options : mOptions) {
            if (options.isRequired() && TextUtils.isEmpty(options.getValue()) && TextUtils.isEmpty(options.getDefaultValue())) {
                mView.showInputEmpty(options.getLabel() + "不能为空");
                return false;
            }
        }
        return true;
    }
}
