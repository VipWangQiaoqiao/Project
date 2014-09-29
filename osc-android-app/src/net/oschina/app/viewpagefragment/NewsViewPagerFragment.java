package net.oschina.app.viewpagefragment;

import android.support.v4.app.Fragment;
import android.view.View;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.fragment.FragmentTest;
import net.oschina.app.fragment.NewsFragment;

/**
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午2:21:52
 *
 */
public class NewsViewPagerFragment extends BaseViewPagerFragment {
	
    public static NewsViewPagerFragment newInstance() {
        return new NewsViewPagerFragment();
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.news_viewpage_arrays);
		adapter.addTab(title[0], "news", NewsFragment.class, null);
		adapter.addTab(title[1], "new_blogs", NewsFragment.class, null);
		adapter.addTab(title[2], "featured_blogs", NewsFragment.class, null);
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
