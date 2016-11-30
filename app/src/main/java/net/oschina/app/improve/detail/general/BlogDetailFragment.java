package net.oschina.app.improve.detail.general;

import net.oschina.app.R;
import net.oschina.app.improve.detail.v2.DetailFragment;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class BlogDetailFragment extends DetailFragment {
    public static BlogDetailFragment newInstance() {
        BlogDetailFragment fragment = new BlogDetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blog_detail_v2;
    }
}
