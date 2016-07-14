package net.oschina.app.improve.general.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.adapter.base.BaseListAdapter;
import net.oschina.app.improve.bean.Blog;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.fragments.base.BaseGeneralListFragment;
import net.oschina.app.improve.general.adapter.BlogActionAdapter;
import net.oschina.app.improve.general.adapter.BlogAdapter;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 博客界面
 */
public class BlogFragment extends BaseGeneralListFragment<Blog> {

    public static final String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";

    public static final String BLOG_NORMAL = "blog_normal";
    public static final String BLOG_HEAT = "blog_heat";
    public static final String BLOG_RECOMMEND = "blog_recommend";

    private int[] positions = {1, 0, 0};
    private ConnectivityManager connectivityManager;
    private BlogActionAdapter actionAdapter;
    private int catalog = 3;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        @SuppressLint("InflateParams")
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_blog_header, null, false);

        GridView quesGridView = (GridView) headView.findViewById(R.id.gv_ques);
        actionAdapter = new BlogActionAdapter(getActivity(), positions);
        quesGridView.setAdapter(actionAdapter);
        quesGridView.setItemChecked(0, true);
        quesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                displacementCatalog(position);
                ((BlogAdapter) mAdapter).setActionPosition(position + 1);
                if (!mIsRefresh) {
                    mIsRefresh = true;
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
     * displacement Catalog  type
     *
     * @param position position
     */
    private void displacementCatalog(int position) {
        switch (position) {
            case 0:
                catalog = 3;
                break;
            case 1:
                catalog = 2;
                break;
            case 2:
                catalog = 1;
                break;
            default:
                catalog = 3;
                break;
        }
    }

    /**
     * According to the distribution network is events
     */
    private void requestEventDispatcher() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            boolean connectedOrConnecting = networkInfo.isConnectedOrConnecting();
            NetworkInfo.State state = networkInfo.getState();
            if (connectedOrConnecting && state == NetworkInfo.State.CONNECTED) {
                requestData();
            } else {
                requestLocalCache();
            }
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
            default:
                CACHE_NAME = BLOG_NORMAL;
                break;
        }

    }

    @Override
    protected void initData() {
        CACHE_NAME = BLOG_NORMAL;
        super.initData();
    }

    @Override
    protected void onRequestError(int code) {
        super.onRequestError(code);
        requestLocalCache();
    }


    @Override
    protected void requestData() {
        super.requestData();
        verifyCacheType();
        OSChinaApi.getBlogList(catalog, mIsRefresh ? (mBean != null ? mBean.getPrevPageToken() : null) : (mBean != null ? mBean.getNextPageToken() : null), mHandler);

    }


    @Override
    protected BaseListAdapter<Blog> getListAdapter() {
        return new BlogAdapter(this);
    }

    @Override
    protected Type getType() {
        return new TypeToken<ResultBean<PageBean<Blog>>>() {
        }.getType();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Blog blog = mAdapter.getItem(position - 1);
        if (blog != null) {
            BlogDetailActivity.show(getActivity(), blog.getId());
            TextView title = (TextView) view.findViewById(R.id.tv_item_blog_title);
            TextView content = (TextView) view.findViewById(R.id.tv_item_blog_body);
            updateTextColor(title, content);
            verifyCacheType();
            saveToReadedList(CACHE_NAME, blog.getId() + "");
        }

    }


    @Override
    protected void setListData(ResultBean<PageBean<Blog>> resultBean) {
        verifyCacheType();
        super.setListData(resultBean);
    }

}
