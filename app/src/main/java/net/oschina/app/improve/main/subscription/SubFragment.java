package net.oschina.app.improve.main.subscription;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.detail.activities.EventDetailActivity;
import net.oschina.app.improve.detail.activities.NewsDetailActivity;
import net.oschina.app.improve.detail.activities.QuestionDetailActivity;
import net.oschina.app.improve.detail.activities.SoftwareDetailActivity;
import net.oschina.app.improve.detail.activities.TranslateDetailActivity;
import net.oschina.app.improve.widget.banner.EventHeaderView;
import net.oschina.app.improve.widget.banner.HeaderView;
import net.oschina.app.improve.widget.banner.NewsHeaderView;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class SubFragment extends BaseRecyclerViewFragment<SubBean> {

    @Bind(R.id.appBar)
    AppBarLayout mAppBar;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    private SubTab mTab;
    private HeaderView mHeaderView;


    public static SubFragment newInstance(Context context, SubTab subTab) {
        SubFragment fragment = new SubFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_tab", subTab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sub;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mTab = (SubTab) bundle.getSerializable("sub_tab");
    }

    @Override
    public void initData() {
        super.initData();
        if (mTab.getBanner() != null) {
            mHeaderView = mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_NEWS ?
                    new NewsHeaderView(mContext, getImgLoader(), mTab.getHref()) :
                    new EventHeaderView(mContext, getImgLoader(), mTab.getHref());
            mAppBar.addView(mHeaderView);
        }
    }

    @Override
    public void onItemClick(int position, long itemId) {
        SubBean sub = mAdapter.getItem(position);
        switch (sub.getType()) {
            case News.TYPE_SOFTWARE:
                SoftwareDetailActivity.show(mContext, sub.getId());
                break;
            case News.TYPE_QUESTION:
                QuestionDetailActivity.show(mContext, sub.getId());
                break;
            case News.TYPE_BLOG:
                BlogDetailActivity.show(mContext, sub.getId());
                break;
            case News.TYPE_TRANSLATE:
                TranslateDetailActivity.show(mContext, sub.getId());
                break;
            case News.TYPE_EVENT:
                EventDetailActivity.show(mContext, sub.getId());
                break;
            case News.TYPE_NEWS:
                NewsDetailActivity.show(mContext, sub.getId());
                break;
            default:
                UIHelper.showUrlRedirect(mContext, sub.getHref());
                break;
        }
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (mHeaderView != null)
            mHeaderView.requestBanner();
    }

    @Override
    protected void requestData() {
        OSChinaApi.getSubscription(mTab.getHref(), mIsRefresh ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected void setListData(ResultBean<PageBean<SubBean>> resultBean) {
        super.setListData(resultBean);
        if (mAdapter.getItems().size() > 0) {
            mCoordinatorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected BaseRecyclerAdapter<SubBean> getRecyclerAdapter() {
        if (mTab.getType() == News.TYPE_BLOG)
            return new BlogSubAdapter(getActivity());
        else if (mTab.getType() == News.TYPE_EVENT)
            return new EventSubAdapter(this);
        else if (mTab.getType() == News.TYPE_QUESTION)
            return new QuestionSubAdapter(this);
        return new NewsSubBeanAdapter(getActivity());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
    }
}
