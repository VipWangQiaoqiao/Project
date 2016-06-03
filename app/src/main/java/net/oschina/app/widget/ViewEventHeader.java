package net.oschina.app.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.Banner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by huanghaibin
 * on 16-6-2.
 */
public class ViewEventHeader extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private ViewPager vp_event;
    private List<ViewEventBanner> banners = new ArrayList<>();
    private EventPagerAdapter adapter;
    private SuperRefreshLayout refreshLayout;
    private ScheduledExecutorService mSchedule;
    private int mCurrentItem = 0;
    private Handler handler;
    private boolean isMoving = false;
    public ViewEventHeader(Context context) {
        super(context);
        init(context);
    }

    public ViewEventHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setRefreshLayout(SuperRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_list_event_header, this, true);
        vp_event = (ViewPager) findViewById(R.id.vp_event);
        adapter = new EventPagerAdapter();
        vp_event.setAdapter(adapter);
        new SmoothScroller(getContext()).setViewPagerScroller(vp_event, getContext());
        vp_event.addOnPageChangeListener(this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                vp_event.setCurrentItem(mCurrentItem);
            }
        };
        mSchedule = Executors.newSingleThreadScheduledExecutor();
        mSchedule.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (!isMoving) {
                    mCurrentItem = (mCurrentItem + 1) % banners.size();
                    handler.obtainMessage().sendToTarget();
                }
            }
        }, 2, 5, TimeUnit.SECONDS);
    }

    public void initData(RequestManager manager, List<Banner> banners) {
        this.banners.clear();
        for (Banner banner : banners) {
            ViewEventBanner eventBanner = new ViewEventBanner(getContext());
            eventBanner.initData(manager, banner);
            this.banners.add(eventBanner);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        isMoving = true;
    }

    @Override
    public void onPageSelected(int position) {
        isMoving = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        isMoving = state != ViewPager.SCROLL_STATE_IDLE;
        if (!refreshLayout.isRefreshing() && !refreshLayout.isMoving())
            refreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
    }

    private class EventPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return banners.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(banners.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(banners.get(position));
            return banners.get(position);
        }
    }
}
