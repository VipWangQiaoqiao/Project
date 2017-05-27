package net.oschina.app.improve.main.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Scroller;

import net.oschina.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 无限轮播Banner
 * Created by haibin
 * on 17/2/27.
 */
@SuppressWarnings("all")
public class BannerView extends ViewGroup implements View.OnClickListener {
    private int duration;
    private BannerTransformer mTransformer;
    private float mHorizontalOffset;
    private float mVerticalOffset;
    private int mTotalScrollX;
    private Scroller mScroller;
    private ItemInfo mLastViewItem, mPreViewItem, mCurViewItem;
    private View mCurrentView;
    private DataSetObserver mBannerObserver;
    private float mLastX;
    private int mTotalDx;
    private int mCurItem;
    private int mMaximumVelocity;
    private float mXVelocity;
    private static final int MIN_FLING_VELOCITY = 400; // dips
    private BaseBannerAdapter mAdapter;
    private VelocityTracker mVelocityTracker;
    private boolean isScrollBack;
    private boolean isToLeft;
    private boolean isTouch;
    private List<OnBannerChangeListener> mBannerChangeListeners;
    private OnBannerItemClicklistener mOnItemClickListener;

    public BannerView(Context context) {
        this(context, null, 0);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        mScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        final float density = context.getResources().getDisplayMetrics().density;

        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        mHorizontalOffset = array.getDimension(R.styleable.BannerView_horizontal_offset, 0);
        mVerticalOffset = array.getDimension(R.styleable.BannerView_vertical_offset, 0);
        duration = array.getInt(R.styleable.BannerView_scroll_duration, 500);
        array.recycle();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isTouch = true;
        if (event.getPointerCount() >= 2) return false;
        if (mAdapter == null) return super.onTouchEvent(event);
        int count = mAdapter.getCount();
        if (count <= 1) return super.onTouchEvent(event);
        float x = event.getRawX();
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) { // 如果上次的调用没有执行完就取消。
                    return false;
                }
                mLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                requestDisallowInterceptTouchEvent(true);
                int dxMove = (int) (mLastX - x);
                mTotalDx += dxMove;
                onScrollBy(dxMove);
                mLastX = x;
                return true;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if (Math.abs(mTotalDx) <= 10)
                    performClick();
            case MotionEvent.ACTION_CANCEL: {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                mXVelocity = velocityTracker.getXVelocity();
                isTouch = false;
                scrollToItem();
                scrollFinish(count);
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void setCurrentItem(int item) {
        if (item <= 0 && item <= mAdapter.getCount() - 1 && item != mCurItem) {
            if (item < mCurItem) mXVelocity = -1301;
            else mXVelocity = 1301;
            this.mCurItem = item;
            scrollToItem();
            scrollFinish(mAdapter.getCount());
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null && mAdapter != null && mAdapter.getCount() != 0)
            mOnItemClickListener.onItemClick(mCurItem);
    }

    public void scrollToNext() {
        if (isTouch || mAdapter == null || mAdapter.getCount() <= 1 || !mScroller.isFinished())
            return;
        mXVelocity = -1311;
        scrollToItem();
        scrollFinish(mAdapter.getCount());
    }

    public void setBannerOnItemClickListener(OnBannerItemClicklistener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setTransformer(BannerTransformer transformer) {
        this.mTransformer = transformer;
    }

    private void onScrollBy(int dx) {
        if (mBannerChangeListeners != null) {
            for (OnBannerChangeListener listener : mBannerChangeListeners) {
                listener.onViewStateChanged(OnBannerChangeListener.STATE_DRAGGING);
            }
        }
        scrollBy(dx, 0);
        isScrollBack = false;
        isToLeft = mTotalDx < 0;
        if (mTransformer != null) {
            mTransformer.onScroll(mCurViewItem.view, mPreViewItem.view, mLastViewItem.view, getWidth(), mTotalDx);
        }
        if (mBannerChangeListeners != null) {
            for (OnBannerChangeListener listener : mBannerChangeListeners) {
                listener.onViewScrolled(mCurItem, mTotalDx);
            }
        }
    }

    private void scrollToItem() {
        int width = getWidth();
        int dx, scrollX = getScrollX();
        if (Math.abs(mXVelocity) >= 1300) {
            if (mXVelocity > 0) {//前一页
                dx = -width - mTotalDx;
                scrollX -= 3 * mHorizontalOffset;
                dx += 3 * mHorizontalOffset;
                isScrollBack = false;
            } else {//下一页
                dx = width - mTotalDx;
                scrollX += 3 * mHorizontalOffset;
                dx -= 3 * mHorizontalOffset;
                isScrollBack = false;
            }
        } else {
            if (mTotalDx < 0) {
                if (mTotalDx < -(width / 2)) {//前一页
                    dx = -width - mTotalDx;
                    scrollX -= 3 * mHorizontalOffset;
                    dx += 3 * mHorizontalOffset;
                    isScrollBack = false;
                } else {//右回滚
                    isScrollBack = true;
                    dx = -mTotalDx;
                    mCurViewItem.view.setScaleY(1);
                }
            } else {
                if (mTotalDx > width / 2) {//下一页
                    dx = width - mTotalDx;
                    scrollX += 3 * mHorizontalOffset;
                    dx -= 3 * mHorizontalOffset;
                    isScrollBack = false;
                } else {//左回滚
                    mCurViewItem.view.setScaleY(1);
                    dx = -mTotalDx;
                    isScrollBack = true;
                }
            }
        }
        mScroller.startScroll(scrollX, 0, dx, 0, duration);
        invalidate();
    }

    /**
     * 左边第一个的时候
     */
    private void scrollLeftEdge(int count) {
        int width = getWidth();
        View preView = mPreViewItem.view;
        int prePosition = mPreViewItem.position;
        View curView = mCurViewItem.view;
        preView.setScaleY(curView.getScaleY());
        mCurrentView = preView;
        int curPosition = mCurViewItem.position;

        int i = (mCurItem + 1) % count;
        mAdapter.destroyItem(this, i, mLastViewItem.view);//移除最右边的view

        int index = (mCurItem - 2 + count) % count;
        View pre = mAdapter.instantiateItem(index);
        mPreViewItem.view = pre;
        mPreViewItem.position = index;
        addView(pre, 0);

        mTotalScrollX -= width;

        mLastViewItem.position = curPosition;
        mLastViewItem.view = curView;
        mCurViewItem.view = preView;
        mCurViewItem.position = prePosition;
    }

    /**
     * 左边第一个的时候
     */
    private void scrollRightEdge(int count) {
        View lastView = mLastViewItem.view;
        int lastPosition = mLastViewItem.position;
        View curView = mCurViewItem.view;
        mCurrentView = lastView;
        lastView.setScaleY(curView.getScaleY());
        int curPosition = mCurViewItem.position;

        int i = (mCurItem - 1) % count;
        mAdapter.destroyItem(this, i, mPreViewItem.view);//移除最左边的view

        int index = (mCurItem + 2) % count;
        int width = getWidth();

        View last = mAdapter.instantiateItem(index);
        mLastViewItem.view = last;
        mLastViewItem.position = index;
        addView(last);

        mTotalScrollX += width;

        mCurViewItem.view = lastView;
        mCurViewItem.position = lastPosition;
        mPreViewItem.view = curView;
        mPreViewItem.position = curPosition;
    }

    /**
     * 滚动完成时
     */
    private void scrollFinish(int count) {
        int width = getWidth();
        if (Math.abs(mXVelocity) >= 1300) {
            if (mXVelocity > 0) {//前一页
                scrollLeftEdge(count);
                if (mCurItem == 0)
                    mCurItem = count - 1;
                else
                    mCurItem -= 1;
                if (mBannerChangeListeners != null) {
                    for (OnBannerChangeListener listener : mBannerChangeListeners) {
                        listener.onViewSelected(mCurItem);
                    }
                }
            } else {//下一页
                scrollRightEdge(count);
                if (mCurItem == count - 1)
                    mCurItem = 0;
                else
                    mCurItem += 1;
                if (mBannerChangeListeners != null) {
                    for (OnBannerChangeListener listener : mBannerChangeListeners) {
                        listener.onViewSelected(mCurItem);
                    }

                }
            }
        } else {
            if (mTotalDx < 0) {
                if (mTotalDx <= -(width / 2)) {//前一页
                    scrollLeftEdge(count);
                    if (mCurItem == 0)
                        mCurItem = count - 1;
                    else
                        mCurItem -= 1;
                    if (mBannerChangeListeners != null) {
                        for (OnBannerChangeListener listener : mBannerChangeListeners) {
                            listener.onViewSelected(mCurItem);
                        }

                    }
                }
            } else {
                if (mTotalDx >= width / 2) {//下一页
                    scrollRightEdge(count);
                    if (mCurItem == count - 1)
                        mCurItem = 0;
                    else
                        mCurItem += 1;
                    if (mBannerChangeListeners != null) {
                        for (OnBannerChangeListener listener : mBannerChangeListeners) {
                            listener.onViewSelected(mCurItem);
                        }
                    }
                }
            }
        }

        mTotalDx = 0;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mTransformer != null)
                mTransformer.curPageTransform(mCurrentView, mPreViewItem.view, mLastViewItem.view, isScrollBack, getWidth(), mScroller.getCurrX(), mTotalScrollX, isToLeft);
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
            if (mBannerChangeListeners != null) {
                for (OnBannerChangeListener listener : mBannerChangeListeners) {
                    listener.onViewStateChanged(mScroller.getCurrX() % getWidth() == 0 ? OnBannerChangeListener.STATE_IDLE : OnBannerChangeListener.STATE_DRAGGING);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int ws, hs;
            if (i == 0) {
                ws = MeasureSpec.makeMeasureSpec(width - 2 * (int) mHorizontalOffset, MeasureSpec.EXACTLY);
                hs = MeasureSpec.makeMeasureSpec(height - 3 * (int) mVerticalOffset, MeasureSpec.EXACTLY);
            } else if (i == 1) {
                ws = MeasureSpec.makeMeasureSpec(width - 2 * (int) mHorizontalOffset, MeasureSpec.EXACTLY);
                hs = MeasureSpec.makeMeasureSpec(height - 2 * (int) mVerticalOffset, MeasureSpec.EXACTLY);
            } else {
                ws = MeasureSpec.makeMeasureSpec(width - 2 * (int) mHorizontalOffset, MeasureSpec.EXACTLY);
                hs = MeasureSpec.makeMeasureSpec(height - 3 * (int) mVerticalOffset, MeasureSpec.EXACTLY);
            }
            measureChild(childView, ws, hs);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = -1; i < childCount - 1; i++) {
            View childView = getChildAt(i + 1);
            int childW = childView.getMeasuredWidth();
            int childH = childView.getMeasuredHeight();
            int left, right, top, bottom;
            if (i == -1) {
                if (getChildCount() == 1) {
                    left = mTotalScrollX + 2 * (int) mHorizontalOffset;
                    right = childW + mTotalScrollX + (int) mHorizontalOffset;
                    top = 3 * (int) mVerticalOffset / 2;
                    bottom = top + childH;
                } else {
                    left = -childW + mTotalScrollX + 2 * (int) mHorizontalOffset;
                    right = mTotalScrollX + (int) mHorizontalOffset;
                    top = 3 * (int) mVerticalOffset / 2;
                    bottom = top + childH;
                }
            } else if (i == 0) {//当前居中项
                left = mTotalScrollX + (int) mHorizontalOffset;
                right = left + childW;
                top = (int) mVerticalOffset;
                bottom = top + childH;
            } else {
                left = childW + mTotalScrollX + (int) mHorizontalOffset;
                right = left + childW;
                top = 3 * (int) mVerticalOffset / 2;
                bottom = top + childH;
            }

            childView.layout(left, top, right, bottom);
        }
    }

    public BaseBannerAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseBannerAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.setViewPagerObserver(null);
            this.mCurItem = 0;
            this.removeAllViews();
            this.scrollTo(0, 0);
        }
        this.mAdapter = adapter;
        if (mAdapter != null) {
            if (mBannerObserver == null) mBannerObserver = new BannerObserver();
            this.mAdapter.setViewPagerObserver(this.mBannerObserver);
            measureView();
        }
    }

