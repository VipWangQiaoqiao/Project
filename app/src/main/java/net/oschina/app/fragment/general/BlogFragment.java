package net.oschina.app.fragment.general;


import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.Blog;
import net.oschina.app.fragment.base.BaseListFragment;

import java.lang.reflect.Type;

/**
 * 博客界面
 */
public class BlogFragment extends BaseListFragment<Blog> {

    public static final String BUNDLE_BLOG_TYPE = "BUNDLE_BLOG_TYPE";


    @Override
    protected BaseListAdapter<Blog> getListAdapter() {
        return null;
    }

    @Override
    protected Type getType() {
        return null;
    }
}
