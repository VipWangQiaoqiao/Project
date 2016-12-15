package net.oschina.app.improve.pay.platform;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.util.Map;

/**
 * 支付宝支付
 * Created by thanatos on 2016/10/11.
 */

public class Alipay {

    public static final int PAY_RESULT_CODE_SUCCESS = 9000;
    public static final int PAY_RESULT_CODE_DEALING = 8000;
    public static final int PAY_RESULT_CODE_FAILURE = 4000;
    public static final int PAY_RESULT_CODE_REPEAT_REQUEST = 5000;
    public static final int PAY_RESULT_CODE_CANCEL = 6001;
    public static final int PAY_RESULT_CODE_INTERNET_ERROR = 6002;
    public static final int PAY_RESULT_CODE_UNKNOWN = 6004;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Map<String, String> result = (Map<String, String>) msg.obj;
            if (result == null) return;
            String status = result.get("resultStatus");
            if (TextUtils.isEmpty(status)) return;
            int code = Integer.valueOf(status);
            switch (code) {
                case PAY_RESULT_CODE_SUCCESS:
                    onSuccess();
                    break;
                case PAY_RESULT_CODE_DEALING:
                    onDealing();
                    break;
                case PAY_RESULT_CODE_FAILURE:
                    onFailure();
                    break;
                case PAY_RESULT_CODE_REPEAT_REQUEST:
                    onRepeatRequest();
                    break;
                case PAY_RESULT_CODE_CANCEL:
                    onCancel();
                    break;
                case PAY_RESULT_CODE_INTERNET_ERROR:
                    onInternetError();
                    break;
                case PAY_RESULT_CODE_UNKNOWN:
                    onUnknownError();
                    break;
            }
        }
    };

    public void pay(final Activity activity, final String payinfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                PayTask alipay = new PayTask(activity);
//                Map<String, String> result = alipay.payV2(payinfo, true);
//
//                Message msg = mHandler.obtainMessage();
//                msg.obj = result;
//                mHandler.sendMessage(msg);
            }
        }).start();
    }

    public void onSuccess() {
        // do when success
    }

    public void onDealing() {
        // pass
    }

    public void onFailure() {
        // pass
    }

    public void onRepeatRequest() {
        // repeat request result
    }

    public void onCancel() {
        // cancel pay
    }

    public void onInternetError() {
        // internet error
    }

    public void onUnknownError() {
        // other error unknown
    }

}
