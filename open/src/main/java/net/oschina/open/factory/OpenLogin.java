package net.oschina.open.factory;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

/**
 * Created by fei
 * on 2016/10/15.
 * desc:
 */

public class OpenLogin<T> extends LoginFactory {

    private String mAppKey;
    private String mAppId;

    //注：关于授权回调页对移动客户端应用来说对用户是不可见的，所以定义为何种形式都将不影响，
    //so,请先确认是否设置了该url
    // 但是没有定义将无法使用 SDK 认证登录。
    // 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
    private String mRedirectUrl = "https://api.weibo.com/oauth2/default.html";

    private WeiboAuthListener mWeiboAuthListener;
    private IUiListener mIUiListener;


    public OpenLogin addRedirectUrl(String redirectUrl) {
        mRedirectUrl = redirectUrl;
        return this;
    }

    public OpenLogin addWeiboAuthListener(WeiboAuthListener weiboAuthListener) {
        mWeiboAuthListener = weiboAuthListener;
        return this;
    }

    public OpenLogin addIUiListener(IUiListener IUiListener) {
        mIUiListener = IUiListener;
        return this;
    }


    @SuppressWarnings("unchecked")
    @Override
    public T createOpen(Context context, int openType) throws ClassNotFoundException {

        switch (openType) {
            case 0:
                //WeChat
                return (T) initWeChat(context);
            case 1:
                //Tencent
                return (T) initTencent(context);
            case 2:
                //Weibo
                return (T) initWeiBo(context);
            default:
                throw new ClassNotFoundException("未找到该openSdk...");
        }
    }

    @Override
    public OpenLogin<T> addAppKey(String appKey) {
        this.mAppKey = appKey;
        return this;
    }

    @Override
    public OpenLogin<T> addAppId(String appId) {
        this.mAppId = appId;
        return this;
    }

    @Override
    public void toLogin(Context context, Activity activity, int loginType) throws NoSuchMethodException {
        switch (loginType) {
            case 0:
                //WeChat
                IWXAPI iwxapi = initWeChat(context);
                // 唤起微信登录授权
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_login";
                if (iwxapi != null) {
                    iwxapi.sendReq(req);
                } else {
                    throw new NullPointerException();
                }
                break;
            case 1:
                //Tencent
                Tencent tencent = initTencent(context);
                loginByActivity(activity, tencent);
                break;
            case 2:
                //Weibo
                // 创建授权认证信息
                String redirect_url = this.mRedirectUrl;
                AuthInfo authInfo = new AuthInfo(context, mAppKey, redirect_url, null);//scope 为null  将加速登录
                SsoHandler ssoHandler = new SsoHandler(activity, authInfo);
                ssoHandler.authorize(mWeiboAuthListener);
                break;
            default:
                throw new NoSuchMethodException("未找到该方法...");
        }

    }


    private void loginByActivity(Activity activity, Tencent tencent) {
        tencent.login(activity, "all", mIUiListener);
    }

    @Override
    public void toLogin(Context context, Fragment fragment, int loginType) throws NoSuchMethodException {

        switch (loginType) {
            case 0:
                //WeChat
                IWXAPI iwxapi = initWeChat(context);
                // 唤起微信登录授权
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_login";
                if (iwxapi != null)
                    iwxapi.sendReq(req);
                else
                    throw new NullPointerException();
                break;
            case 1:
                //Tencent
                Tencent tencent = initTencent(context);
                loginByFragment(fragment, tencent);
                break;
            case 2:
                //Weibo
                String redirect_url = this.mRedirectUrl;
                AuthInfo authInfo = new AuthInfo(context, mAppKey, redirect_url, null);
                SsoHandler ssoHandler = new SsoHandler(fragment.getActivity(), authInfo);
                ssoHandler.authorize(mWeiboAuthListener);
                break;
            default:
                throw new NoSuchMethodException("未找到该方法...");
        }
    }

    private void loginByFragment(Fragment fragment, Tencent tencent) {
        if (tencent != null && !tencent.isSessionValid()) {
            tencent.login(fragment, "all", mIUiListener);
        }
    }

    private IWeiboShareAPI initWeiBo(Context context) {
        IWeiboShareAPI weiBoShareSDK = WeiboShareSDK.createWeiboAPI(context, mAppKey);
        boolean weiBoAppInstalled = weiBoShareSDK.isWeiboAppInstalled();
        boolean weiBoAppSupportAPI = weiBoShareSDK.isWeiboAppSupportAPI();
        if (weiBoAppInstalled && weiBoAppSupportAPI) {
            boolean registerApp = weiBoShareSDK.registerApp();
            if (registerApp) {
                return weiBoShareSDK;
            }
        }
        return null;
    }

    private Tencent initTencent(Context context) {
        return Tencent.createInstance(mAppId, context);
    }

    private IWXAPI initWeChat(Context context) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, mAppId, false);
        boolean wxAppInstalled = iwxapi.isWXAppInstalled();
        boolean wxAppSupportAPI = iwxapi.isWXAppSupportAPI();
        if (wxAppInstalled && wxAppSupportAPI) {
            boolean registerApp = iwxapi.registerApp(mAppId);
            if (registerApp) {
                return iwxapi;
            }
        }
        return null;
    }

}
