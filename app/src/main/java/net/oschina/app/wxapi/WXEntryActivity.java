package net.oschina.app.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Constants;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.base.AccountBaseActivity;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.helper.ContactsCacheManager;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.util.TDevice;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;


/**
 * created by fei
 * desc:
 */
public class WXEntryActivity extends Activity {

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin_entry);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * show WaitDialog
     *
     * @return progressDialog
     */
    protected ProgressDialog showWaitDialog() {
        String message = getResources().getString(R.string.progress_submit);
        if (mDialog == null) {
            mDialog = DialogHelper.getProgressDialog(this, message);
        }
        mDialog.show();

        return mDialog;
    }

    /**
     * hide waitDialog
     */
    protected void hideWaitDialog() {
        ProgressDialog dialog = mDialog;
        if (dialog != null) {
            mDialog = null;
            try {
                dialog.cancel();
                // dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {

            //用户同意
            String code = resp.code;
            String state = resp.state;
            // 如果不是登录
            if (!"wechat_login".equals(state)) {
                finish();
            } else {
                //上面的code就是接入指南里要拿到的code
                getAccessTokenAndOpenId(code);
            }

        } else {
            finish();
        }
    }

    // 使用code获取微信的access_token和openid
    private void getAccessTokenAndOpenId(String code) {
        //   final ProgressDialog waitDialog = DialogHelp.getWaitDialog(this, "加载中...");
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&grant_type=authorization_code&code=%s";
        String tokenUrl = String.format(url, Constants.WEICHAT_APPID, Constants.WEICHAT_SECRET, code);

        if (!HasInternet()) return;

        ApiHttpClient.getDirect(tokenUrl, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaitDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                requestFailureHint(throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                if (!HasInternet()) return;

                //新版微信登录
                if (!TextUtils.isEmpty(responseString)) {

                    OSChinaApi.openLogin(OSChinaApi.LOGIN_WECHAT, responseString, new TextHttpResponseHandler() {

                        @Override
                        public void onStart() {
                            super.onStart();
                            // showWaitDialog();
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            //hideWaitDialog();
                        }

                        @Override
                        public void onCancel() {
                            super.onCancel();
                            //hideWaitDialog();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            requestFailureHint(throwable);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            Type type = new TypeToken<ResultBean<User>>() {
                            }.getType();

                            ResultBean<User> resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                User user = resultBean.getResult();
                                if (AccountHelper.login(user, headers)) {
                                    setResult(RESULT_OK);
                                    finish();
                                    Intent intent = new Intent();
                                    intent.setAction(AccountBaseActivity.ACTION_ACCOUNT_FINISH_ALL);
                                    LocalBroadcastManager.getInstance(WXEntryActivity.this).sendBroadcast(intent);
                                    ContactsCacheManager.sync();
                                } else {
                                    AppContext.showToast("登录异常");
                                }
                            } else {
                                AppContext.showToast(resultBean.getMessage(), Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideWaitDialog();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                hideWaitDialog();
            }
        });
    }

    private boolean HasInternet() {
        if (!TDevice.hasInternet()) {
            AppContext.showToast(R.string.tip_network_error, Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }


    /**
     * request network error
     *
     * @param throwable throwable
     */
    private void requestFailureHint(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }
        AppContext.showToast(getResources().getString(R.string.request_error_hint));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
