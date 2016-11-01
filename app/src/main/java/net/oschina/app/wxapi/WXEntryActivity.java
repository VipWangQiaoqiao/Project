package net.oschina.app.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Constants;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.util.DialogHelp;

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
            mDialog = DialogHelp.getWaitDialog(this, message);
        }
        mDialog.setMessage(message);
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
        ApiHttpClient.getDirect(tokenUrl, new TextHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showWaitDialog();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //这里是老板微信登录,新版完善了就可以删除掉
                // Intent intent = new Intent(OpenIdCatalog.WECHAT);
                //intent.putExtra(LoginBindActivityChooseActivity.BUNDLE_KEY_OPENIDINFO, openInfo);
                //  sendBroadcast(intent);

                //新版微信登录
                if (!TextUtils.isEmpty(responseString)) {

                    OSChinaApi.openLogin(OSChinaApi.LOGIN_WECHART, responseString, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            throwable.printStackTrace();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            Type type = new TypeToken<ResultBean<User>>() {
                            }.getType();

                            ResultBean<User> resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                User user = resultBean.getResult();
                                AccountHelper.login(user, headers);
                                finishClearTopActivity(WXEntryActivity.this, MainActivity.class);
                                finish();
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

    /**
     * finish clearTop activity
     *
     * @param context       context
     * @param activityClass activityClass
     */
    private void finishClearTopActivity(Context context, Class activityClass) {
        // Kill and skip
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
