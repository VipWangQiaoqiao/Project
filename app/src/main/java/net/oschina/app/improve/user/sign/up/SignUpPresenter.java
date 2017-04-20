package net.oschina.app.improve.user.sign.up;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.EventSignIn;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.ui.empty.EmptyLayout;

import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2017/4/12.
 */

class SignUpPresenter implements SignUpContract.Presenter {
    private final SignUpContract.View mView;
    private final SignUpContract.EmptyView mEmptyView;

    SignUpPresenter(SignUpContract.View mView, SignUpContract.EmptyView mEmptyView) {
        this.mView = mView;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getEventDetail(final long id) {
        OSChinaApi.getDetail(5, "", id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<SubBean> resultBean = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<SubBean>>() {
                            }.getType());

                    if (resultBean.isSuccess()) {
                        mView.showGetDetailSuccess(resultBean.getResult());
                        getApplyInfo(id);
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
    public void getApplyInfo(long id) {
        OSChinaApi.syncSignUserInfo(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                mView.showNetworkError(R.string.state_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<Map<String, String>> mapResultBean = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<Map<String, String>>>() {
                            }.getType());
                    if (mapResultBean.isSuccess()) {
                        mapResultBean.getResult();
                        mView.showGetApplyInfoSuccess(mapResultBean.getResult());
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
    public void signUp(long id) {
        OSChinaApi.eventSignin(id, "", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<EventSignIn> result = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<EventSignIn>>() {
                            }.getType());
                    if (result.isSuccess()) {
                        EventSignIn eventSignIn = result.getResult();
                        mView.showSignInSuccess(eventSignIn);
                    } else {
                        mView.showSignInFailure(R.string.event_sign_in_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showSignInFailure(R.string.event_sign_in_error);
                }
            }
        });
    }

    @Override
    public void cancelApply(long id) {
        OSChinaApi.cancelApply(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.state_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<EventSignIn> result = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<EventSignIn>>() {
                            }.getType());
                    if (result.getCode() == 1) {
                        mView.showCancelApplySuccess(result.getMessage());
                    } else {
                        mView.showCancelApplyFailure(result.getMessage());
                    }
                } catch (Exception e) {
                    e.getMessage();
                    mView.showCancelApplyFailure("取消报名失败");
                }
            }
        });
    }
}
