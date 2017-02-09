package net.oschina.app.improve.pay.platform;

import android.app.Activity;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.oschina.app.util.TLog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by thanatos on 16/10/11.
 */

public class WXPay implements IWXAPIEventHandler {

    public static final int PAY_RESULT_CODE_SUCCESS = 0;
    public static final int PAY_RESULT_CODE_ERROR = -1;
    public static final int PAY_RESULT_CODE_CANCEL = -2;

    private static String APP_ID = "";
    private IWXAPI iwxapi;

    public WXPay(Activity context) {
        iwxapi = WXAPIFactory.createWXAPI(context, APP_ID, true);
        iwxapi.handleIntent(context.getIntent(), this);
    }

    private void pay(Map<String, String> map) {
        PayReq request = new PayReq();
        request.appId = APP_ID;
        request.partnerId = map.get("partnerId");
        request.prepayId = map.get("prepayId");
        request.packageValue = map.get("packageValue");
        request.nonceStr = map.get("nonceStr");
        request.timeStamp = map.get("timeStamp");
        request.sign = map.get("sign");
        iwxapi.sendReq(request);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() != ConstantsAPI.COMMAND_PAY_BY_WX) return;
        TLog.d("oschina", "微信支付成功");
        switch (resp.errCode) {
            case PAY_RESULT_CODE_SUCCESS:
                break;
            case PAY_RESULT_CODE_ERROR:
                break;
            case PAY_RESULT_CODE_CANCEL:
                break;
        }
    }

    public static List<Pair<String, String>> decode(String info) {
        List<Pair<String, String>> list = new ArrayList<>();
        if (TextUtils.isEmpty(info)) return list;
        String[] kv = info.split("&");
        String[] k;
        for (String v : kv) {
            k = v.split("=");
            if (k.length != 2) continue;
            try {
                list.add(Pair.create(k[0], URLEncoder.encode(k[1], "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                TLog.d("oschina", "encode url parameter error");
            }
        }
        return list;
    }

    public void onSuccess() {
        // when success
    }

    public void onError() {
        // error
    }

    public void onCancel() {
        // user cancel pay the order
    }
}
