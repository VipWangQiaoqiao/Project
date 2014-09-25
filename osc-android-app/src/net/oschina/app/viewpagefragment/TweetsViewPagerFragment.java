package net.oschina.app.viewpagefragment;

import android.support.v4.app.Fragment;
import android.view.View;
import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.fragment.FragmentTest;

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
		adapter.addTab(title[0], "new_tweets", FragmentTest.class, null);
		adapter.addTab(title[1], "hot_tweets", FragmentTest.class, null);
		adapter.addTab(title[2], "my_tweets", FragmentTest.class, null);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
}
