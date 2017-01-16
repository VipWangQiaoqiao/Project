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
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.search.activities.SearchActivity;
import net.oschina.app.improve.search.adapters.SearchUserAdapter;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;

import java.lang.reflect.Type;

/**
 * 搜索人界面
 * Created by thanatos
 * on 16/10/24.
 */
public class SearchUserFragment extends BaseRecyclerViewFragment<User>
        implements SearchActivity.SearchAction {

    private String content;

    // fix: 直接点击找人Tab的时候，doSearch调用requestData方法, 异步网络过程中,
    // 生命周期流程调用requestData, 前一次调用使isRefreshing致为false，导致数据重复
    private boolean isRequesting = false;

    public static Fragment instantiate(Context context) {
        return new SearchUserFragment();
    }

    @Override
    protected BaseRecyclerAdapter<User> getRecyclerAdapter() {
        return new SearchUserAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<User>>>() {
        }.getType();
    }

    @Override
    protected void requestData() {
        super.requestData();
        Log.i("thanatosx", "Search User Fragment Request Data, Content: " + content);
        if (TextUtils.isEmpty(content)) {
            mRefreshLayout.setRefreshing(false);
            return;
        }
        if (isRequesting) return;
        isRequesting = true;
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.search(News.TYPE_FIND_PERSON, content, token, mHandler);
    }

    @Override
    protected void onRequestFinish() {
        super.onRequestFinish();
        isRequesting = false;
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        User user = mAdapter.getItem(position);
        if (user == null) return;
        OtherUserHomeActivity.show(getContext(), user.getId());
    }

    @Override
    public void search(String content) {
        Log.i("thanatosx", "Search User Fragment Do Search, Content: " + content);
        if (this.content != null && this.content.equals(content)) return;
        this.content = content;
        if(mRecyclerView == null)
            return;
        mAdapter.clear();
        mRefreshLayout.setRefreshing(true);
        onRefreshing();
    }

    @Override
    protected boolean isNeedEmptyView() {
        return false;
    }

    @Override
    protected boolean isNeedCache() {
        return false;
    }
}
