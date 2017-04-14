package net.oschina.app.improve.user.sign.up;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.EventDetail;
import net.oschina.app.improve.bean.EventSignIn;
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
        OSChinaApi.getEventDetail(id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("onSuccess",responseString);
                try {
                    ResultBean<EventDetail> resultBean = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<EventDetail>>() {
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
                Log.e("onSuccess",responseString);
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
                Log.e("onSuccess",responseString);
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
}
