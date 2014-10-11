package net.oschina.app.viewpagefragment;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.Tweet;
import net.oschina.app.fragment.TweetsFragment;
import android.os.Bundle;
import android.view.View;

/**
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年9月25日 下午2:21:52
 *
 */
public class TweetsViewPagerFragment extends BaseViewPagerFragment {
	
    public static TweetsViewPagerFragment newInstance() {
        return new TweetsViewPagerFragment();
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.tweets_viewpage_arrays);
		adapter.addTab(title[0], "new_tweets", TweetsFragment.class, getBundle(Tweet.CATALOG_LATEST));
		adapter.addTab(title[1], "hot_tweets", TweetsFragment.class, getBundle(Tweet.CATALOG_HOT));
		adapter.addTab(title[2], "my_tweets", TweetsFragment.class, getBundle(Tweet.CATALOG_MINE));
	}
	
	private Bundle getBundle(int catalog) {
		Bundle bundle = new Bundle();
		bundle.putInt(BaseListFragment.BUNDLE_TWEET_TYPE, catalog);
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
