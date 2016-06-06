package net.oschina.app.fragment.general;


import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.adapter.general.BlogAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.blog.Blog;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.fragment.base.BaseListFragment;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.ui.blog.BlogDetailActivity;
import net.oschina.app.ui.empty.EmptyLayout;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 博客界面
 */
public class BlogFragment extends BaseListFragment<Blog> implements OnTabReselectListener {

    public static final String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";
    private static final String TAG = "BlogFragment";
    private static final String HISTORY_BEAN = "history_bean";
    private boolean isFirst = true;
    private List<Blog> isHistoryBlogs = new ArrayList<>(10);

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public void onRefreshing() {
        isFirst = true;
        super.onRefreshing();
    }

    @Override
    protected void requestData() {
        super.requestData();

        OSChinaApi.getBlogList(mIsRefresh ? OSChinaApi.CATALOG_BLOG_HEAT : OSChinaApi.CATALOG_BLOG_NORMAL,
                mIsRefresh ? mBean.getPrevPageToken() : mBean.getNextPageToken(), mHandler);

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
        Blog blog = mAdapter.getItem(position);
        if (blog != null) {

            BlogDetailActivity.show(getActivity(), blog.getId());
            blog.setIsHistory(1);
            mAdapter.updateItem(position, blog);
            isHistoryBlogs.add(blog);

            CACHE_NAME = HISTORY_BEAN;
            CacheManager.saveObject(getActivity(), (Serializable) isHistoryBlogs, CACHE_NAME);

        }
    }


    @SuppressWarnings("unchecked")
    private long HistoryPosition(int id) {
        long tempId = -1;

        List<Blog> blogs = (List<Blog>) CacheManager.readObject(getActivity(), HISTORY_BEAN);
        if (blogs != null && !blogs.isEmpty()) {
            for (Blog b : blogs) {
                if (b.getId() == id && b.getIsHistory() == 1) {
                    return b.getId();
                }
            }
        }

        return tempId;
    }

    @Override
    protected void setListData(ResultBean<PageBean<Blog>> resultBean) {
        //is refresh
        mBean.setNextPageToken(resultBean.getResult().getNextPageToken());
        if (mIsRefresh) {
            List<Blog> blogs = resultBean.getResult().getItems();
            Blog blog = new Blog();
            blog.setViewType(Blog.VIEW_TYPE_TITLE_HEAT);
            blogs.add(0, blog);
            mBean.setItems(blogs);
            mAdapter.clear();
            mAdapter.addItem(mBean.getItems());
            mRefreshLayout.setCanLoadMore();
            mIsRefresh = false;
            OSChinaApi.getBlogList(OSChinaApi.CATALOG_BLOG_NORMAL, null, mHandler);
        } else {
            List<Blog> blogs = resultBean.getResult().getItems();
            if (isFirst) {
                Blog blog = new Blog();
                blog.setViewType(Blog.VIEW_TYPE_TITLE_NORMAL);
                blogs.add(0, blog);
                isFirst = false;
                mExeService.execute(new Runnable() {
                    @Override
                    public void run() {
                        CacheManager.saveObject(getActivity(), mBean, CACHE_NAME);
                    }
                });
            }
            mRefreshLayout.setCanLoadMore();
            mBean.setPrevPageToken(resultBean.getResult().getPrevPageToken());
            mAdapter.addItem(blogs);
        }

        if (resultBean.getResult().getItems().size() < 20) {
            setFooterType(TYPE_NO_MORE);
            // mRefreshLayout.setNoMoreData();
        }
        if (mAdapter.getDatas().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

    }

    @Override
    public void onTabReselect() {

        if (!isFirst) {
            isFirst = true;
        }
        mIsRefresh = true;
        //  requestData();

        Log.d(TAG, "onTabReselect: ---->hello blog");
    }
}
