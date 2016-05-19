package net.oschina.app.fragment.general;

import net.oschina.app.adapter.NewsAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.News;

/**
 * 资讯界面
 */
public class NewsFragment extends BaseListFragment<News> {
    @Override
    protected ListBaseAdapter<News> getListAdapter() {
        return new NewsAdapter();
    }
}
