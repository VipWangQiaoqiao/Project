package net.oschina.open.factory;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import net.oschina.open.bean.Share;
import net.oschina.open.utils.OpenUtils;

/**
 * Created by qiujuer
 * on 2016/11/1.
 */
public class OpenBuilder {
    private Activity activity;

    public static OpenBuilder with(Activity activity) {
        OpenBuilder builder = new OpenBuilder();
        builder.activity = activity;
        return builder;
    }

    public TencentOperator useTencent(String appId) {
        Tencent tencent = Tencent.createInstance(appId, activity);
        return new TencentOperator(tencent);
    }

    public WeiboOperator useWeibo(String appKey) {
        return new WeiboOperator(appKey);
    }

    public WechatOperator useWechat(String appId) {
        return new WechatOperator(appId);
    }


    public class TencentOperator {
        Tencent tencent;

        TencentOperator(Tencent tencent) {
            this.tencent = tencent;
        }

        public Tencent login(IUiListener listener, Callback callback) {
            int login = tencent.login(activity, "all", listener);
            return tencent;
        }

        public void share(Share share, IUiListener listener, Callback callback) {
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, share.getTitle());
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, share.getSummary());
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, share.getUrl());
            String shareIconUrl = share.getImageUrl();
            if (!TextUtils.isEmpty(shareIconUrl)) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareIconUrl);
            } else {
                params.putInt(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, share.getAppShareIcon());
            }
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, share.getAppName());
            if (callback != null) {
                if (tencent != null) {
                    try {
                        tencent.shareToQQ(activity, params, listener);
                        callback.onSuccess();
                    } catch (Exception e) {
                        callback.onFailed();
                    }
                } else {
                    callback.onFailed();
                }
            }
        }
    }

    public class WeiboOperator {
        String appKey;

        WeiboOperator(String appKey) {
            this.appKey = appKey;
        }

        public SsoHandler login(WeiboAuthListener listener) {
            AuthInfo authInfo = new AuthInfo(activity, appKey, "http://sns.whalecloud.com/sina2/callback", null);
            SsoHandler handler = new SsoHandler(activity, authInfo);
            handler.authorize(listener);
            return handler;
        }

        public void share(Share share, Callback callback) {
            IWeiboShareAPI weiBoShareSDK = WeiboShareSDK.createWeiboAPI(activity, appKey, false);
            if (!(weiBoShareSDK.isWeiboAppInstalled()
                    && weiBoShareSDK.isWeiboAppSupportAPI()
                    && weiBoShareSDK.registerApp())) {
                if (callback != null)
                    callback.onFailed();
                return;
            }

            // 1. 初始化微博的分享消息
            // 分享网页
            WebpageObject webpageObject = new WebpageObject();

            webpageObject.identify = Utility.generateGUID();
            webpageObject.title = share.getTitle();
            webpageObject.description = share.getTitle();

            Bitmap bitmap = share.getThumbBitmap();
            if (bitmap == null) {
                bitmap = OpenUtils.getShareBitmap(activity.getApplicationContext(), share.getBitmapResID());
            }

            // 设置 Bitmap 类型的图片到视频对象里         最好设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
            webpageObject.setThumbImage(bitmap);
            webpageObject.actionUrl = share.getUrl();
            webpageObject.defaultText = " - 开源中国";

            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

            weiboMessage.mediaObject = webpageObject;
            // 2. 初始化从第三方到微博的消息请求
            SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
            // 用transaction唯一标识一个请求
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.multiMessage = weiboMessage;

            // 3. 发送请求消息到微博，唤起微博分享界面
            if ((!weiBoShareSDK.sendRequest(activity, request)) && callback != null) {
                callback.onFailed();
                bitmap.recycle();
            } else {
                if (callback != null)
                    callback.onSuccess();
                bitmap.recycle();
            }
        }
    }

    public class WechatOperator {
        String appId;

        WechatOperator(String appId) {
            this.appId = appId;
        }

        public void login(Callback callback) {
            IWXAPI iwxapi = init();
            if (iwxapi == null) {
                if (callback != null)
                    callback.onFailed();
                return;
            }
            // 唤起微信登录授权
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_login";
            // 失败回调
            if (!iwxapi.sendReq(req) && callback != null) {
                callback.onFailed();
            } else {
                if (callback != null)
                    callback.onSuccess();
            }
        }

        public void shareSession(Share share, Callback callback) {
            share(share, SendMessageToWX.Req.WXSceneSession, callback);
        }

        public void shareTimeLine(Share share, Callback callback) {
            share(share, SendMessageToWX.Req.WXSceneTimeline, callback);
        }

        private IWXAPI init() {
            IWXAPI iwxapi = WXAPIFactory.createWXAPI(activity, appId, false);
            if (iwxapi.isWXAppInstalled() && iwxapi.isWXAppSupportAPI() && iwxapi.registerApp(appId)) {
                return iwxapi;
            }
            return null;
        }

        private void share(Share share, int scene, Callback callback) {
            IWXAPI iwxapi = init();
            if (iwxapi == null) {
                if (callback != null)
                    callback.onFailed();
                return;
            }

            //1.初始化一个WXTextObject对象,填写分享的文本内容
            WXWebpageObject wxWebpageObject = new WXWebpageObject();
            wxWebpageObject.webpageUrl = share.getUrl();
            wxWebpageObject.extInfo = share.getDescription();

            //2.用WXTextObject对象初始化一个WXMediaMessage对象
            WXMediaMessage msg = new WXMediaMessage();
            msg.title = share.getTitle();
            msg.mediaObject = wxWebpageObject;
            msg.description = share.getDescription();

            Bitmap bitmap = share.getThumbBitmap();
            if (bitmap == null) {
                bitmap = OpenUtils.getShareBitmap(activity, share.getBitmapResID());
            }
            msg.setThumbImage(bitmap);

            //3.构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = OpenUtils.buildTransaction("webPage");
            //transaction字段用于唯一标识一个请求
            req.message = msg;
            req.scene = scene;

            //4.发送这次分享
            boolean sendReq = iwxapi.sendReq(req);
            //发送请求失败,回调
            if (!sendReq && callback != null) {
                callback.onFailed();
                bitmap.recycle();
            } else {
                if (callback != null)
                    callback.onSuccess();
                bitmap.recycle();
            }
        }
    }

    public interface Callback {
        void onFailed();

        void onSuccess();
    }
}
