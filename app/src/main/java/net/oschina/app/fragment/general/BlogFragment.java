package net.oschina.app.fragment.general;


import android.view.View;

import com.google.gson.reflect.TypeToken;

import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.adapter.general.BlogAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.bean.base.PageBean;
import net.oschina.app.bean.base.ResultBean;
import net.oschina.app.bean.blog.Blog;
import net.oschina.app.fragment.base.BaseListFragment;
import net.oschina.app.util.UIHelper;

import java.lang.reflect.Type;

/**
 * 博客界面
 */
public class BlogFragment extends BaseListFragment<Blog> {

    public static final String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";
    private static final String TAG = "BlogFragment";

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void requestData() {
        super.requestData();
        OSChinaApi.getBlogList(OSChinaApi.CATALOG_BLOG_NORMAL, mIsRefresh ? mBeam.getPrevPageToken() : mBeam.getNextPageToken(), mHandler);
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
    protected void onItemClick(Blog item, int position) {
        super.onItemClick(item, position);

        UIHelper.showBlogDetail(getActivity(), (int) item.getId(),
                item.getCommentCount());

    }
}
