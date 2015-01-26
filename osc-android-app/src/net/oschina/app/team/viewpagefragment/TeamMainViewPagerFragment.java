package net.oschina.app.team.viewpagefragment;

import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.team.fragment.DynamicFragment;
import net.oschina.app.team.fragment.MyIssueFragment;
import net.oschina.app.team.fragment.TeamMemberFragment;
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
        adapter.addTab("团队动态", "", DynamicFragment.class, getActivity()
                .getIntent().getExtras());
        adapter.addTab("我的任务", "", MyIssueFragment.class, getActivity()
                .getIntent().getExtras());
        adapter.addTab("团队成员", "", TeamMemberFragment.class, getActivity()
                .getIntent().getExtras());
    }
}
