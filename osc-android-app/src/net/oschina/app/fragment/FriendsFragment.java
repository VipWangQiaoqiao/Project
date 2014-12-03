package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.adapter.FriendAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.FriendsList;
import net.oschina.app.bean.FriendsList.Friend;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.Notice;
import net.oschina.app.service.NoticeUtils;
import net.oschina.app.ui.MainActivity;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * 关注、粉丝
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年11月6日 上午11:15:37
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FriendsFragment extends BaseListFragment {

	public final static String BUNDLE_KEY_UID = "UID";

	protected static final String TAG = FriendsFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "friend_list";

	private int mUid;

	@Override
	public void initView(View view) {
		super.initView(view);
	}

	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mUid = args.getInt(BUNDLE_KEY_UID, 0);
		}
	}
	
	@Override
	public void onResume() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				refreshNotice();
			}
		}, 800);
		super.onResume();
	}

	private void refreshNotice() {
		Notice notice = MainActivity.mNotice;
		if (notice != null && notice.getNewFansCount() > 0) {
			onRefresh();
		}
	}

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new FriendAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX + "_" + mCatalog + "_" + mUid;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		FriendsList list = XmlUtils.toBean(FriendsList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((FriendsList) seri);
	}

	@Override
	protected void sendRequestData() {
		OSChinaApi.getFriendList(mUid, mCatalog, mCurrentPage, mHandler);
	}

	@Override
	protected void onRefreshNetworkSuccess() {
		if (mCatalog == FriendsList.TYPE_FANS && mUid == AppContext.getInstance().getLoginUid()) {
			NoticeUtils.clearNotice(Notice.TYPE_NEWFAN);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Friend item = (Friend) mAdapter.getItem(position);
		if (item != null) {
			if (mUid == AppContext.getInstance().getLoginUid()) {
				UIHelper.showMessageDetail(getActivity(), item.getUserid(), item.getName());
				return;
			}
			UIHelper.showUserCenter(getActivity(), item.getUserid(),item.getName());
		}
			
	}
}
