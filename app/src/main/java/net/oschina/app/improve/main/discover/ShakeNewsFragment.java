package net.oschina.app.improve.main.discover;

import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/10/11.
 */

public class ShakeNewsFragment extends BaseSensorFragment {

    public static ShakeNewsFragment newInstance() {
        ShakeNewsFragment fragment = new ShakeNewsFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shake_news;
    }

    @Override
    public void onShake() {
        OSChinaApi.getShakeNews(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }
}
