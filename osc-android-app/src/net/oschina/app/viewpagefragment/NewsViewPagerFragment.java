package net.oschina.app.viewpagefragment;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.NewsList;
import net.oschina.app.fragment.NewsFragment;
import net.oschina.app.interf.OnTabReselectListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * 资讯viewpager页面
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午2:21:52
 *
 */
public class NewsViewPagerFragment extends BaseViewPagerFragment implements OnTabReselectListener {
	
	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.news_viewpage_arrays);
		adapter.addTab(title[0], "news", NewsFragment.class, getBundle(NewsList.CATALOG_ALL));
		adapter.addTab(title[1], "news_week", NewsFragment.class, getBundle(NewsList.CATALOG_WEEK));
		adapter.addTab(title[2], "news_month", NewsFragment.class, getBundle(NewsList.CATALOG_MONTH));
	}
	
	private Bundle getBundle(int newType) {
		Bundle bundle = new Bundle();
		bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, newType);
		return bundle;
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void initView(View view) {
		
	}

	@Override
	public void initData() {
		
	}

	@Override
	public void onTabReselect() {
		int currentIndex = mViewPager.getCurrentItem();
		Fragment currentFragment = getChildFragmentManager().getFragments().get(currentIndex);
		if (currentFragment != null && currentFragment instanceof OnTabReselectListener) {
            OnTabReselectListener listener = (OnTabReselectListener) currentFragment;
            listener.onTabReselect();
        }
	}
}
