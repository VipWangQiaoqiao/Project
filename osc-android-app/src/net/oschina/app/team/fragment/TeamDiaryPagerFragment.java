package net.oschina.app.team.fragment;

import net.oschina.app.R;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.team.adapter.TeamDiaryListAdapter;
import net.oschina.app.util.StringUtils;

import org.kymjs.kjframe.KJHttp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 周报模块（外部无关模式,可独立于其他模块）
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
public class TeamDiaryPagerFragment extends BaseFragment {

    @InjectView(R.id.team_diary_pager)
    ViewPager mPager;
    @InjectView(R.id.team_diary_pager_title)
    TextView mTvTitle;
    @InjectView(R.id.team_diary_pager_calendar)
    ImageView mImgCalendar;
    @InjectView(R.id.team_diary_pager_left)
    ImageView mImgLeft;
    @InjectView(R.id.team_diary_pager_right)
    ImageView mImgRight;

    private Activity aty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        aty = getActivity();
        View view = View.inflate(getActivity(),
                R.layout.fragment_team_diarypager, null);
        ButterKnife.inject(this, view);
        initData();
        initView(view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mPager.setCurrentItem(StringUtils.getWeekOfYear());
        mPager.setAdapter(new DiaryPagerAdapter());
    }

    public class DiaryPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return StringUtils.getWeekOfYear();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View pagerView = View.inflate(aty, R.layout.pager_item_diary, null);
            ListView listview = (ListView) pagerView
                    .findViewById(R.id.diary_listview);
            initListPager(listview, position);
            switch (position % 2) {
            case 0:
                pagerView.setBackgroundColor(0xff00ff00);
                break;
            case 1:
                pagerView.setBackgroundColor(0xff00ffff);
                break;
            }
            (container).addView(pagerView);
            return pagerView;
        }

        /**
         * 不同的pager显示不同的数据，根据pager动态加载数据
         * 
         * @param view
         * @param position
         */
        private void initListPager(ListView view, int position) {
            view.setAdapter(new TeamDiaryListAdapter(aty));

            KJHttp kjh = new KJHttp();
        }
    }
}
