package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import android.view.View;
import android.widget.AdapterView;
import net.oschina.app.adapter.BlogAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.BlogList;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.util.XmlUtils;

/**
 * @author HuangWenwei
 *
 * @date 2014年9月29日
 */
public class recommendBlogFragment extends BaseListFragment {
	
	protected static final String TAG = recommendBlogFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "recommendblogslist_";
	private static final String BLOG_TYPE = "recommend";

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new BlogAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		BlogList list = XmlUtils.toBean(BlogList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return (ListEntity) seri;
	}


	@Override
	protected void sendRequestData() {
		OSChinaApi.getBlogList(BLOG_TYPE, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

}
