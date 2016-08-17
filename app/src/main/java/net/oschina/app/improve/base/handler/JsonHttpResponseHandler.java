package net.oschina.app.improve.base.handler;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by thanatos on 16/8/15.
 */
public abstract class JsonHttpResponseHandler<T> extends TextHttpResponseHandler{

    private FragmentActivity activity;
    private Fragment fragment;

    public JsonHttpResponseHandler(FragmentActivity activity){
        this.activity = activity;
    }

    public JsonHttpResponseHandler(Fragment fragment){
        this(fragment.getActivity());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String resp) {
        if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && activity.isDestroyed()) return;
        if (fragment != null && fragment.isDetached()) return;
        T result = new GsonBuilder().create().fromJson(resp, new TypeToken<T>(){}.getType());
        onSuccess(statusCode, headers, result);
    }

    public abstract void onSuccess(int statusCode, Header[] headers, T result);
}
