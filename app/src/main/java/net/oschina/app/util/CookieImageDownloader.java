package net.oschina.app.util;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * 为Picasso设置网络请求头中的cookie
 * Created by kymjs on 2/25/16.
 */
public class CookieImageDownloader extends UrlConnectionDownloader {

    private String cookie;

    public CookieImageDownloader(Context context, String cookie) {
        super(context);
        this.cookie = cookie;
    }

    @Override
    protected HttpURLConnection openConnection(Uri path) throws IOException {
        HttpURLConnection conn = super.openConnection(path);
        conn.setRequestProperty("Cookie", cookie);
        return conn;
    }
}
