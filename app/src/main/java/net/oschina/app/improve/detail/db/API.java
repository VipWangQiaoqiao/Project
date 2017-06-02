package net.oschina.app.improve.detail.db;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.BuildConfig;

/**
 * 阅读习惯接口
 * Created by haibin on 2017/5/23.
 */
public final class API {
    private static final String URL = "http://61.145.122.155:8080/apiv2/user_behaviors_collect/add";

    public static void addBehaviors(String json, TextHttpResponseHandler handler) {
        RequestParams params = new RequestParams("json", json);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("passcode", BuildConfig.VIOLET_PASSCODE);
        client.post(URL, params, handler);
    }
}
