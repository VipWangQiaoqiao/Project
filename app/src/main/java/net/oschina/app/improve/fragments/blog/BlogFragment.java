package net.oschina.app.improve.fragments.blog;


import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.improve.adapter.base.BaseListAdapter;
import net.oschina.app.improve.adapter.general.BlogAdapter;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.Blog;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.activities.BlogDetailActivity;
import net.oschina.app.improve.fragments.base.BaseGeneralListFragment;
import net.oschina.app.ui.empty.EmptyLayout;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 博客界面
 */
public class BlogFragment extends BaseGeneralListFragment<Blog> {

    public static final String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";

    public static final String HISTORY_BLOG = "history_blog";
    private boolean isFirst = true;

    @Override
    public void onRefreshing() {
        isFirst = true;
        super.onRefreshing();
    }

    @Override
    protected void requestData() {
        super.requestData();

        if (mIsRefresh) {
            OSChinaApi.getBlogList(OSChinaApi.CATALOG_BLOG_HEAT, null, mHandler);
        } else {
            OSChinaApi.getBlogList(OSChinaApi.CATALOG_BLOG_NORMAL, mBean == null ? null : mBean.getNextPageToken(), mHandler);
        }
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
            TextView title = (TextView) view.findViewById(R.id.tv_item_blog_title);
            TextView content = (TextView) view.findViewById(R.id.tv_item_blog_body);

            updateTextColor(title, content);
            saveToReadedList(BlogFragment.HISTORY_BLOG, blog.getId() + "");

        }
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
                AppOperator.runOnThread(new Runnable() {
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
            mRefreshLayout.setNoMoreData();
        }
        if (mAdapter.getDatas().size() > 0) {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRefreshLayout.setVisibility(View.VISIBLE);
        } else {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

    }

}
