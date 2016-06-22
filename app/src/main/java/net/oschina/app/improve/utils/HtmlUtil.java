package net.oschina.app.improve.utils;

import android.webkit.WebView;

import net.oschina.app.util.HTMLUtil;

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
        //String body = String.format(HTML_FRAME, HtmlUtil.TITLE_DETAIL + HtmlUtil.WEB_LOAD_IMAGES, content);
        String body = HTMLUtil.setupWebContent(content, true, true);
        webView.loadDataWithBaseURL("", body, "text/html", "UTF-8", "");
    }
}
