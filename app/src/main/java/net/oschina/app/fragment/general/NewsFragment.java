package net.oschina.app.fragment.general;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.adapter.NewsAdapter;
import net.oschina.app.base.BaseListFragment;
import net.oschina.app.base.ListBaseAdapter;
import net.oschina.app.bean.News;

import java.util.ArrayList;
import java.util.List;

/**
 * 资讯界面
 */
public class NewsFragment extends BaseListFragment<News> {
    private View mHeaderView;
    ViewPager viewPager;

    List<Fragment> fragments = new ArrayList<>();

    @Override
    public void initView(View view) {
        mHeaderView = mInflater.inflate(R.layout.view_news_header, null);
        super.initView(view);


    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    protected ListBaseAdapter<News> getListAdapter() {
        return new NewsAdapter();
    }

    class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
