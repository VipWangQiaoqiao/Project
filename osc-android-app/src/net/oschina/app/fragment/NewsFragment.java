package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.adapter.NewsAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.view.View;
import android.widget.AdapterView;

/**
 * 新闻资讯
 * 
 * @author william_sim
 */
public class NewsFragment extends BaseListFragment {

	protected static final String TAG = NewsFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "newslist_";

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new NewsAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		NewsList list = XmlUtils.toBean(NewsList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((NewsList) seri);
	}

	@Override
	protected void sendRequestData() {
		OSChinaApi.getNewsList(mCatalog, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		News news = (News) mAdapter.getItem(position);
		if (news != null)
			UIHelper.showNewsRedirect(view.getContext(), news);
	}
}
