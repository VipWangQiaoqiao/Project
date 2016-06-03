package net.oschina.app.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
public class ViewNewsHeader extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private ViewPager vp_news;
    private List<ViewNewsBanner> banners = new ArrayList<>();
    private NewsPagerAdapter adapter;
    private SuperRefreshLayout refreshLayout;
    private ScheduledExecutorService mSchedule;
    private int mCurrentItem = 0;
    private boolean isMoving = false;
    private boolean isTouch = false;
    private Handler handler;

    public ViewNewsHeader(Context context) {
        super(context);
        init(context);
    }

    public ViewNewsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setRefreshLayout(SuperRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_list_news_header, this, true);
        vp_news = (ViewPager) findViewById(R.id.vp_news);
        adapter = new NewsPagerAdapter();
        vp_news.setAdapter(adapter);
        new SmoothScroller(getContext()).setViewPagerScroller(vp_news, getContext());
        vp_news.addOnPageChangeListener(this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                vp_news.setCurrentItem(mCurrentItem);
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
        }, 2, 6, TimeUnit.SECONDS);

        vp_news.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        isMoving = false;
                        isTouch = false;
                        break;
                    case MotionEvent.ACTION_DOWN:
                        isTouch = true;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        isTouch = false;
                        isMoving = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMoving = true;
                        isTouch = true;
                        break;
                    default:
                        isMoving = false;
                        isTouch = false;
                }
                return false;
            }
        });
    }

    public void initData(RequestManager manager, List<Banner> banners) {
        this.banners.clear();
        for (Banner banner : banners) {
            ViewNewsBanner newsBanner = new ViewNewsBanner(getContext());
            newsBanner.initData(manager, banner);
            this.banners.add(newsBanner);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        isMoving = mCurrentItem != position;
    }

    @Override
    public void onPageSelected(int position) {
        isMoving = false;
        mCurrentItem = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        isMoving = state != ViewPager.SCROLL_STATE_IDLE;
        if (!refreshLayout.isRefreshing() && !refreshLayout.isMoving() && isTouch)
            refreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
    }


    private class NewsPagerAdapter extends PagerAdapter {
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
