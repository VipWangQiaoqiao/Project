package net.oschina.app.improve.utils;

import android.util.Log;
import android.webkit.WebView;

/**
 * Created by JuQiu
 * on 16/6/21.
 */

public final class HtmlUtil {

    public static final String HTML_FRAME = "<!DOCTYPE HTML><html><head>%s</head><body><div class=\"body-content\">%s</div></body></html>";

    public static final String TITLE_DETAIL = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/client.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/detail_page.js\"></script>"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>"
            + "<script type=\"text/javascript\">function showImagePreview(var url){window.location.url= url;}</script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/css/common_detail.css\">";

    public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";


    public static final void initDetailView(WebView webView, String content) {
        String body = String.format(HTML_FRAME, HtmlUtil.TITLE_DETAIL + HtmlUtil.WEB_LOAD_IMAGES, content);

        /*
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLog("WebViewClient.onPageStarted:" + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                showLog("WebViewClient.onPageFinished:" + url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                showLog("WebViewClient.onLoadResource:" + url);
            }
        });
        */

        webView.loadDataWithBaseURL("", body, "text/html", "UTF-8", "");
    }

    private static void showLog(String log) {
        Log.e(HtmlUtil.class.getName(), log);
    }
}
