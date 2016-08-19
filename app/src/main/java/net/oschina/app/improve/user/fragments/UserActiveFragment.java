package net.oschina.app.improve.user.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.Active;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.user.adapter.UserActiveAdapter;

import java.lang.reflect.Type;

/**
 * 某用户的动态(讨论)列表
 * Created by thanatos on 16/8/16.
 */
public class UserActiveFragment extends BaseRecyclerViewFragment<Active> {

    public static final String BUNDLE_KEY_USER_ID = "BUNDLE_KEY_USER_ID";

    private long uid;

    public static Fragment instantiate(Long uid){
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY_USER_ID, uid);
        Fragment fragment = new UserActiveFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        uid = bundle.getLong(BUNDLE_KEY_USER_ID);
    }

    @Override
    protected BaseRecyclerAdapter<Active> getRecyclerAdapter() {
        return new UserActiveAdapter(getContext(), BaseRecyclerAdapter.ONLY_FOOTER);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Active>>>(){}.getType();
    }

    @Override
    protected void requestData() {
        OSChinaApi.getUserActives(uid, null, mHandler);
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getUserActives(uid, mBean.getNextPageToken(), mHandler);
    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }
}
