package net.oschina.app.fragment.general;

import net.oschina.app.adapter.BlogAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Blog;

/**
 * 博客界面
 */
public class BlogFragment extends BaseListFragment<Blog>{
    public static final String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";

    @Override
    protected ListBaseAdapter<Blog> getListAdapter() {
        return new BlogAdapter();
    }
}
