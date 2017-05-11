package net.oschina.app.improve.git.code;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.git.bean.CodeDetail;
import net.oschina.app.improve.git.utils.MarkdownUtils;
import net.oschina.app.improve.git.utils.SourceEditor;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2017/3/13.
 */

public class CodeDetailFragment extends BaseFragment implements CodeDetaiContract.View {

    @Bind(R.id.tv_file_name)
    TextView mTextFileName;
    @Bind(R.id.webView)
    WebView mWebView;
    private SourceEditor mEditor;
    private CodeDetaiContract.Presenter mPresenter;

    static CodeDetailFragment newInstance(String fileName) {
        CodeDetailFragment fragment = new CodeDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("fileName", fileName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_code_detail;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initData() {
        super.initData();
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDefaultFontSize(10);
        settings.setAllowContentAccess(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
        });
        mEditor = new SourceEditor(mWebView);
        mTextFileName.setText(getArguments().getString("fileName"));
        mPresenter.getCodeDetail();
    }

    @Override
    public void showNetworkError(int strId) {

    }

    @Override
    public void showGetCodeSuccess(CodeDetail detail) {
        String pathName = getArguments().getString("fileName");
        mEditor.setMarkdown(MarkdownUtils.isMarkdown(pathName));
        mEditor.setSource(pathName, detail);
    }

    @Override
    public void showGetCodeFailure(int strId) {

    }

    @Override
    public void setPresenter(CodeDetaiContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
