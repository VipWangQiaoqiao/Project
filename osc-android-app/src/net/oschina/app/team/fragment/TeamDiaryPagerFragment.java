package net.oschina.app.team.fragment;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.fragment.MyInformationFragment;
import net.oschina.app.team.adapter.TeamDiaryListAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamList;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TLog;

import org.apache.http.Header;
import org.kymjs.kjframe.utils.KJLoger;
import org.kymjs.kjframe.utils.PreferenceHelper;

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

import com.loopj.android.http.AsyncHttpResponseHandler;

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
    private Team team;

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

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            int index = bundle.getInt(MyInformationFragment.TEAM_LIST_KEY, 0);
            String cache = PreferenceHelper.readString(getActivity(),
                    MyInformationFragment.TEAM_LIST_FILE,
                    MyInformationFragment.TEAM_LIST_KEY);
            team = TeamList.toTeamList(cache).get(index);
        } else {
            team = new Team();
            TLog.log(getClass().getSimpleName(), "team对象初始化异常");
        }
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
            OSChinaApi.getDiaryFromWhichWeek(team.getId() + "", "2015",
                    position + "", new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int arg0, Header[] arg1,
                                byte[] arg2) {
                            KJLoger.debug("====" + new String(arg2));
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1,
                                byte[] arg2, Throwable arg3) {

                        }
                    });
        }
    }
}
