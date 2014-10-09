package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import android.view.View;
import android.widget.AdapterView;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.NewsList;
import net.oschina.app.util.XmlUtils;

/**
 * @author HuangWenwei
 * 
 * @date 2014年9月30日
 */
public class LatestTweetFragment extends BaseListFragment {

	protected static final String TAG = LatestTweetFragment.class
			.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "latesttweetlist_";

	@Override
	protected ListBaseAdapter getListAdapter() {
		return null;
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

	}

}
