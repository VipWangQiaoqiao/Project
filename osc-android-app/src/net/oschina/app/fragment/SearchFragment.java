package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.R;
import net.oschina.app.adapter.SearchAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.SearchList;
import net.oschina.app.bean.SearchList.SearchResult;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

public class SearchFragment extends BaseListFragment {
	protected static final String TAG = SearchFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "search_list_";
	private String mCatalog;
	private String mSearch;
	private boolean mRquestDataIfCreated = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mCatalog = args.getString(BUNDLE_KEY_CATALOG);
		}
		int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
		getActivity().getWindow().setSoftInputMode(mode);
	}

	public void search(String search) {
		mSearch = search;
		if (mErrorLayout != null) {
			mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
			mState = STATE_REFRESH;
			requestData(true);
		} else {
			mRquestDataIfCreated = true;
		}
	}

	@Override
	protected boolean requestDataIfViewCreated() {
		return mRquestDataIfCreated;
	}

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new SearchAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX + mCatalog + mSearch;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		SearchList list = XmlUtils.toBean(SearchList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((SearchList) seri);
	}
	
	@Override
	protected void sendRequestData() {
		OSChinaApi.getSearchList(mCatalog, mSearch, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SearchList.SearchResult res = (SearchResult) mAdapter.getItem(position);
		if (res != null)
			UIHelper.showUrlRedirect(view.getContext(), res.getUrl());
	}
}
