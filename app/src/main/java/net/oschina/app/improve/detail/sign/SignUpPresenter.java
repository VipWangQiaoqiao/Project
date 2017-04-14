package net.oschina.app.improve.detail.sign;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.bean.SignUpEventOptions;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/12/5.
 */

class SignUpPresenter implements SignUpContract.Presenter {
    private final SignUpContract.View mView;
    private List<SignUpEventOptions> mOptions;
    private final SignUpContract.EmptyView mEmptyView;

    SignUpPresenter(SignUpContract.View mView, SignUpContract.EmptyView mEmptyView) {
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
                try {
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

        OSChinaApi.signUpEvent(sourceId, getOptions(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<EventDetail>>() {
                    }.getType();
                    ResultBean<EventDetail> resultBean = AppOperator.createGson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        mView.showSignUpSuccess(resultBean.getResult());
                    } else {
                        mView.showSignUpError(resultBean.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showSignUpError("报名失败");
                }
            }
        });
    }

    private boolean check() {
        for (SignUpEventOptions options : mOptions) {
            if (options.isRequired() && options.getSelectList() != null) {
                if (options.getSelectList().size() == 0) {
                    mView.showInputEmpty(options.getLabel() + "不能为空");
                    return false;
                }
            }
            if (options.isRequired() && options.getValue() == null && TextUtils.isEmpty(options.getDefaultValue()) && options.getFormType() != SignUpEventOptions.FORM_TYPE_CHECK_BOX) {
                mView.showInputEmpty(options.getLabel() + "不能为空");
                return false;
            }
        }
        return true;
    }

    private List<SignUpEventOptions> getOptions() {
        List<SignUpEventOptions> ops = new ArrayList<>();
        for (SignUpEventOptions options : mOptions) {
            if (options.getSelectList() == null) {
                ops.add(options);
            } else {
                for (String s : options.getSelectList()) {
                    SignUpEventOptions sign = new SignUpEventOptions();
                    sign.setKey(options.getKey());
                    sign.setValue(s);
                    ops.add(sign);
                }
            }
        }
        return ops;
    }
}
