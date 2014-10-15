package net.oschina.app.viewpagefragment;

import android.os.Bundle;
import android.view.View;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Post;
import net.oschina.app.fragment.FragmentTest;
import net.oschina.app.fragment.PostsFragment;

/**
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午2:21:52
 *
 */
public class QuestViewPagerFragment extends BaseViewPagerFragment {
	
    public static QuestViewPagerFragment newInstance() {
        return new QuestViewPagerFragment();
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.quests_viewpage_arrays);
		adapter.addTab(title[0], "quest_ask", PostsFragment.class, getBundle(Post.CATALOG_ASK));
		adapter.addTab(title[1], "quest_share", PostsFragment.class, getBundle(Post.CATALOG_SHARE));
		adapter.addTab(title[2], "quest_multiple", PostsFragment.class, getBundle(Post.CATALOG_OTHER));
		adapter.addTab(title[3], "quest_occupation", PostsFragment.class, getBundle(Post.CATALOG_JOB));
		adapter.addTab(title[4], "quest_station", PostsFragment.class, getBundle(Post.CATALOG_SITE));
	}
	
	private Bundle getBundle(int catalog) {
		Bundle bundle = new Bundle();
		bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, catalog);
		return bundle;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initView(View view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
}
