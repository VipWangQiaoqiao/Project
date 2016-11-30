package net.oschina.app.improve.detail.v2;

import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.improve.widget.SimplexToast;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class DetailFragment extends BaseFragment implements DetailContract.View {
    protected DetailContract.Presenter mPresenter;
    protected OWebView mWebView;


    public static DetailFragment newInstance() {
        DetailFragment fragment = new DetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail_v2;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mWebView = (OWebView) mRoot.findViewById(R.id.webView);
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    @Override
    public void showGetDetailSuccess(SubBean bean) {
        if (mContext != null)
            mWebView.loadDetailDataAsync(bean.getBody(), (Runnable) mContext);
    }

    @Override
    public void showGetDetailError(String message) {
        SimplexToast.show(mContext, message);
    }

    @Override
    public void showFavReverseSuccess(boolean isFav, int strId) {
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }

    @Override
    public void showFavError() {
        SimplexToast.show(mContext, "收藏失败");
    }

    @Override
    public void showNetworkError(int strId) {
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }

    public void toShare(String title, String content, String url) {
        ((DetailActivity) mContext).toShare(title, content, url);
    }

    public void scrolloToBottom() {

    }
}
