package net.oschina.app.improve.user.sign.in;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

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

class SignInPresenter implements SignInContract.Presenter {
    private final SignInContract.View mView;
    private final SignInContract.EmptyView mEmptyView;

    SignInPresenter(SignInContract.View mView, SignInContract.EmptyView mEmptyView) {
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
                try {
                    ResultBean<EventDetail> resultBean = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<EventDetail>>() {
                            }.getType());

                    if (resultBean.isSuccess()) {
                        mView.showGetDetailSuccess(resultBean.getResult());
                        getApplyInfo(id);
                    }else {
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

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<Map<String, String>> mapResultBean = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<Map<String, String>>>() {
                            }.getType());
                    if(mapResultBean.isSuccess()){
                        mapResultBean.getResult();
                        mEmptyView.hideEmptyLayout();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void signIn(long id) {
        OSChinaApi.eventSignin(id, "", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    ResultBean<EventSignIn> result = AppOperator.createGson().fromJson(responseString,
                            new TypeToken<ResultBean<EventSignIn>>() {
                            }.getType());
                    if (result.isSuccess()) {
                        EventSignIn eventSignIn = result.getResult();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
