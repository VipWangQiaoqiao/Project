package net.oschina.app.improve.search.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.base.fragments.BaseRecyclerViewFragment;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.general.BlogDetailActivity;
import net.oschina.app.improve.detail.general.NewsDetailActivity;
import net.oschina.app.improve.detail.general.QuestionDetailActivity;
import net.oschina.app.improve.detail.general.SoftwareDetailActivity;
import net.oschina.app.improve.search.activities.SearchActivity;
import net.oschina.app.improve.search.adapters.SearchArticleAdapter;
import net.oschina.app.util.UIHelper;

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
    private boolean isRequesting;

    public static Fragment instantiate(Context context, int catalog) {
        Fragment fragment = new SearchArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_CATALOG, catalog);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        catalog = bundle.getInt(BUNDLE_KEY_CATALOG, News.TYPE_NEWS);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    protected BaseRecyclerAdapter<News> getRecyclerAdapter() {
        return new SearchArticleAdapter(getContext());
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<News>>>() {
        }.getType();
    }

    @Override
    protected void requestData() {
        super.requestData();
        if (TextUtils.isEmpty(content)) {
            mRefreshLayout.setRefreshing(false);
            return;
        }
        if (isRequesting) return;
        isRequesting = true;
        String token = isRefreshing ? null : mBean.getNextPageToken();
        OSChinaApi.search(catalog, content, token, mHandler);
    }

    @Override
    protected void onRequestFinish() {
        super.onRequestFinish();
        isRequesting = false;
    }

    @Override
    public void onItemClick(int position, long itemId) {
        super.onItemClick(position, itemId);
        News item = mAdapter.getItem(position);
        if (item == null) return;
        switch (item.getType()) {
            case News.TYPE_BLOG:
                BlogDetailActivity.show(getContext(), item.getId());
                break;
            case News.TYPE_QUESTION:
                QuestionDetailActivity.show(getContext(), item.getId());
                break;
            case News.TYPE_SOFTWARE:
                SoftwareDetailActivity.show(getContext(), item.getId());
                break;
            case News.TYPE_NEWS:
                NewsDetailActivity.show(getContext(), item.getId());
                break;
            default:
                UIHelper.openInternalBrowser(getContext(), item.getHref());
        }
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
