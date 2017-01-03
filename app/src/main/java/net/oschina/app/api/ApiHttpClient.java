package net.oschina.app.api;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.oschina.app.AppContext;
import net.oschina.app.Setting;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.util.TLog;
import net.oschina.common.verify.Verifier;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.protocol.HttpContext;

@SuppressWarnings("WeakerAccess")
public class ApiHttpClient {
    public final static String HOST = "www.oschina.net";
    //public final static String HOST = "www.oschina.tk";
    public static String API_URL = "https://www.oschina.net/%s";

    private static AsyncHttpClient CLIENT;

    private ApiHttpClient() {
    }

    /**
     * 初始化网络请求，包括Cookie的初始化
     *
     * @param context AppContext
     */
    public static void init(Application context) {
        API_URL = Setting.getServerUrl(context) + "%s";
        CLIENT = null;
        AsyncHttpClient client = new AsyncHttpClient();
        //client.setCookieStore(new PersistentCookieStore(context));
        // Set
        ApiHttpClient.setHttpClient(client, context);
        // Set Cookie
        setCookieHeader(AccountHelper.getCookie());
    }

    public static AsyncHttpClient getHttpClient() {
        return CLIENT;
    }

    public static void delete(String partUrl, AsyncHttpResponseHandler handler) {
        CLIENT.delete(getAbsoluteApiUrl(partUrl), handler);
        log("DELETE " + partUrl);
    }

    public static void get(String partUrl, AsyncHttpResponseHandler handler) {
        CLIENT.get(getAbsoluteApiUrl(partUrl), handler);
        log("GET " + partUrl);
    }

    public static void get(String partUrl, RequestParams params,
                           AsyncHttpResponseHandler handler) {
        CLIENT.get(getAbsoluteApiUrl(partUrl), params, handler);
        log("GET " + partUrl + "?" + params);
    }

    public static String getAbsoluteApiUrl(String partUrl) {
        String url = partUrl;
        if (!partUrl.startsWith("http:") && !partUrl.startsWith("https:")) {
            url = String.format(API_URL, partUrl);
        }
        log("request:" + url);
        return url;
    }

    public static void getDirect(String url, AsyncHttpResponseHandler handler) {
        CLIENT.get(url, handler);
        log("GET " + url);
    }

    public static void log(String log) {
        TLog.log("ApiHttpClient", log);
    }

    public static void post(String partUrl, AsyncHttpResponseHandler handler) {
        CLIENT.post(getAbsoluteApiUrl(partUrl), handler);
        log("POST " + partUrl);
    }

    public static void post(String partUrl, RequestParams params,
                            AsyncHttpResponseHandler handler) {
        CLIENT.post(getAbsoluteApiUrl(partUrl), params, handler);
        log("POST " + partUrl + "?" + params);
    }

    public static void put(String partUrl, AsyncHttpResponseHandler handler) {
        CLIENT.put(getAbsoluteApiUrl(partUrl), handler);
        log("PUT " + partUrl);
    }

    public static void put(String partUrl, RequestParams params,
                           AsyncHttpResponseHandler handler) {
        CLIENT.put(getAbsoluteApiUrl(partUrl), params, handler);
        log("PUT " + partUrl + "?" + params);
    }

    public static void setHttpClient(AsyncHttpClient c, Application application) {
        c.addHeader("Accept-Language", Locale.getDefault().toString());
        c.addHeader("Host", HOST);
        c.addHeader("Connection", "Keep-Alive");
        //noinspection deprecation
        c.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        // Set AppToken
        c.addHeader("AppToken", Verifier.getPrivateToken(application));
        // setUserAgent
        c.setUserAgent(ApiClientHelper.getUserAgent(AppContext.getInstance()));
        CLIENT = c;
        initSSL(CLIENT);
    }

    public static void setCookieHeader(String cookie) {
        if (!TextUtils.isEmpty(cookie))
            CLIENT.addHeader("Cookie", cookie);
        log("setCookieHeader:" + cookie);
    }

    /**
     * 销毁当前AsyncHttpClient 并重新初始化网络参数，初始化Cookie等信息
     *
     * @param appContext AppContext
     */
    public static void destroyAndRestore(Application appContext) {
        cleanCookie();
        CLIENT = null;
        init(appContext);
    }

    public static void cleanCookie() {
        // first clear store
        // new PersistentCookieStore(AppContext.getInstance()).clear();
        // clear header
        AsyncHttpClient client = CLIENT;
        if (client != null) {
            HttpContext httpContext = client.getHttpContext();
            CookieStore cookies = (CookieStore) httpContext
                    .getAttribute(HttpClientContext.COOKIE_STORE);
            // 清理Async本地存储
            if (cookies != null) {
                cookies.clear();
            }
            // 清理当前正在使用的Cookie
            client.removeHeader("Cookie");
        }
        log("cleanCookie");
    }

    /**
     * 从AsyncHttpClient自带缓存中获取CookieString
     *
     * @param client AsyncHttpClient
     * @return CookieString
     */
    private static String getClientCookie(AsyncHttpClient client) {
        String cookie = "";
        if (client != null) {
            HttpContext httpContext = client.getHttpContext();
            CookieStore cookies = (CookieStore) httpContext
                    .getAttribute(HttpClientContext.COOKIE_STORE);

            if (cookies != null && cookies.getCookies() != null && cookies.getCookies().size() > 0) {
                for (Cookie c : cookies.getCookies()) {
                    cookie += (c.getName() + "=" + c.getValue()) + ";";
                }
            }
        }
        log("getClientCookie:" + cookie);
        return cookie;
    }

    /**
     * 得到当前的网络请求Cookie，
     * 登录后触发
     *
     * @param headers Header
     */
    public static String getCookie(Header[] headers) {
        String cookie = getClientCookie(ApiHttpClient.getHttpClient());
        if (TextUtils.isEmpty(cookie)) {
            cookie = "";
            if (headers != null) {
                for (Header header : headers) {
                    String key = header.getName();
                    String value = header.getValue();
                    if (key.contains("Set-Cookie"))
                        cookie += value + ";";
                }
                if (cookie.length() > 0) {
                    cookie = cookie.substring(0, cookie.length() - 1);
                }
            }
        }

        log("getCookie:" + cookie);
        return cookie;
    }

    private static void initSSL(AsyncHttpClient client) {
        try {
            /// We initialize a default Keystore
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // We load the KeyStore
            trustStore.load(null, null);
            // We initialize a new SSLSocketFacrory
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            // We set that all host names are allowed in the socket factory
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            // We set the SSL Factory
            client.setSSLSocketFactory(socketFactory);
            // We initialize a GET http request
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        @SuppressWarnings("WeakerAccess")
        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
}
