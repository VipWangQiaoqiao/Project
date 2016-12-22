package net.oschina.app.improve.detail.v2;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.detail.general.AboutAdapter;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.improve.widget.SimplexToast;

import java.util.ArrayList;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class DetailFragment extends BaseFragment implements DetailContract.View, BaseRecyclerAdapter.OnItemClickListener {
    protected DetailContract.Presenter mPresenter;
    protected OWebView mWebView;
    protected RecyclerView mRecyclerView;
    protected AboutAdapter mAdapter;

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
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.rv_about);
        mAdapter = new AboutAdapter(getActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onItemClick(int position, long itemId) {

    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    @Override
    public void showGetDetailSuccess(SubBean bean) {
        if (mContext == null) return;
        bean.setAbouts(new ArrayList<About>());
        mAdapter.addAll(bean.getAbouts());
        mWebView.loadDetailDataAsync(bean.getBody(), (Runnable) mContext);
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

    @Override
    public void showCommentSuccess(Comment comment) {

    }

    @Override
    public void showCommentError(String message) {

    }

    public void scrollToBottom() {

    }
}
