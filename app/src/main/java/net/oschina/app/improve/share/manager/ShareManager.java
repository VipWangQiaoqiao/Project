package net.oschina.app.improve.share.manager;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import net.oschina.app.R;
import net.oschina.app.improve.share.bean.Share;
import net.oschina.app.improve.share.constant.OpenConstant;

/**
 * Created by fei on 2016/10/11.
 * desc:
 */

public class ShareManager {

    private static final String TAG = "ShareManager";
    private IWXAPI mIWXAPI;
    private Tencent mTencent;

    public ShareManager() {
    }

    public ShareManager registerWeChatShare(Context context) {

        IWXAPI iwxapi = WXAPIFactory.createWXAPI(context, OpenConstant.WECHAT_APP_ID, true);
        iwxapi.registerApp(OpenConstant.WECHAT_APP_ID);

        this.mIWXAPI = iwxapi;
        return this;
    }

    public ShareManager registerQQShare(Context context) {
        this.mTencent = Tencent.createInstance(OpenConstant.QQ_APP_ID, context);
        return this;
    }

    public ShareManager registerSinaShare(Context context, Activity activity, Share share) {

        // 创建微博分享接口实例
        IWeiboShareAPI weiBoAPI = WeiboShareSDK.createWeiboAPI(context, OpenConstant.WB_APP_KEY);

        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        boolean registerApp = weiBoAPI.registerApp();
        if (registerApp) {
            shareWeiBoWeb(weiBoAPI, activity, share);
        }

        return this;
    }


    public void shareWechatWeb(Context context, Share share) {

        //1.初始化一个WXTextObject对象,填写分享的文本内容
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = share.getUrl();

        //2.用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.title = share.getTitle();
        msg.mediaObject = wxWebpageObject;
        msg.description = "这是一个网页....";

        Bitmap bitmap = getShareBitmap(context);
        msg.setThumbImage(bitmap);

        //3.构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webPage");
        //transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = share.getShareScene() == 0 ? SendMessageToWX.Req.WXSceneTimeline :
                SendMessageToWX.Req.WXSceneSession;

        //4.发送这次分享
        mIWXAPI.sendReq(req);
    }

    public void shareQQWeb(Activity context, Share share) {

        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, share.getTitle());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, share.getContent());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, share.getUrl());
        params.putInt(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, R.mipmap.ic_share);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getResources().getString(R.string
                .app_name));

        mTencent.shareToQQ(context, params, null);
    }

    public void shareWeiBoWeb(IWeiboShareAPI shareAPI, Activity activity, Share share) {

        // 1. 初始化微博的分享消息
        // 分享网页
        WebpageObject webpageObject = new WebpageObject();

        webpageObject.identify = Utility.generateGUID();
        webpageObject.title = share.getTitle();
        webpageObject.description = share.getTitle();

        Bitmap bitmap = getShareBitmap(activity.getApplicationContext());
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        webpageObject.setThumbImage(bitmap);
        webpageObject.actionUrl = share.getUrl();
        webpageObject.defaultText = " -  开源中国客户端";

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


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System
                .currentTimeMillis();
    }

    private Bitmap getShareBitmap(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_share);
    }


}
