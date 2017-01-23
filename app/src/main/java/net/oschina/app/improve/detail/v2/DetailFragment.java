package net.oschina.app.improve.detail.v2;

import android.annotation.SuppressLint;
import android.support.v4.widget.NestedScrollView;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.comment.CommentView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.utils.ReadedIndexCacheManager;
import net.oschina.app.improve.widget.DetailAboutView;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.improve.widget.ScreenView;
import net.oschina.app.improve.widget.SimplexToast;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public abstract class DetailFragment extends BaseFragment implements
        DetailContract.View,
        View.OnClickListener {
    protected DetailContract.Presenter mPresenter;
    protected OWebView mWebView;
    protected SubBean mBean;
    protected CommentView mCommentView;
    protected DetailAboutView mDetailAboutView;
    protected int CACHE_CATALOG;
    protected NestedScrollView mViewScroller;
    protected ScreenView mScreenView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail_v2;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mWebView = (OWebView) mRoot.findViewById(R.id.webView);
        mCommentView = (CommentView) mRoot.findViewById(R.id.cv_comment);
        mDetailAboutView = (DetailAboutView) mRoot.findViewById(R.id.lay_detail_about);
        mViewScroller = (NestedScrollView) mRoot.findViewById(R.id.lay_nsv);
        mScreenView = (ScreenView) mRoot.findViewById(R.id.screenView);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void showGetDetailSuccess(SubBean bean) {
        this.mBean = bean;
        if (mContext == null) return;
        mWebView.loadDetailDataAsync(bean.getBody(), (Runnable) mContext);

        if (mDetailAboutView != null) {
            mDetailAboutView.setAbout(bean.getAbouts(), bean.getType());
        }

        if (mCommentView == null || mBean.getType() == News.TYPE_TRANSLATE) {
            if (mCommentView != null) {
                mCommentView.setVisibility(View.GONE);
            }
            return;
        }
        SubBean.Statistics statistics = bean.getStatistics();
        if (statistics == null)
            return;
        mCommentView.setTitle(String.format("%s (%d)", getResources().getString(R.string.answer_hint), bean.getStatistics().getComment()));
        mCommentView.init(bean.getId(),
                bean.getType(),
                getCommentOrder(),
                bean.getStatistics().getComment(),
                getImgLoader(), (OnCommentClickListener) mContext);
    }

    public void onPageFinished() {
        if (mBean == null || mBean.getId() <= 0) return;
        final int index = ReadedIndexCacheManager.getIndex(getContext(), mBean.getId(),
                CACHE_CATALOG);
        if (index != 0) {
            if (mViewScroller == null)
                return;
            mViewScroller.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mViewScroller == null)
                        return;
                    mViewScroller.smoothScrollTo(0, index);
                }
            }, 250);
        }
    }

    @Override
    public void showFavReverseSuccess(boolean isFav, int strId) {
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }

    @Override
    public void showFavError() {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, "收藏失败");
    }

    @Override
    public void showNetworkError(int strId) {
        if (mContext == null)
            return;
        SimplexToast.show(mContext, mContext.getResources().getString(strId));
    }

    public void toShare(String title, String content, String url) {
        ((DetailActivity) mContext).toShare(title, content, url);
    }

    @Override
    public void showCommentSuccess(Comment comment) {
        if (mCommentView == null)
            return;
        mCommentView.init(mBean.getId(),
                mBean.getType(),
                getCommentOrder(),
                mBean.getStatistics().getComment(),
                getImgLoader(), (OnCommentClickListener) mContext);
    }

    @Override
    public void showCommentError(String message) {

    }

    @Override
    public void showAddRelationSuccess(boolean isRelation, int strId) {

    }

    @Override
    public void showAddRelationError() {
        SimplexToast.show(mContext, "关注失败");
    }

    protected String getExtraString(Object object) {
        return object == null ? "" : object.toString();
    }

    protected int getExtraInt(Object object) {
        return object == null ? 0 : Double.valueOf(object.toString()).intValue();
    }

    protected abstract int getCommentOrder();

    @Override
    public void onDestroy() {
        if (mBean != null && mBean.getId() > 0 && mViewScroller != null) {
            ReadedIndexCacheManager.saveIndex(getContext(), mBean.getId(), CACHE_CATALOG,
                    (mScreenView != null && mScreenView.isViewInScreen()) ? 0 : mViewScroller.getScrollY());
        }
        mWebView.destroy();
        super.onDestroy();
    }
}
