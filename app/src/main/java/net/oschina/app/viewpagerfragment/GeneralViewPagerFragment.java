package net.oschina.app.viewpagerfragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.BlogList;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.general.fragments.BlogFragment;
import net.oschina.app.improve.general.fragments.EventFragment;
import net.oschina.app.improve.general.fragments.NewsFragment;
import net.oschina.app.improve.general.fragments.QuestionFragment;
import net.oschina.app.improve.utils.UIUtil;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.UIHelper;

/**
 * 综合Tab界面
 */
public class GeneralViewPagerFragment extends BaseViewPagerFragment implements
        OnTabReselectListener {

    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {

        FrameLayout generalActionBar = (FrameLayout) mRoot.findViewById(R.id.general_actionbar);
        TextView tvTitle = (TextView) generalActionBar.findViewById(R.id.tv_explore_scan);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) generalActionBar.getLayoutParams();
            layoutParams.topMargin = UIUtil.getStatusHeight(getActivity());
        }
        ImageView ivDiscover = (ImageView) generalActionBar.findViewById(R.id.iv_explore_discover);

        tvTitle.setText(R.string.main_tab_name_news);
        ivDiscover.setVisibility(View.VISIBLE);
        ivDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SEARCH);
            }
        });


        String[] title = getResources().getStringArray(
                R.array.general_viewpage_arrays);

        adapter.addTab(title[0], "news", NewsFragment.class,
                getBundle(NewsList.CATALOG_ALL));
        adapter.addTab(title[1], "latest_blog", BlogFragment.class,
                getBundle(NewsList.CATALOG_WEEK));
        adapter.addTab(title[2], "question", QuestionFragment.class,
                getBundle(BlogList.CATALOG_LATEST));
        adapter.addTab(title[3], "activity", EventFragment.class,
                getBundle(BlogList.CATALOG_RECOMMEND));
    }

    private Bundle getBundle(int newType) {
        Bundle bundle = new Bundle();
        bundle.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, newType);
        return bundle;
    }

    @Override
    protected void setScreenPageLimit() {
        mViewPager.setOffscreenPageLimit(3);
    }

    /**
     * 基类会根据不同的catalog展示相应的数据
     *
     * @param catalog 要显示的数据类别
     * @return
     */
    private Bundle getBundle(String catalog) {
        Bundle bundle = new Bundle();
        bundle.putString(BlogFragment.BUNDLE_BLOG_TYPE, catalog);
        return bundle;
    }

    @Override
    public void onTabReselect() {
        Fragment fragment = mTabsAdapter.getItem(mViewPager.getCurrentItem());
        if (fragment != null && fragment instanceof BaseGeneralListFragment) {
            ((BaseGeneralListFragment) fragment).onTabReselect();
        }
    }
}