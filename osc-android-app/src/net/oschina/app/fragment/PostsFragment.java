package net.oschina.app.fragment;

import java.io.InputStream;
import java.io.Serializable;

import net.oschina.app.adapter.PostAdapter;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.ListEntity;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostList;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;
import android.view.View;
import android.widget.AdapterView;

/**
 * 新闻资讯
 * 
 * @author william_sim
 */
public class PostsFragment extends BaseListFragment {

	protected static final String TAG = PostsFragment.class.getSimpleName();
	private static final String CACHE_KEY_PREFIX = "postslist_";

	@Override
	protected ListBaseAdapter getListAdapter() {
		return new PostAdapter();
	}

	@Override
	protected String getCacheKeyPrefix() {
		return CACHE_KEY_PREFIX;
	}

	@Override
	protected ListEntity parseList(InputStream is) throws Exception {
		PostList list = XmlUtils.toBean(PostList.class, is);
		return list;
	}

	@Override
	protected ListEntity readList(Serializable seri) {
		return ((PostList) seri);
	}

	@Override
	protected void sendRequestData() {
		OSChinaApi.getPostList(mCatalog, mCurrentPage, mHandler);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Post post = (Post) mAdapter.getItem(position);
		if (post != null)
			UIHelper.showPostDetail(view.getContext(), post.getId());
	}
}
