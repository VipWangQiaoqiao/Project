package net.oschina.open.factory;

import android.app.Activity;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

/**
 * Created by qiujuer
 * on 2016/11/1.
 */
public class OpenBuilder {
    private Activity activity;
    private String appKey;
    private String appId;

    public static OpenBuilder with(Activity activity) {
        OpenBuilder builder = new OpenBuilder();
        builder.activity = activity;
        return builder;
    }

    public TencentOperator useTencent(String appId) {
        this.appId = appId;

        Tencent tencent = Tencent.createInstance(this.appId, activity);
        return new TencentOperator(tencent);
    }

    public WeiboOperator useWeibo(String appKey) {
        this.appKey = appKey;

        AuthInfo authInfo = new AuthInfo(activity, this.appKey, "http://sns.whalecloud.com/sina2/callback", null);
        SsoHandler handler = new SsoHandler(activity, authInfo);
        return new WeiboOperator(handler);
    }

    public WechatOperator useWechat(String appId) {
        this.appId = appId;

        IWXAPI iwxapi = WXAPIFactory.createWXAPI(activity, this.appId, false);

        boolean wxAppInstalled = iwxapi.isWXAppInstalled();
        boolean wxAppSupportAPI = iwxapi.isWXAppSupportAPI();

        IWXAPI operator = null;
        if (wxAppInstalled && wxAppSupportAPI) {
            boolean registerApp = iwxapi.registerApp(this.appId);
            if (registerApp) {
                operator = iwxapi;
            }
        }
        return new WechatOperator(operator);
    }


    public class TencentOperator {
        Tencent tencent;

        TencentOperator(Tencent tencent) {
            this.tencent = tencent;
        }

        public Tencent login(IUiListener listener) {
            tencent.login(activity, "all", listener);
            return tencent;
        }

        public void share() {

        }
    }

    public class WeiboOperator {
        SsoHandler handler;

        WeiboOperator(SsoHandler handler) {
            this.handler = handler;
        }

        public SsoHandler login(WeiboAuthListener listener) {
            handler.authorize(listener);
            return handler;
        }

        public void share() {

        }
    }

    public class WechatOperator {
        IWXAPI iwxapi;

        WechatOperator(IWXAPI iwxapi) {
            this.iwxapi = iwxapi;
        }

        public void login(Runnable badRunnable) {
            if (iwxapi == null) {
                badRunnable.run();
                return;
            }
            // 唤起微信登录授权
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_login";
            if (iwxapi != null)
                iwxapi.sendReq(req);
            else
                badRunnable.run();
        }

        public void share() {

        }
    }
}
