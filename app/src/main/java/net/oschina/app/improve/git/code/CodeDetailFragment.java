package net.oschina.app.improve.git.code;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
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

public class CodeDetailFragment extends BaseFragment implements CodeDetailContract.View {

    @Bind(R.id.tv_file_name)
    TextView mTextFileName;
    @Bind(R.id.webView)
    WebView mWebView;
    @Bind(R.id.ll_name)
    LinearLayout mLinearName;
    @Bind(R.id.line)
    View mLine;
    private SourceEditor mEditor;
    private CodeDetailContract.Presenter mPresenter;

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
    public void showLandscape() {
        mLine.setVisibility(View.GONE);
        mLinearName.setVisibility(View.GONE);
    }

    @Override
    public void showPortrait() {
        mLine.setVisibility(View.VISIBLE);
        mLinearName.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(CodeDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
