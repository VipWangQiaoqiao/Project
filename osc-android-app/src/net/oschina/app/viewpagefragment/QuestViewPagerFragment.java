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
public class QuestViewPagerFragment extends BaseViewPagerFragment {
	
    public static QuestViewPagerFragment newInstance() {
        return new QuestViewPagerFragment();
    }

	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.quests_viewpage_arrays);
		adapter.addTab(title[0], "quest_ask", FragmentTest.class, null);
		adapter.addTab(title[1], "quest_share", FragmentTest.class, null);
		adapter.addTab(title[2], "quest_multiple", FragmentTest.class, null);
		adapter.addTab(title[3], "quest_occupation", FragmentTest.class, null);
		adapter.addTab(title[4], "quest_station", FragmentTest.class, null);
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
