package net.oschina.app.improve.main.tabs;

import android.support.v4.app.Fragment;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.bean.SimpleBackPage;
import net.oschina.app.improve.base.fragments.BaseGeneralListFragment;
import net.oschina.app.improve.base.fragments.BaseViewPagerFragment;
import net.oschina.app.improve.general.fragments.BlogFragment;
import net.oschina.app.improve.general.fragments.EventFragment;
import net.oschina.app.improve.general.fragments.NewsFragment;
import net.oschina.app.improve.general.fragments.QuestionFragment;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.UIHelper;

/**
 * Created by fei
 * on 2016/9/5.
 * <p>
 * Changed qiujuer
 * on 2016/9/5.
 */
public class GeneralViewPagerFragment extends BaseViewPagerFragment implements OnTabReselectListener {


    @Override
    public void onTabReselect() {

        if (mBaseViewPager != null) {
            BaseViewPagerAdapter pagerAdapter = (BaseViewPagerAdapter) mBaseViewPager.getAdapter();
            Fragment fragment = pagerAdapter.getCurFragment();
            if (fragment != null && fragment instanceof BaseGeneralListFragment) {
                ((BaseGeneralListFragment) fragment).onTabReselect();
            }
        }
    }

    @Override
    protected PagerInfo[] getPagers() {

        String[] titles = getResources().getStringArray(R.array.general_viewpage_arrays);
        PagerInfo[] infoList = new PagerInfo[4];

        infoList[0] = new PagerInfo(titles[0], NewsFragment.class, null);
        infoList[1] = new PagerInfo(titles[1], BlogFragment.class, null);
        infoList[2] = new PagerInfo(titles[2], QuestionFragment.class, null);
        infoList[3] = new PagerInfo(titles[3], EventFragment.class, null);

        return infoList;
    }

    @Override
    protected int getTitleRes() {
        return R.string.main_tab_name_news;
    }

    @Override
    protected int getIconRes() {
        return 0;
    }

    @Override
    protected View.OnClickListener getIconClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SEARCH);
            }
        };
    }

}
