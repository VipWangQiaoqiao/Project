package net.oschina.app.improve.search.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.UserV2;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.search.activities.SearchActivity;
import net.oschina.app.improve.search.adapters.SearchUserAdapter;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;

import java.lang.reflect.Type;

/**
 * Created by thanatos on 16/10/24.
 */

public class SearchUserFragment extends BaseRecyclerViewFragment<UserV2>
        implements SearchActivity.SearchAction{

    private String content;

    public static Fragment instantiate(Context context){
        return new SearchUserFragment();
    }

    @Override
    protected BaseRecyclerAdapter<UserV2> getRecyclerAdapter() {
        // TODO add adapter
        return new SearchUserAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<UserV2>>>(){}.getType();
    }

    @Override
    protected void requestData() {
        super.requestData();
        if (TextUtils.isEmpty(content)) return;
        String token = mIsRefresh ? null : mBean.getNextPageToken();
        OSChinaApi.search(News.TYPE_FIND_PERSON, content, token, mHandler);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        OtherUserHomeActivity.show(getContext(), mAdapter.getItem(position).getId());
    }

    @Override
    public void search(String content) {
        if (this.content != null && this.content.equals(content)) return;
        this.content = content;
        mAdapter.clear();
        mRefreshLayout.setRefreshing(true);
        onRefreshing();
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }
}
