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
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

/**
 * 新闻资讯
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年11月12日 下午4:17:45
 *
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
		return CACHE_KEY_PREFIX + "_" + mCatalog;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		NewsList list = XmlUtils.toBean(NewsList.class, is);
		if (mCatalog == NewsList.CATALOG_WEEK || mCatalog == NewsList.CATALOG_MONTH) {
			mState = ListBaseAdapter.STATE_NO_MORE;
		}
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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mCatalog == NewsList.CATALOG_WEEK || mCatalog == NewsList.CATALOG_MONTH) {
			mState = ListBaseAdapter.STATE_NO_MORE;
			try {
				if (view.getPositionForView(mAdapter.getFooterView()) == view
						.getLastVisiblePosition()) {
					mAdapter.setNoMore();
				}
			} catch (Exception e) {
			}
			return;
		}
		super.onScrollStateChanged(view, scrollState);
	}
}
