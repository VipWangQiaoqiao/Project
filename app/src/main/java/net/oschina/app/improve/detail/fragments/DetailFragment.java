package net.oschina.app.improve.detail.fragments;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import net.oschina.app.R;
import net.oschina.app.improve.detail.contract.DetailContract;
import net.oschina.app.improve.fragments.base.BaseFragment;
import net.oschina.app.util.UIHelper;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public abstract class DetailFragment<Data, DataView extends DetailContract.View, Operator extends DetailContract.Operator<Data, DataView>> extends BaseFragment implements DetailContract.View {
    protected Operator mOperator;
    protected WebView mWebView;

    public Operator getOperator() {
        return mOperator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        this.mOperator = (Operator) context;
        this.mOperator.setDataView((DataView) this);
        super.onAttach(context);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initWebView(R.id.lay_webview);
    }

    void initWebView(@IdRes int layId) {
        WebView webView = new WebView(getActivity());
        webView.setHorizontalScrollBarEnabled(false);
        UIHelper.initWebView(webView);
        UIHelper.addWebImageShow(getActivity(), webView);
        ((ViewGroup) mRoot.findViewById(layId)).addView(webView);
        mWebView = webView;
    }

    void setBodyContent(String body){

    }

    @Override
    public void onDestroy() {
        WebView view = mWebView;
        if (view != null) {
            mWebView = null;
            view.getSettings().setJavaScriptEnabled(false);
            view.removeJavascriptInterface("mWebViewImageListener");
            view.removeAllViewsInLayout();
            view.setWebChromeClient(null);
            view.removeAllViews();
            view.destroy();
        }
        mOperator = null;

        super.onDestroy();
        super.onDestroy();
    }
}
