package net.oschina.app.improve.base.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import net.oschina.app.R;

import butterknife.Bind;

/**
 * Created by fei
 * on 2016/9/5.
 * <p>
 * Changed qiujuer
 * on 2016/9/5.
 */
public abstract class BaseViewPagerFragment extends BaseTitleFragment {
    @Bind(R.id.tab_nav)
    protected TabLayout mTabNav;

    @Bind(R.id.base_viewPager)
    protected ViewPager mBaseViewPager;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_base_viewpager;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        BaseViewPagerAdapter adapter = new BaseViewPagerAdapter(getChildFragmentManager(), getPagers());
        mBaseViewPager.setAdapter(adapter);
        mTabNav.setupWithViewPager(mBaseViewPager);
        mBaseViewPager.setCurrentItem(0, true);
    }

    protected abstract PagerInfo[] getPagers();

    public static class PagerInfo {
        private String title;
        private Class<?> clx;
        private Bundle args;
        private Fragment fragment;


        public PagerInfo(String title, Class<?> clx, Bundle args) {
            this.title = title;
            this.clx = clx;
            this.args = args;
        }
    }

    public class BaseViewPagerAdapter extends FragmentPagerAdapter {
        private PagerInfo[] mInfoList;

        public BaseViewPagerAdapter(FragmentManager fm, PagerInfo[] infoList) {
            super(fm);
            mInfoList = infoList;
        }

        @Override
        public Fragment getItem(int position) {
            PagerInfo info = mInfoList[position];
            Fragment fragment = info.fragment;
            if (fragment == null) {
                fragment = Fragment.instantiate(getContext(), info.clx.getName(), info.args);
                info.fragment = fragment;
            }
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            mInfoList[position].fragment = null;
        }

        @Override
        public int getCount() {
            return mInfoList.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mInfoList[position].title;
        }

    }
}
