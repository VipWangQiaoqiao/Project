package net.oschina.app.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;

import net.oschina.app.R;
import net.oschina.app.api.ApiHttpClient;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.Constants;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.util.TLog;

import cz.msebera.android.httpclient.Header;

/**
 * 微信回调的activity
 * Created by zhangdeyi on 15/7/27.
 */
public class WXEntryActivity extends Activity {

    public static final String EXTRA_LOGIN_WX = "extra_login_wx";
    public static final String ACTION_LOGIN_WX = "net.oschina.app.wx.action.wx_login";

    private static final String TAG = "WXEntryActivity";

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
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                TLog.log("Test", responseString);

                //这里是老板微信登录,新版完善了就可以删除掉
                // Intent intent = new Intent(OpenIdCatalog.WECHAT);
                //intent.putExtra(LoginBindActivityChooseActivity.BUNDLE_KEY_OPENIDINFO, openInfo);
                //  sendBroadcast(intent);

                Log.e(TAG, "onSuccess: ----------->" + responseString);

                //新版微信登录
                if (!TextUtils.isEmpty(responseString)) {

                    OSChinaApi.openLogin(2, responseString, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {

                            Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();

                        }
                    });

                }

            }
        });
    }

}
