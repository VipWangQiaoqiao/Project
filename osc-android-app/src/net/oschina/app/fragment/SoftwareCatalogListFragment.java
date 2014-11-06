package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import net.oschina.app.adapter.SoftwareCatalogListAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.SoftwareCatalogList;
import net.oschina.app.bean.SoftwareType;
import net.oschina.app.util.XmlUtils;

public class SoftwareCatalogListFragment extends BaseListFragment {
	
	protected static final String TAG = SoftwareCatalogListFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "softwarecataloglist_";
	
	protected int softwarecatalogtag = 0;

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new SoftwareCatalogListAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		SoftwareCatalogList list = XmlUtils.toBean(SoftwareCatalogList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return (SoftwareCatalogList) seri;
	}

	@Override
	protected void sendRequestData() {
		OSChinaApi.getSoftwareCatalogList(softwarecatalogtag, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		SoftwareType tag = (SoftwareType) mAdapter.getItem(position);
//		softwarecatalogtag = tag.getTag();
//		Log.e("软件分类:", String.valueOf(softwarecatalogtag));
//		sendRequestData();
	}

	
}