    public void addOnBannerChangeListener(OnBannerChangeListener mBannerChangeListener) {
        if (this.mBannerChangeListeners == null)
            this.mBannerChangeListeners = new ArrayList<>();
        this.mBannerChangeListeners.add(mBannerChangeListener);
    }

    private void setupViewInfo() {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ViewParent parent = this;
        while (parent != null && parent.getParent() != null
                && !((parent = parent.getParent()) instanceof ViewPager)) ;
        parent.requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    private void measureView() {
        int count = mAdapter.getCount();
        if (count == 0) return;

        mCurItem = 0;
        View cur = mAdapter.instantiateItem(0);
        mCurViewItem = new ItemInfo();
        mCurViewItem.view = cur;
        mCurViewItem.position = 0;
        mCurrentView = cur;
        addView(cur);

        if (count == 1) return;
        mPreViewItem = new ItemInfo();
        View pre = mAdapter.instantiateItem(count - 1);//最后一个View
        mPreViewItem.view = pre;
        mPreViewItem.position = count - 1;
        addView(pre, 0);

        mLastViewItem = new ItemInfo();
        View last = mAdapter.instantiateItem(1);//最后一个View
        mLastViewItem.view = last;
        mLastViewItem.position = 1;
        addView(last);
    }

    void dataSetChanged() {
        mScroller.startScroll(0, 0, 0, 0, 0);
        this.mCurItem = 0;
        this.removeAllViews();
        mCurViewItem = null;
        mPreViewItem = null;
        mLastViewItem = null;
        mTotalScrollX = 0;
        measureView();
        this.scrollTo(0, 0);
        requestLayout();
        if (mBannerChangeListeners != null) {
            for (OnBannerChangeListener listener : mBannerChangeListeners) {
                listener.onViewSelected(mCurItem);
            }
        }
    }

    private class BannerObserver extends DataSetObserver {
        BannerObserver() {
            super();
        }

        @Override
        public void onChanged() {
            BannerView.this.dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            BannerView.this.dataSetChanged();
        }
    }

    static class ItemInfo {
        View view;
        int position;
        int pagerWidth;
    }

    public interface BannerTransformer {

        void curPageTransform(View cur, View pre, View next, boolean isScrollBack, int parentWidth, int currX, int mTotalScrollX, boolean isToLeft);

        /**
         * @param cur         当前
         * @param pre         前一个
         * @param next        下一个
         * @param parentWidth banner的宽
         * @param dx          偏移量
         */
        void onScroll(View cur, View pre, View next, int parentWidth, int dx);
    }

    public interface OnBannerChangeListener {
        public static final int STATE_IDLE = -1;
        public static final int STATE_DRAGGING = 1;

        void onViewScrolled(int position, float positionOffset);

        void onViewSelected(int position);

        void onViewStateChanged(int state);
    }

    public interface OnBannerItemClicklistener {
        void onItemClick(int position);
    }
}
