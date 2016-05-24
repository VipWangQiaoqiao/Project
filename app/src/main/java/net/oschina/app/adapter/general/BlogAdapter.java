package net.oschina.app.adapter.general;

import net.oschina.app.adapter.ViewHolder;
import net.oschina.app.adapter.base.BaseListAdapter;
import net.oschina.app.bean.blog.Blog;

/**
 * Created by fei on 2016/5/24.
 * desc:
 */
public class BlogAdapter extends BaseListAdapter<Blog> {

    public BlogAdapter(Callback callback) {
        super(callback);
    }

    @Override
    protected void convert(ViewHolder vh, Blog item, int position) {

    }

    @Override
    protected int getLayoutId(int position, Blog item) {
        return 0;
    }
}
