package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.R;
import net.oschina.app.adapter.TweetAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.TweetsList;
import net.oschina.app.util.XmlUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * @author HuangWenwei
 *
 * @date 2014年10月10日
 */
public class TweetsFragment extends BaseListFragment {
	
	protected static final String TAG = TweetsFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "tweetslist_";
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		LayoutInflater inflate  = getLayoutInflater(savedInstanceState);
		View view = inflate.inflate(net.oschina.app.R.layout.fragment_pull_refresh_listview, null);
		ListView lv = (ListView) view.findViewById(R.id.listview);
		lv.setDivider(null);
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	protected ListBaseAdapter getListAdapter() {
		return new TweetAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		TweetsList list = XmlUtils.toBean(TweetsList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((TweetsList) seri);
	}

	@Override
	protected void sendRequestData() {
		OSChinaApi.getTweetList(tweetType, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

}
