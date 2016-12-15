package net.oschina.app.improve.main;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.widget.OWebView;

import butterknife.Bind;

public class BrowserActivity extends BaseActivity {
    @Bind(R.id.webView)
    protected OWebView mWebView;

    @Override
    protected int getContentView() {
        return R.layout.activity_browser;
    }

    @Override
    public void onResume() {
        super.onResume();
        OWebView webView = mWebView;
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        OWebView webView = mWebView;
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        OWebView view = mWebView;
        if (view != null) {
            mWebView = null;
            view.destroy();
        }
        super.onDestroy();
    }
}
