package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.adapter.UserFavoriteAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.FavoriteList;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.util.XmlUtils;
import android.view.View;
import android.widget.AdapterView;

public class UserFavoriteFragment extends BaseListFragment {
	
	protected static final String TAG = UserFavoriteFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "userfavorite_";
	
	@Override
	protected ListBaseAdapter getListAdapter() {
		return new UserFavoriteAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX + mCatalog;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		
		FavoriteList list = XmlUtils.toBean(FavoriteList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((ListEntity) seri);
	}

	@Override
	protected void sendRequestData() {
		OSChinaApi.getFavoriteList(AppContext.getInstance().getLoginUid(), userfavoriteType, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		super.onItemClick(parent, view, position, id);
	}
}
