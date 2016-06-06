package net.oschina.app.viewpagerfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.adapter.ViewPageFragmentAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.BaseViewPagerFragment;
import net.oschina.app.bean.BlogList;
import net.oschina.app.bean.NewsList;
import net.oschina.app.fragment.general.BlogFragment;
import net.oschina.app.fragment.general.EventFragment;
import net.oschina.app.fragment.general.NewsFragment;
import net.oschina.app.fragment.general.QuestionFragment;
import net.oschina.app.interf.OnTabReselectListener;

/**
 * 综合Tab界面
 */
public class GeneralViewPagerFragment extends BaseViewPagerFragment implements
        OnTabReselectListener {

    private static final String TAG = "GeneralViewPagerFragment";


    @Override
    protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
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
        switch (currentIndex) {
            case 0:
                NewsFragment newsFragment = (NewsFragment) mTabsAdapter.getItem(currentIndex);
                if (newsFragment != null) {
                    newsFragment.onTabReselect();
                }
                break;
            case 1:
                BlogFragment blogFragment = (BlogFragment) mTabsAdapter.getItem(currentIndex);
                if (blogFragment != null) {
                    blogFragment.onTabReselect();
                }
                break;
            case 2:
                QuestionFragment questionFragment = (QuestionFragment) mTabsAdapter.getItem(currentIndex);
                if (questionFragment != null) {
                    questionFragment.onTabReselect();
                }
                break;
            case 3:
                EventFragment eventFragment = (EventFragment) mTabsAdapter.getItem(currentIndex);
                if (eventFragment != null) {
                    eventFragment.onTabReselect();
                }
                break;
            default:
                break;

        }

        Log.d(TAG, "onTabReselect: ----->" + mViewPager.getCurrentItem());
//       Fragment currentFragment = getChildFragmentManager().getFragments()
//               .get(currentIndex);
//
//       if (currentFragment != null
//                && currentFragment instanceof OnTabReselectListener) {
//           OnTabReselectListener listener = (OnTabReselectListener) currentFragment;
//           listener.onTabReselect();
//        }
    }
}