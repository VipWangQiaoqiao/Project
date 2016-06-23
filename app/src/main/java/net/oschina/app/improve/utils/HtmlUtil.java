package net.oschina.app.improve.utils;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.oschina.app.util.HTMLUtil;
import net.oschina.app.util.UIHelper;

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

    public static void initWebView(WebView webView) {
        webView.setHorizontalScrollBarEnabled(false);
        UIHelper.initWebView(webView);
        UIHelper.addWebImageShow(webView.getContext(), webView);
    }

    public static void initWebViewDetailData(WebView webView, String content, Runnable finishCallback) {
        //String body = String.format(HTML_FRAME, HtmlUtil.TITLE_DETAIL + HtmlUtil.WEB_LOAD_IMAGES, content);
        String body = HTMLUtil.setupWebContent(content, true, true);
        webView.setWebViewClient(new WebClient(finishCallback));
        webView.loadDataWithBaseURL("", body, "text/html", "UTF-8", "");
    }

    private static class WebClient extends WebViewClient implements Runnable {
        private Runnable mFinishCallback;
        private boolean mDone = false;

        WebClient(Runnable finishCallback) {
            super();
            mFinishCallback = finishCallback;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mDone = false;
            // 当webview加载2秒后强制回馈完成
            view.postDelayed(this, 2800);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            run();
        }

        @Override
        public synchronized void run() {
            if (!mDone) {
                mDone = true;
                mFinishCallback.run();
            }
        }
    }
}
