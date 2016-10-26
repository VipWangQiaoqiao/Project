package net.oschina.app.improve.search.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.search.activities.SearchActivity;
import net.oschina.app.improve.search.adapters.SearchArticleAdapter;

import java.lang.reflect.Type;

/**
 * 博客、软件、资讯、问答的搜索界面
 * Created by thanatos on 16/10/18.
 */

public class SearchArticleFragment extends BaseRecyclerViewFragment<News>
        implements SearchActivity.SearchAction {

    public static final String BUNDLE_KEY_CATALOG = "BUNDLE_KEY_CATALOG";

    private int catalog = News.TYPE_NEWS;
    private String content;

    public static Fragment instantiate(Context context, int catalog) {
        Fragment fragment = new SearchArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_CATALOG, catalog);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        catalog = getArguments().getInt(BUNDLE_KEY_CATALOG, News.TYPE_NEWS);
    }

    @Override
    protected BaseRecyclerAdapter<News> getRecyclerAdapter() {
        // TODO add adapter
        return new SearchArticleAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<News>>>() {}.getType();
    }

    @Override
    protected void requestData() {
        if (TextUtils.isEmpty(content)) return;
        super.requestData();
        String token = mIsRefresh ? null : mBean.getNextPageToken();
        OSChinaApi.search(catalog, content, token, mHandler);
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
