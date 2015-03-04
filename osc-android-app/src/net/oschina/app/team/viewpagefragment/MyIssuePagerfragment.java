package net.oschina.app.team.viewpagefragment;

import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.team.fragment.MyIssueDetail;
import net.oschina.app.team.fragment.TeamBoardFragment;
import net.oschina.app.ui.SimpleBackActivity;

import org.kymjs.kjframe.utils.KJLoger;

import android.os.Bundle;
import android.view.View;

public class MyIssuePagerfragment extends BaseViewPagerFragment {
    public static final String MY_ISSUEDETAIL_KEY = "MyIssuePagerfragment_key";

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
    public void onResume() {
        super.onResume();

        int currentPage = 0;
        try {
            currentPage = getActivity().getIntent()
                    .getBundleExtra(SimpleBackActivity.BUNDLE_KEY_ARGS)
                    .getInt(TeamBoardFragment.WHICH_PAGER_KEY, 0);
        } catch (NullPointerException e) {
        }
        mViewPager.setCurrentItem(currentPage);
        KJLoger.debug("" + currentPage);
    }

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
        Bundle bundle0 = getActivity().getIntent().getExtras();
        bundle0.remove(MyIssuePagerfragment.MY_ISSUEDETAIL_KEY);
        bundle0.putString(MyIssuePagerfragment.MY_ISSUEDETAIL_KEY, "opened");
        adapter.addTab("进行中", "", MyIssueDetail.class, bundle0);

        Bundle bundle1 = getActivity().getIntent().getExtras();
        bundle1.remove(MyIssuePagerfragment.MY_ISSUEDETAIL_KEY);
        bundle1.putString(MyIssuePagerfragment.MY_ISSUEDETAIL_KEY, "closed");
        adapter.addTab("已完成", "", MyIssueDetail.class, bundle1);

        Bundle bundle2 = getActivity().getIntent().getExtras();
        bundle2.remove(MyIssuePagerfragment.MY_ISSUEDETAIL_KEY);
        bundle2.putString(MyIssuePagerfragment.MY_ISSUEDETAIL_KEY, "outdate");
        adapter.addTab("已过期", "", MyIssueDetail.class, bundle2);
    }
}
