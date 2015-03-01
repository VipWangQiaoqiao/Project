package net.oschina.app.team.viewpagefragment;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.team.fragment.MyIssueFragment;
import net.oschina.app.team.fragment.TeamMemberFragment;
import net.oschina.app.team.fragment.TeamProjectFragment;
import android.os.Bundle;
import android.view.View;

/**
 * Team主界面
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class TeamMainViewPagerFragment extends BaseViewPagerFragment {

    //
    // @Override
    // public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // inflater.inflate(R.menu.team_issue_menu, menu);
    // super.onCreateOptionsMenu(menu, inflater);
    // }
    //
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	// setHasOptionsMenu(true);
	mViewPager.setOffscreenPageLimit(2);
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
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
	String[] arraStrings = getResources().getStringArray(
		R.array.team_main_viewpager);

	adapter.addTab(arraStrings[0], "", TeamProjectFragment.class,
		getActivity().getIntent().getExtras());
	adapter.addTab(arraStrings[1], "", MyIssueFragment.class, getActivity()
		.getIntent().getExtras());
	adapter.addTab(arraStrings[2], "", TeamMemberFragment.class,
		getActivity().getIntent().getExtras());
    }
}
