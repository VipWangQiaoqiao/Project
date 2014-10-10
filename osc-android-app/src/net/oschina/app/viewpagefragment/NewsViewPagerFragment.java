package net.oschina.app.viewpagefragment;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.Blog;
import net.oschina.app.fragment.BlogFragment;
import net.oschina.app.fragment.NewsFragment;
import android.os.Bundle;
import android.view.View;

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
		adapter.addTab(title[1], "new_blogs", BlogFragment.class, getBundle(Blog.CATALOG_LATEST));
		adapter.addTab(title[2], "featured_blogs", BlogFragment.class, getBundle(Blog.CATALOG_RECOMMEND));
	}
	
	private Bundle getBundle(String blogType) {
		Bundle bundle = new Bundle();
		bundle.putString(BaseListFragment.BUNDLE_BLOG_TYPE, blogType);
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
