package net.oschina.app.improve.detail.v2;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.comment.CommentView;
import net.oschina.app.improve.comment.OnCommentClickListener;
import net.oschina.app.improve.detail.general.AboutAdapter;
import net.oschina.app.improve.widget.OWebView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.UIHelper;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public abstract class DetailFragment extends BaseFragment implements
        DetailContract.View,
        BaseRecyclerAdapter.OnItemClickListener,
        View.OnClickListener {
    protected DetailContract.Presenter mPresenter;
    protected OWebView mWebView;
    protected RecyclerView mRecyclerView;
    protected AboutAdapter mAdapter;
    protected SubBean mBean;
    protected CommentView mCommentView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_detail_v2;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mWebView = (OWebView) mRoot.findViewById(R.id.webView);
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.rv_about);
        if (mRecyclerView == null)
            return;
        mAdapter = new AboutAdapter(getActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mCommentView = (CommentView) mRoot.findViewById(R.id.cv_comment);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(int position, long itemId) {
        About about = mAdapter.getItem(position);
        if (about == null)
            return;
        SubBean sub = new SubBean();
        sub.setId(about.getId());
        sub.setType(about.getType());
        sub.setStatistics(new SubBean.Statistics());
        sub.getStatistics().setComment(about.getCommentCount());
        sub.getStatistics().setView(about.getViewCount());
        switch (about.getType()) {
            case News.TYPE_SOFTWARE:
                net.oschina.app.improve.detail.general.SoftwareDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_QUESTION:
                net.oschina.app.improve.detail.general.QuestionDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_BLOG:
                net.oschina.app.improve.detail.general.BlogDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_TRANSLATE:
                net.oschina.app.improve.detail.general.NewsDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_EVENT:
                net.oschina.app.improve.detail.general.EventDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_NEWS:
                net.oschina.app.improve.detail.general.NewsDetailActivity.show(mContext, sub);
                break;
            default:
                UIHelper.showUrlRedirect(mContext, about.getHref());
                break;
        }
    }

    @Override
    public void setPresenter(DetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    @Override
    public void showGetDetailSuccess(SubBean bean) {
        this.mBean = bean;
        if (mContext == null) return;
        mWebView.loadDetailDataAsync(bean.getBody(), (Runnable) mContext);
        if (mAdapter == null)
            return;
        mAdapter.addAll(bean.getAbouts());
        if (mCommentView == null)
            return;
        mCommentView.setTitle(String.format("%s (%d)", getResources().getString(R.string.answer_hint), bean.getStatistics().getComment()));
        mCommentView.init(bean.getId(),
                bean.getType(),
                getCommentOrder(),
                bean.getStatistics().getComment(),
                getImgLoader(), (OnCommentClickListener) mContext);
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
}
