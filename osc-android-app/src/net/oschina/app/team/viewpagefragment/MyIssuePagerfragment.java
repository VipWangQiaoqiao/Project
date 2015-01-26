package net.oschina.app.team.viewpagefragment;

import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.team.fragment.MyIssueDetail;
import android.os.Bundle;
import android.view.View;

public class MyIssuePagerfragment extends BaseViewPagerFragment {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager.setOffscreenPageLimit(2);
    }

    @Override
    public void onClick(View v) {}

    @Override
    public void initView(View view) {}

    @Override
    public void initData() {}

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        adapter.addTab("进行中", "", MyIssueDetail.class, getActivity()
                .getIntent().getExtras());
        adapter.addTab("已完成", "", MyIssueDetail.class, getActivity()
                .getIntent().getExtras());
        adapter.addTab("已过期", "", MyIssueDetail.class, getActivity()
                .getIntent().getExtras());
    }
}
