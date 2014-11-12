package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import android.view.View;
import android.widget.AdapterView;
import net.oschina.app.adapter.SoftwareAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.Software;
import net.oschina.app.bean.SoftwareDec;
import net.oschina.app.bean.SoftwareList;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

public class SoftwareListFragment extends BaseListFragment {
	
	protected static final String TAG = SoftwareListFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "softwarelist_";

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new SoftwareAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX + softwareType;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		SoftwareList list = XmlUtils.toBean(SoftwareList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return((SoftwareList)seri);
	}

	@Override
	protected void sendRequestData() {
		OSChinaApi.getSoftwareList(softwareType, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SoftwareDec softwaredec = (SoftwareDec) mAdapter.getItem(position-1);
		if(softwaredec!=null)
			UIHelper.showUrlRedirect(getActivity(), softwaredec.getUrl());
	}
	
	
}
