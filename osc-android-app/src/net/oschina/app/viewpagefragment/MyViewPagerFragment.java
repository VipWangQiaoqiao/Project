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
public class MyViewPagerFragment extends BaseViewPagerFragment {
	
    public static MyViewPagerFragment newInstance() {
        return new MyViewPagerFragment();
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.my_viewpage_arrays);
		adapter.addTab(title[0], "quest_ask", FragmentTest.class, null);
		adapter.addTab(title[1], "quest_share", FragmentTest.class, null);
		adapter.addTab(title[2], "quest_multiple", FragmentTest.class, null);
		adapter.addTab(title[3], "quest_occupation", FragmentTest.class, null);
		adapter.addTab(title[4], "quest_station", FragmentTest.class, null);
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
