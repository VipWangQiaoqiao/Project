package net.oschina.app.improve.general.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.base.adapter.BaseListAdapter;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.bean.Blog;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.detail.fragments.BlogDetailFragment;
import net.oschina.app.improve.general.adapter.BlogActionAdapter;
import net.oschina.app.improve.general.adapter.BlogAdapter;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 博客界面
 */
public class BlogFragment extends BaseGeneralListFragment<Blog> {

    public static final String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";

    public static final String BLOG_NORMAL = "blog_normal";        //最新博客
    public static final String BLOG_HEAT = "blog_heat";            //本周热门
    public static final String BLOG_RECOMMEND = "blog_recommend";  //最新推荐

    public static final String BLOG_TYPE_KEY = "blog_type_key";
    public static final int BLOG_HEAT_TYPE = 2;//2:热门博客
    public static final int BLOG_NEW_TYPE = 3;//3:最新博客
    public static final int BLOG_RECOMMEND_TYPE = 4;//4:推荐博客

    private int[] positions = {1, 0, 0};
    private BlogActionAdapter actionAdapter;
    private int catalog = 3;

    public static Fragment instantiate(Context context, int subtype){
        Fragment fragment = new BlogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BLOG_TYPE_KEY, subtype);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);

        if (bundle == null) return;

        int blogType = bundle.getInt(BLOG_TYPE_KEY, BLOG_NEW_TYPE);
        switch (blogType) {
            case BLOG_HEAT_TYPE:
                catalog = OSChinaApi.CATALOG_BLOG_HEAT;
                break;
            case BLOG_NEW_TYPE:
                catalog = OSChinaApi.CATALOG_BLOG_NORMAL;
                break;
            case BLOG_RECOMMEND_TYPE:
                catalog = OSChinaApi.CATALOG_BLOG_RECOMMEND;
                break;
            default:
                catalog = OSChinaApi.CATALOG_BLOG_NORMAL;
                break;
        }

    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        @SuppressLint("InflateParams")
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_blog_header, null, false);
        headView.setVisibility(View.GONE);
        GridView quesGridView = (GridView) headView.findViewById(R.id.gv_ques);
        quesGridView.setVisibility(View.GONE);
        actionAdapter = new BlogActionAdapter(getActivity(), positions);
        quesGridView.setAdapter(actionAdapter);
        quesGridView.setItemChecked(0, true);
        quesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                displacementCatalog(position);
                ((BlogAdapter) mAdapter).setActionPosition(position);
                if (!mIsRefresh) {
                    mIsRefresh = true;
                    mBean.setPrevPageToken(null);
                    mBean.setNextPageToken(null);
                }
                updateAction(position);
                if (positions[position] == 1) {
                    requestEventDispatcher();
                }
            }
        });
        mListView.addHeaderView(headView);

    }

    /**
     * According to the distribution network is events
     */
    private void requestEventDispatcher() {
        if (TDevice.hasInternet()) {
            mRefreshLayout.setRefreshing(true);
            onRefreshing();
            // requestData();
        } else {
            requestLocalCache();
        }
    }

    /**
     * notify action data
     *
     * @param position position
     */
    private void updateAction(int position) {
        int len = positions.length;
        for (int i = 0; i < len; i++) {
            if (i != position) {
                positions[i] = 0;
            } else {
                positions[i] = 1;
            }
        }
        actionAdapter.notifyDataSetChanged();
    }

    /**
     * displacement Catalog  type
     *
     * @param position position
     */
    private void displacementCatalog(int position) {
        switch (position) {
            case 0:
                catalog = OSChinaApi.CATALOG_BLOG_RECOMMEND;
                break;
            case 1:
                catalog = OSChinaApi.CATALOG_BLOG_HEAT;
                break;
            case 2:
                catalog = OSChinaApi.CATALOG_BLOG_NORMAL;
                break;
        }
    }

    /**
     * request local cache
     */
    @SuppressWarnings("unchecked")
    private void requestLocalCache() {
        verifyCacheType();
        mBean = (PageBean<Blog>) CacheManager.readObject(getActivity(), CACHE_NAME);
        if (mBean != null) {
            mAdapter.clear();
            mAdapter.addItem(mBean.getItems());
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRefreshLayout.setCanLoadMore();
        } else {
            mBean = new PageBean<>();
            mBean.setItems(new ArrayList<Blog>());
            onRefreshing();
        }
    }

    @Override
    protected void initData() {
        CACHE_NAME = BLOG_NORMAL;
        super.initData();
        if (!mIsRefresh) {
            mIsRefresh = true;
//            mBean.setPrevPageToken(null);
//            mBean.setNextPageToken(null);
        }
        requestEventDispatcher();
    }

    @Override
    protected void onRequestError(int code) {
        super.onRequestError(code);
        requestLocalCache();
    }

    /**
     * verify cache type
     */
    private void verifyCacheType() {

        switch (catalog) {
            case 1:
                CACHE_NAME = BLOG_NORMAL;
                break;
            case 2:
                CACHE_NAME = BLOG_HEAT;
                break;
            case 3:
                CACHE_NAME = BLOG_RECOMMEND;
                break;
        }

    }

    @Override
    protected BaseListAdapter<Blog> getListAdapter() {
        return new BlogAdapter(this);
    }

    @Override
    protected void requestData() {
        super.requestData();
        verifyCacheType();
        OSChinaApi.getBlogList(catalog, mIsRefresh ? /*(mBean != null ? mBean.getPrevPageToken() : null)*/null
                : (mBean != null ? mBean.getNextPageToken() : null), mHandler);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Blog>>>() {
        }.getType();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        Blog blog = mAdapter.getItem(position - 1);
        if (blog != null) {
            BlogDetailActivity.show(getActivity(), blog.getId());
            TextView title = (TextView) view.findViewById(R.id.tv_item_blog_title);
            TextView content = (TextView) view.findViewById(R.id.tv_item_blog_body);
            updateTextColor(title, content);
            verifyCacheType();
            saveToReadedList(CACHE_NAME, String.valueOf(blog.getId()));
        }

    }

}
