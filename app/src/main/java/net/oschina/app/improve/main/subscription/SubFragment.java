package net.oschina.app.improve.main.subscription;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.SubTab;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.widget.banner.EventHeaderView;
import net.oschina.app.improve.widget.banner.HeaderView;
import net.oschina.app.improve.widget.banner.NewsHeaderView;

import java.lang.reflect.Type;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class SubFragment extends BaseRecyclerViewFragment<SubBean> {

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
        }
        mAdapter.setHeaderView(mHeaderView);
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
    protected BaseRecyclerAdapter<SubBean> getRecyclerAdapter() {
        return new SubBeanAdapter(getActivity(),
                mTab.getBanner() != null ? BaseRecyclerAdapter.BOTH_HEADER_FOOTER : BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<SubBean>>>() {
        }.getType();
    }
}
