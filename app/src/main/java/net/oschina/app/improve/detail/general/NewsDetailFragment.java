package net.oschina.app.improve.detail.general;

import net.oschina.app.R;
import net.oschina.app.improve.detail.v2.DetailFragment;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class NewsDetailFragment extends DetailFragment {
    public static NewsDetailFragment newInstance() {
        NewsDetailFragment fragment = new NewsDetailFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_detail_v2;
    }
}
