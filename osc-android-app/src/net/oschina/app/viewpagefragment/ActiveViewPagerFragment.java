package net.oschina.app.viewpagefragment;

import android.os.Bundle;
import android.view.View;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.Active;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.Post;
import net.oschina.app.fragment.ActiveFragment;
import net.oschina.app.fragment.FragmentTest;
import net.oschina.app.fragment.MessageFragment;
import net.oschina.app.fragment.PostsFragment;

/**
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午2:21:52
 *
 */
public class ActiveViewPagerFragment extends BaseViewPagerFragment {
	
    public static ActiveViewPagerFragment newInstance() {
        return new ActiveViewPagerFragment();
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.my_viewpage_arrays);
		adapter.addTab(title[0], "active_all", ActiveFragment.class, getBundle(ActiveList.CATALOG_LASTEST));
		adapter.addTab(title[1], "active_me", ActiveFragment.class, getBundle(ActiveList.CATALOG_ATME));
		adapter.addTab(title[2], "active_comment", ActiveFragment.class, getBundle(ActiveList.CATALOG_COMMENT));
		adapter.addTab(title[3], "active_myself", ActiveFragment.class, getBundle(ActiveList.CATALOG_MYSELF));
		adapter.addTab(title[4], "active_mes", MessageFragment.class, null);
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
