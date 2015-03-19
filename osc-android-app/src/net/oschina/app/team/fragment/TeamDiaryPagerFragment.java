package net.oschina.app.team.fragment;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.base.BaseFragment;
import net.oschina.app.cache.CacheManager;
import net.oschina.app.team.adapter.TeamDiaryListAdapter;
import net.oschina.app.team.bean.Team;
import net.oschina.app.team.bean.TeamDiary;
import net.oschina.app.team.bean.TeamDiaryList;
import net.oschina.app.team.ui.TeamMainActivity;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.UIHelper;
import net.oschina.app.util.XmlUtils;

import org.apache.http.Header;
import org.kymjs.kjframe.http.core.KJAsyncTask;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 周报模块（外部无关模式,可独立于其他模块）<br>
 * 首次进入，跳转至最后一周的周报信息，并读取文件缓存中的数据到内存缓存。<br>
 * 当滑动ViewPager时，切换并加载下一周或上一周的周报数据，并存入内存缓存中
 * 
 * @author kymjs (https://github.com/kymjs)
 */
public class TeamDiaryPagerFragment extends BaseFragment implements
        OnDateSetListener {
    private static String TAG = "TeamDiaryPagerFragment";
    public static String DIARYDETAIL_KEY = "team_diary_detail_key";
    public static String TEAMID_KEY = "team_diary_teamid_key";

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
    private int currentWeek;
    private int currentYear = 2015;
    private Map<Integer, TeamDiaryList> dataBundleList; // 用于实现二级缓存
    private final Calendar calendar = Calendar.getInstance();

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
        Bundle bundle = getArguments();
        if (bundle != null) {
            team = (Team) bundle
                    .getSerializable(TeamMainActivity.BUNDLE_KEY_TEAM);
        }
        TAG += team.getId();
        currentWeek = StringUtils.getWeekOfYear();
        dataBundleList = new HashMap<Integer, TeamDiaryList>(currentWeek);
        // 由于数据量比较大，做二级缓存
        KJAsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < currentWeek; i++) {
                    TeamDiaryList dataBundle = ((TeamDiaryList) CacheManager
                            .readObject(aty, TAG + i));
                    if (dataBundle != null) {
                        if (dataBundleList.get(i) != null) {
                            dataBundleList.remove(i);
                        }
                        dataBundleList.put(i, dataBundle);
                    }
                }
            }
        });
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mPager.setAdapter(new DiaryPagerAdapter());
        mPager.setCurrentItem(currentWeek); // 首先显示当前周，然后左翻页查看历史

        final int currentPage = mPager.getCurrentItem();
        if (currentPage == 0) {
            mImgLeft.setImageResource(R.drawable.ic_diary_back);
        } else {
            mImgLeft.setImageResource(R.drawable.ic_diary_canback);
        }
        if (currentPage == currentWeek - 1) {
            mImgRight.setImageResource(R.drawable.ic_diary_forward);
        } else {
            mImgRight.setImageResource(R.drawable.ic_diary_canforward);
        }

        mTvTitle.setText("第" + currentWeek + "周周报总览");
        mImgCalendar.setOnClickListener(this);
        mImgLeft.setOnClickListener(this);
        mImgRight.setOnClickListener(this);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {}

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageScrollStateChanged(int arg0) {
                int currentPage = mPager.getCurrentItem();
                if (currentPage == 0) {
                    mImgLeft.setImageResource(R.drawable.ic_diary_back);
                } else {
                    mImgLeft.setImageResource(R.drawable.ic_diary_canback);
                }
                if (currentPage == currentWeek - 1) {
                    mImgRight.setImageResource(R.drawable.ic_diary_forward);
                } else {
                    mImgRight.setImageResource(R.drawable.ic_diary_canforward);
                }
                mTvTitle.setText("第" + (currentPage + 1) + "周周报总览");
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        case R.id.team_diary_pager_right:
            int currentPage1 = mPager.getCurrentItem();
            if (currentPage1 < mPager.getAdapter().getCount()) {
                mPager.setCurrentItem(currentPage1 + 1);
            }
            break;
        case R.id.team_diary_pager_left:
            int currentPage2 = mPager.getCurrentItem();
            if (currentPage2 > 0) {
                mPager.setCurrentItem(currentPage2 - 1);
            }
            break;
        case R.id.team_diary_pager_calendar:
            final DatePickerDialog datePickerDialog = DatePickerDialog
                    .newInstance(this, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH), false);
            datePickerDialog.setVibrate(false);
            datePickerDialog.setYearRange(2014, 2015);
            datePickerDialog.show(getFragmentManager(), "datepicker");
            break;
        default:
            break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year,
            int month, int day) {
        int[] dateBundle = StringUtils.getCurrentDate();
        if ((dateBundle[0] == year && dateBundle[1] <= month)
                || (dateBundle[0] == year && dateBundle[1] == month + 1 && dateBundle[2] < day)) {
            AppContext.showToast("那天怎么会有周报呢");
        } else {
            currentYear = year;
            currentWeek = StringUtils.getWeekOfYear(new Date(year, month, day));
            mPager.setAdapter(new DiaryPagerAdapter());
            mPager.setCurrentItem(currentWeek);
            mTvTitle.setText("第" + (currentWeek) + "周周报总览");
        }
    }

    /************************* pager adapter *******************************/

    public class DiaryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return currentYear == 2015 ? StringUtils.getWeekOfYear() : 52;
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
            initPagerContent(pagerView, position + 1); // +1是因为没有第0周，从1开始数
            (container).addView(pagerView);
            return pagerView;
        }

        private void initPagerContent(View pagerView, final int whichWeek) {
            final ListView listview = (ListView) pagerView
                    .findViewById(R.id.diary_listview);
            final SwipeRefreshLayout pullHeadView = (SwipeRefreshLayout) pagerView
                    .findViewById(R.id.swiperefreshlayout);
            final EmptyLayout errorLayout = (EmptyLayout) pagerView
                    .findViewById(R.id.error_layout);
            initListContent(errorLayout, listview, whichWeek);
            errorLayout.setOnLayoutClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentFromNet(errorLayout, pullHeadView, listview,
                            whichWeek);
                }
            });

            pullHeadView.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mState == STATE_REFRESH) {
                        return;
                    } else {
                        // 设置顶部正在刷新
                        setSwipeRefreshLoadingState(pullHeadView);
                        /* !!! 设置耗时操作 !!! */
                        setContentFromNet(null, pullHeadView, listview,
                                whichWeek);
                        errorLayout.setErrorMessage("本周无人提交周报");
                    }
                }
            });
            pullHeadView.setColorSchemeResources(R.color.swiperefresh_color1,
                    R.color.swiperefresh_color2, R.color.swiperefresh_color3,
                    R.color.swiperefresh_color4);
        }

        /**
         * 设置顶部正在加载的状态
         */
        private void setSwipeRefreshLoadingState(
                SwipeRefreshLayout mSwipeRefreshLayout) {
            mState = STATE_REFRESH;
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(true);
                // 防止多次重复刷新
                mSwipeRefreshLayout.setEnabled(false);
            }
        }

        /**
         * 设置顶部加载完毕的状态
         */
        private void setSwipeRefreshLoadedState(
                SwipeRefreshLayout mSwipeRefreshLayout) {
            mState = STATE_NOMORE;
            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(true);
            }
        }

        /************************* list content *******************************/

        /**
         * 不同的pager显示不同的数据，根据pager动态加载数据
         * 
         * @param view
         * @param position
         */
        private void initListContent(EmptyLayout errorLayout, ListView view,
                final int whichWeek) {
            final TeamDiaryList dataBundle = dataBundleList.get(whichWeek);
            if (dataBundle == null) {
                setContentFromNet(errorLayout, null, view, whichWeek);
            } else {
                setContentFromCache(errorLayout, view, dataBundle);
            }
            view.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    Bundle args = new Bundle();
                    args.putInt(TEAMID_KEY, team.getId());
                    args.putSerializable(
                            TeamDiaryPagerFragment.DIARYDETAIL_KEY,
                            dataBundleList.get(whichWeek).getList()
                                    .get(position));
                    UIHelper.showDiaryDetail(aty, args);
                }
            });
        }

        /* self annotation */
        private void setContentFromNet(final EmptyLayout errorLayout,
                final SwipeRefreshLayout pullHeadView, final ListView view,
                final int whichWeek) {
            OSChinaApi.getDiaryFromWhichWeek(team.getId(), currentYear,
                    whichWeek, new AsyncHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            if (errorLayout != null) {
                                ListAdapter adapter = view.getAdapter();
                                errorLayout
                                        .setErrorType(EmptyLayout.NETWORK_LOADING);
                                if (adapter == null && errorLayout != null) {
                                    errorLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(int arg0, Header[] arg1,
                                byte[] arg2, Throwable arg3) {
                            /* 网络异常 */
                            if (errorLayout != null) {
                                errorLayout
                                        .setErrorType(EmptyLayout.NETWORK_ERROR);
                                errorLayout.setVisibility(View.VISIBLE);
                            }
                            if (pullHeadView != null) {
                                setSwipeRefreshLoadedState(pullHeadView);
                            }
                        }

                        @Override
                        public void onSuccess(int arg0, Header[] arg1,
                                byte[] arg2) {
                            final TeamDiaryList bundle = XmlUtils.toBean(
                                    TeamDiaryList.class,
                                    new ByteArrayInputStream(arg2));

                            new Thread(new Runnable() {
                                // dataBundleList没有加入线程安全，由于whichWeek对应的value只在此处会修改，
                                // 线程冲突概率非常小，为了ListView流畅性，忽略线程安全性
                                @Override
                                public void run() {
                                    if (dataBundleList.get(whichWeek) != null) {
                                        dataBundleList.remove(whichWeek);
                                    }
                                    dataBundleList.put(whichWeek, bundle);
                                    CacheManager.saveObject(aty, bundle, TAG
                                            + whichWeek);
                                }
                            }).start();

                            List<TeamDiary> tempData = bundle.getList();
                            if ((tempData == null || tempData.isEmpty())
                                    && errorLayout != null) {
                                errorLayout.setNoDataContent("本周无人提交周报");
                                errorLayout.setErrorType(EmptyLayout.NODATA);
                                errorLayout.setVisibility(View.VISIBLE);
                            } else {
                                if (errorLayout != null) {
                                    errorLayout.setVisibility(View.GONE);
                                }
                                view.setAdapter(new TeamDiaryListAdapter(aty,
                                        tempData));
                            }

                            if (pullHeadView != null) {
                                setSwipeRefreshLoadedState(pullHeadView);
                            }
                        }
                    });
        }

        /* self annotation */
        private void setContentFromCache(EmptyLayout errorLayout,
                ListView view, TeamDiaryList dataBundle) {
            List<TeamDiary> tempData = dataBundle.getList();
            if ((tempData == null || tempData.isEmpty()) && errorLayout != null) {
                errorLayout.setNoDataContent("本周还没有人提交周报");
                errorLayout.setErrorType(EmptyLayout.NODATA);
                errorLayout.setVisibility(View.VISIBLE);
            }
            view.setAdapter(new TeamDiaryListAdapter(aty, tempData));
        }
    }
}
