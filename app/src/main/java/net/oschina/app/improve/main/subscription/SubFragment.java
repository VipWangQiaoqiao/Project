package net.oschina.app.improve.main.subscription;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.AppConfig;
import net.oschina.app.OSCApplication;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseGeneralRecyclerFragment;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.widget.banner.EventHeaderView;
import net.oschina.app.improve.widget.banner.HeaderView;
import net.oschina.app.improve.widget.banner.NewsHeaderView;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class SubFragment extends BaseGeneralRecyclerFragment<SubBean> {
    private SubTab mTab;
    private HeaderView mHeaderView;
    private OSCApplication.ReadState mReadState;

    public static SubFragment newInstance(Context context, SubTab subTab) {
        SubFragment fragment = new SubFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_tab", subTab);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mTab = (SubTab) bundle.getSerializable("sub_tab");
        CACHE_NAME = mTab.getToken();
    }

    @Override
    public void initData() {
        mReadState = OSCApplication.getReadState("sub_list");
        if (mTab.getBanner() != null) {
            mHeaderView = mTab.getBanner().getCatalog() == SubTab.BANNER_CATEGORY_NEWS ?
                    new NewsHeaderView(mContext, getImgLoader(), mTab.getBanner().getHref(), mTab.getToken() + "banner" + mTab.getType()) :
                    new EventHeaderView(mContext, getImgLoader(), mTab.getBanner().getHref(), mTab.getToken() + "banner" + mTab.getType());
        }
        super.initData();
        mAdapter.setHeaderView(mHeaderView);
        mAdapter.setSystemTime(AppConfig.getAppConfig(getActivity()).get("system_time"));
        if (mAdapter instanceof NewsSubAdapter) {
            ((NewsSubAdapter) mAdapter).setTab(mTab);
        }
    }

    @Override
    public void onItemClick(int position, long itemId) {
        SubBean sub = mAdapter.getItem(position);
        if (sub == null)
            return;
        switch (sub.getType()) {
            case News.TYPE_SOFTWARE:
                //SoftwareDetailActivity.show(mContext, sub.getId());
                net.oschina.app.improve.detail.general.SoftwareDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_QUESTION:
                //QuestionDetailActivity.show(mContext, sub.getId());
                net.oschina.app.improve.detail.general.QuestionDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_BLOG:
                //BlogDetailActivity.show(mContext, sub.getId());
                net.oschina.app.improve.detail.general.BlogDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_TRANSLATE:
                //TranslateDetailActivity.show(mContext, sub.getId());
                net.oschina.app.improve.detail.general.NewsDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_EVENT:
                //EventDetailActivity.show(mContext, sub.getId());
                net.oschina.app.improve.detail.general.EventDetailActivity.show(mContext, sub);
                break;
            case News.TYPE_NEWS:
                //NewsDetailActivity.show(mContext, sub.getId());
                net.oschina.app.improve.detail.general.NewsDetailActivity.show(mContext, sub);
                break;
            default:
                UIHelper.showUrlRedirect(mContext, sub.getHref());
                break;
        }

        mReadState.put(sub.getKey());
        mAdapter.updateItem(position);
    }

    @Override
    public void onRefreshing() {
        super.onRefreshing();
        if (mHeaderView != null)
            mHeaderView.requestBanner();
    }

    @Override
    protected void requestData() {
        OSChinaApi.getSubscription(mTab.getHref(), isRefreshing ? null : mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected void setListData(ResultBean<PageBean<SubBean>> resultBean) {
        super.setListData(resultBean);
        mAdapter.setSystemTime(resultBean.getTime());
    }

    @Override
    protected BaseRecyclerAdapter<SubBean> getRecyclerAdapter() {
        int mode = mHeaderView != null ? BaseRecyclerAdapter.BOTH_HEADER_FOOTER : BaseRecyclerAdapter.ONLY_FOOTER;
        if (mTab.getType() == News.TYPE_BLOG)
            return new BlogSubAdapter(getActivity(), mode);
        else if (mTab.getType() == News.TYPE_EVENT)
            return new EventSubAdapter(this, mode);
        else if (mTab.getType() == News.TYPE_QUESTION)
            return new QuestionSubAdapter(this, mode);
        return new NewsSubAdapter(getActivity(), mode);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
    }

    @Override
    protected Class<SubBean> getCacheClass() {
        return SubBean.class;
    }
}
