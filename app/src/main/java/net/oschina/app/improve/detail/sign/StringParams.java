package net.oschina.app.improve.detail.sign;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

/**
 * Created by haibin
 * on 2016/12/8.
 */

public class StringParams extends RequestParams {
    private IdentityHashMap<Param, String> mParams = new IdentityHashMap<>();

    public void putForm(String key, String value) {
        mParams.put(new Param(key), value);
    }

    @Override
    public HttpEntity getEntity(ResponseHandlerInterface progressHandler) throws IOException {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(getParamsList(), contentEncoding);
        return entity;
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for (IdentityHashMap.Entry<Param, String> entry : mParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey().name, entry.getValue()));
        }
        return lparams;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private static class Param {
        private String name;

        public Param(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
