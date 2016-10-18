package net.oschina.open.factory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import net.oschina.open.bean.Share;
import net.oschina.open.utils.OpenUtils;

/**
 * Created by fei
 * on 2016/10/15.
 * desc:
 */

public class OpenShare<T> extends ShareFactory {

    private String mAppKey;
    private String mAppId;

    private SendReqCallback mSendReqCallback;
    private IUiListener mIUiListener;

    public OpenShare<T> addIUiListener(IUiListener IUiListener) {
        mIUiListener = IUiListener;
        return this;
    }

    public OpenShare<T> addSendReqCallback(SendReqCallback sendReqCallback) {
        mSendReqCallback = sendReqCallback;

        return this;
    }

    public OpenShare<T> addAppKey(String appKey) {
        this.mAppKey = appKey;
        return this;
    }

    public OpenShare<T> addAppId(String appId) {
        this.mAppId = appId;
        return this;
    }

    @Override
    public OpenShare<T> toShare(Context context, Activity activity, int shareType, Share share) {

        switch (shareType) {
            case 0:
                //weChat
                IWXAPI iwxapi = initWeChat(context);
                shareWXWeb(context, share, iwxapi);
                break;
            case 1:
                //Tencent
                Tencent tencent = initTencent(context);
                shareTencentWeb(activity, share, tencent);
                break;
            case 2:
                //WeiBo
                IWeiboShareAPI weiboShareAPI = initWeiBo(context);
                shareWeiBoWeb(weiboShareAPI, activity, share);
                break;
            default:
                break;
        }

        return this;
    }

    /**
     * share weibo web style
     *
     * @param shareAPI shareAPI
     * @param activity activity
     * @param share    share
     */
    private void shareWeiBoWeb(IWeiboShareAPI shareAPI, Activity activity, Share share) {

        // 1. 初始化微博的分享消息
        // 分享网页
        WebpageObject webpageObject = new WebpageObject();

        webpageObject.identify = Utility.generateGUID();
        webpageObject.title = share.getTitle();
        webpageObject.description = share.getTitle();

        Bitmap bitmap = OpenUtils.getShareBitmap(activity.getApplicationContext(), share.getBitmapResID());
        // 设置 Bitmap 类型的图片到视频对象里         最好设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        webpageObject.setThumbImage(bitmap);
        webpageObject.actionUrl = share.getUrl();
        webpageObject.defaultText = " - " + share.getDefaultText();

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        weiboMessage.mediaObject = webpageObject;
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        shareAPI.sendRequest(activity, request);
    }


    /**
     * share tencent web  style
     *
     * @param activity activity
     * @param share    share
     * @param tencent  tencent
     */
    private void shareTencentWeb(Activity activity, Share share, Tencent tencent) {

        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, share.getTitle());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, share.getSummary());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, share.getUrl());
        params.putInt(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, share.getAppShareIcon());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, share.getAppName());

        tencent.shareToQQ(activity, params, mIUiListener);
    }

    /**
     * share WeChat  web style
     *
     * @param context context
     * @param share   share
     * @param iwxapi  iwxapi
     */
    private void shareWXWeb(Context context, Share share, IWXAPI iwxapi) {

        //1.初始化一个WXTextObject对象,填写分享的文本内容
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = share.getUrl();

        //2.用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.title = share.getTitle();
        msg.mediaObject = wxWebpageObject;
        msg.description = share.getDescription();

        Bitmap bitmap = OpenUtils.getShareBitmap(context, share.getBitmapResID());
        msg.setThumbImage(bitmap);

        //3.构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = OpenUtils.buildTransaction("webPage");
        //transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = share.getShareScene() == 0 ? SendMessageToWX.Req.WXSceneTimeline :
                SendMessageToWX.Req.WXSceneSession;

        //4.发送这次分享
        boolean sendReq = iwxapi.sendReq(req);
        //发送请求成功,回调
        if (mSendReqCallback != null) {
            mSendReqCallback.call(sendReq);
        }
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

    private IWeiboShareAPI initWeiBo(Context context) {
        IWeiboShareAPI weiBoShareSDK = WeiboShareSDK.createWeiboAPI(context, mAppKey, false);
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

    public interface SendReqCallback {
        void call(boolean call);
    }

    /**
     * 调用系统安装的应用分享
     *
     * @param context context
     * @param share   share
     */
    public static void showSystemShareOption(Context context, Share share) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + share.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, share.getTitle() + " " + share.getUrl());
        context.startActivity(Intent.createChooser(intent, "选择分享"));
    }
}
