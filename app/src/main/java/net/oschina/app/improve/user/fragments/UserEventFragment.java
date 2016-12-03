package net.oschina.app.improve.user.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

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
import net.oschina.app.improve.detail.activities.EventDetailActivity;
import net.oschina.app.improve.user.adapter.UserEventAdapter;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

/**
 * Created by fei
 * on 2016/12/2.
 * desc:
 */

public class UserEventFragment extends BaseGeneralRecyclerFragment<SubBean> {

    private static final String TAG = "UserEventFragment";
    private SubTab mTab;
    private OSCApplication.ReadState mReadState;

    public static UserEventFragment newInstance(Context context, SubTab subTab) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_tab", subTab);
        UserEventFragment userEventFragment = new UserEventFragment();
        userEventFragment.setArguments(bundle);
        return userEventFragment;//(UserEventFragment) Fragment.instantiate(context, UserEventFragment.class.getClass().getName(), bundle);
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mTab = (SubTab) bundle.getSerializable("sub_tab");
        Log.e(TAG, "initBundle: ----->" + mTab.toString());
        CACHE_NAME = mTab.getToken();
    }

    @Override
    public void initData() {
        mReadState = OSCApplication.getReadState("sub_list");
        super.initData();
        mAdapter.setSystemTime(AppConfig.getAppConfig(getActivity()).get("system_time"));
    }

    @Override
    public void onItemClick(int position, long itemId) {
        SubBean sub = mAdapter.getItem(position);
        if (sub == null)
            return;
        switch (sub.getType()) {
            case News.TYPE_EVENT:
                EventDetailActivity.show(mContext, sub.getId());
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
        Log.e(TAG, "onRefreshing: ------>");
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
        return new UserEventAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER);
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

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach: ----->");
    }
}
