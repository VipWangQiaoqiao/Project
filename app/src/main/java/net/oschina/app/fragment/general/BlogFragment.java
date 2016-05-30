package net.oschina.app.fragment.general;


import android.view.View;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppContext;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.adapter.general.BlogAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.blog.Blog;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.fragment.base.BaseListFragment;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 博客界面
 */
public class BlogFragment extends BaseListFragment<Blog> {

    public static final String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";
    private boolean isFirst = true;

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

        OSChinaApi.getBlogList(mIsRefresh ? OSChinaApi.CATALOG_BLOG_HEAT : OSChinaApi.CATALOG_BLOG_NORMAL, mIsRefresh ? mBeam.getPrevPageToken() : mBeam.getNextPageToken(), mHandler);

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
        //super.onItemClick(parent, view, position, id);
        Blog blog = mAdapter.getItem(position);
        if (blog != null) {
            UIHelper.showBlogDetail(getActivity(), (int) blog.getId(),
                    blog.getCommentCount());
        }
    }

    @Override
    protected void setListData(ResultBean<PageBean<Blog>> resultBean) {
        //super.setListData(resultBean);
        //is refresh
        if (mIsRefresh) {
            List<Blog> blogs = resultBean.getResult().getItems();
            Blog blog = new Blog();
            blog.setViewType(Blog.VIEW_TYPE_TITLE_HEAT);
            blogs.add(0, blog);
            mBeam.setItems(blogs);
            mAdapter.clear();
            mAdapter.addItem(mBeam.getItems());
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
                mExeService.submit(new Runnable() {
                    @Override
                    public void run() {
                        CacheManager.saveObject(getActivity(), mBeam, CACHE_NAME);
                    }
                });
            }
            mBeam.setNextPageToken(resultBean.getResult().getNextPageToken());
            mBeam.setPrevPageToken(resultBean.getResult().getPrevPageToken());
            mAdapter.addItem(blogs);
        }

        if (resultBean.getResult().getItems().size() < 10) {
            setFooterType(TYPE_NO_MORE);
            mRefreshLayout.setNoMoreData();
        }
        if (mAdapter.getDatas().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }


    }


    private List<Blog> blogs = new ArrayList<>();
    private TextHttpResponseHandler handler = new TextHttpResponseHandler() {

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            onRequestError(statusCode);
            onRequestFinish();
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, String responseString) {
            try {
                ResultBean<PageBean<Blog>> resultBean = AppContext.createGson().fromJson(responseString, getType());
                if (resultBean != null && resultBean.isSuccess()) {
                    blogs.clear();
                    Blog blog = new Blog();
                    blog.setViewType(Blog.VIEW_TYPE_TITLE_HEAT);
                    blogs.add(blog);
                    blogs.addAll(resultBean.getResult().getItems());
                    blog = new Blog();
                    blog.setViewType(Blog.VIEW_TYPE_TITLE_NORMAL);
                    blogs.add(blog);
                    OSChinaApi.getBlogList(OSChinaApi.CATALOG_BLOG_NORMAL, mIsRefresh ? mBeam.getPrevPageToken() : mBeam.getNextPageToken(), mHandler);
                }
                onRequestFinish();
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(statusCode, headers, responseString, e);
            }
        }
    };


}
