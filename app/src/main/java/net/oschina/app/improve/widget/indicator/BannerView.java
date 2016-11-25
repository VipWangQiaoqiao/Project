package net.oschina.app.improve.widget.indicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haibin
 * on 2016/11/18.
 */
@SuppressWarnings("all")
public class BannerView extends FrameLayout implements View.OnClickListener {

    private PointInfo mStart;
    private PointInfo mEnd;
    private ViewAdapter mAdapter;
    private int mCurrentPosition;

    private float mOffset;
    private float mAlpha;
    private float mNextAlpha;
    private boolean isUpdate;
    private boolean isClick;

    private VelocityTracker mTracker;
    private PagerObserver mObserver;

    private OnItemClickListener mOnItemClickListener;
    private List<OnViewChangeListener> mViewChangeListeners;

    public BannerView(Context context) {
        super(context);
        mStart = new PointInfo();
        mEnd = new PointInfo();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mStart = new PointInfo();
        mEnd = new PointInfo();
        setOnClickListener(this);
    }

    public void setCurrentItem(int position) {
        int count = mAdapter.getCount();
        if (position <= count - 1 && count >= 2) {
            this.mCurrentPosition = position == 0 ? mCurrentPosition = count - 1 : position - 1;
            selected(count);
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null && isClick) {
            mOnItemClickListener.onItemClick(mCurrentPosition);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mAdapter == null) return super.onTouchEvent(event);
        int count = mAdapter.getCount();
        if (count <= 1) return super.onTouchEvent(event);

        int with = getWidth();
        int height = getHeight();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStart.setX(event.getX());
                mStart.setY(event.getY());
                mTracker = VelocityTracker.obtain();
                requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                mOffset = mStart.getX() - event.getX();
                mAlpha = (with - Math.abs(mOffset)) / with;
                mTracker.addMovement(event);
                mTracker.computeCurrentVelocity(1000);
                if (event.getY() >= height) {
                    isClick = true;
                    return false;
                }
                if ((Math.abs(mOffset)) > 50) {
                    isClick = false;
                    notifyViewStateChanged(OnViewChangeListener.STATE_DRAGGING);
                    actionMove(count);
                }
                break;
            case MotionEvent.ACTION_UP://结束时候
                isClick = (Math.abs(mOffset)) < 50;
                actionUp(count);
                notifyViewStateChanged(OnViewChangeListener.STATE_IDLE);
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    private void actionMove(int count) {
        View curView = getChildAt(1);
        View preView = getChildAt(0);
        View nextView = getChildAt(2);
        curView.setAlpha(mAlpha);
        if (count == 2) {
            preView.setAlpha(1);
            notifyViewScrolled(mCurrentPosition == 1 ? 0 : 1);
        } else {
            if (mOffset > 0) {//向左滑+1
                int width = getWidth();
                preView.setAlpha(0);
                mNextAlpha = (Math.abs(mOffset)) / width;
                nextView.setAlpha(mNextAlpha);
                notifyViewScrolled(mCurrentPosition + 1);
            } else {
                preView.setAlpha(1);
                nextView.setAlpha(0);
                notifyViewScrolled(mCurrentPosition - 1);
            }
        }
    }

    private void actionUp(int count) {
        View curView = getChildAt(1);
        View preView = getChildAt(0);
        View nextView = getChildAt(2);

        if (mAlpha < 0.7 && mAlpha != 0.0f && !isUpdate) {//触发切换
            isUpdate = true;
            int index = 0;
            if (count == 2) {
                removeView(preView);
                addView(preView);
                mCurrentPosition = (mCurrentPosition + 1) % count;
            } else {
                if (mOffset <= 0) {//右滑-1
                    removeView(nextView);
                    if (mCurrentPosition == 0) {
                        mCurrentPosition = count - 1;
                        index = mCurrentPosition - 1;
                    } else {
                        --mCurrentPosition;
                        index = mCurrentPosition == 0 ? count - 1 : mCurrentPosition - 1;
                    }
                    View view = mAdapter.instantiateItem(this, index);
                    removeView(view);
                    addView(view, 0);
                } else {//左滑+1
                    mCurrentPosition = (mCurrentPosition + 1) % count;
                    this.removeView(preView);
                    preView.setAlpha(0);
                    //nextView.setAlpha(1);
                    showAlphaAnimation(nextView, mNextAlpha);
                    index = (mCurrentPosition + 1) % count;
                    mAdapter.instantiateItem(this, index);
                    getChildAt(2).setAlpha(0);
                    curView.setAlpha(1);
                }
            }
            //curView.setAlpha(0);
            hideAlphaAnimation(curView, mAlpha);
            notifySelected();
        } else {
            //curView.setAlpha(1);
            //避免点击闪烁
            if (mAlpha == 1.0f || mAlpha == 0.0f)
                return;
            showAlphaAnimation(curView, mAlpha);
            if (count >= 3) {
                preView.setAlpha(1);
                nextView.setAlpha(0);
            }
        }
        isUpdate = false;
    }

    private void selected(int count) {
        isUpdate = true;
        View curView = getChildAt(1);
        View preView = getChildAt(0);
        View nextView = getChildAt(2);
        int index = 0;
        if (count == 2) {
            removeView(preView);
            addView(preView);
        } else {
            mCurrentPosition = (mCurrentPosition + 1) % count;
            this.removeView(preView);
            preView.setAlpha(0);
            showAlphaAnimation(nextView, 0.3f);
            index = (mCurrentPosition + 1) % count;
            mAdapter.instantiateItem(this, index);
            getChildAt(2).setAlpha(0);
            notifySelected();
        }
        //curView.setAlpha(0);
        //hideAlphaAnimation(curView,0.5f);
        notifyViewStateChanged(OnViewChangeListener.STATE_IDLE);
        mOffset = 0.0f;
        mAlpha = 0.0f;
        isUpdate = false;
    }

    private void notifySelected() {
        if (mViewChangeListeners != null) {
            for (OnViewChangeListener listener : mViewChangeListeners) {
                listener.onViewSelected(mCurrentPosition);
            }
        }
    }

    private void notifyViewStateChanged(int state) {
        if (mViewChangeListeners != null) {
            for (OnViewChangeListener listener : mViewChangeListeners) {
                listener.onViewStateChanged(state);
            }
        }
    }

    private void notifyViewScrolled(int p) {
        if (mViewChangeListeners != null) {
            for (OnViewChangeListener listener : mViewChangeListeners) {
                listener.onViewScrolled(p, mOffset);
            }
        }
    }

    public void addOnViewChangeListener(OnViewChangeListener listener) {
        if (mViewChangeListeners == null) mViewChangeListeners = new ArrayList<>();
        mViewChangeListeners.add(listener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public void setAdapter(ViewAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.setViewPagerObserver(null);
        }
        final ViewAdapter oldAdapter = mAdapter;
        mAdapter = adapter;

        int count = mAdapter.getCount();

        if (count == 0)
            return;

        if (count >= 1) {
            mAdapter.instantiateItem(this, count - 1);
        }
        if (count >= 2) {
            mAdapter.instantiateItem(this, 0);
        }
        if (count >= 3) {
            mAdapter.instantiateItem(this, 1);
            getChildAt(2).setAlpha(0);
        }
    }

    void dataSetChanged() {

    }

    private void hideAlphaAnimation(final View view, float curAlpha) {
        view.clearAnimation();
        view.setAlpha(curAlpha);

        view.animate().alpha(0.f).setDuration(800).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setAlpha(0.f);
            }
        });
    }

    private void showAlphaAnimation(final View view, float curAlpha) {
        view.clearAnimation();
        view.setAlpha(curAlpha);
        view.animate().alpha(1.f).setDuration(800).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setAlpha(1.f);
            }
        });

    }

    private static class PointInfo {
        private float x;
        private float y;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    public abstract static class ViewAdapter {
        private final DataSetObservable mObservable = new DataSetObservable();
        private DataSetObserver mViewPagerObserver;

        public abstract View instantiateItem(ViewGroup parent, int position);

        public abstract int getCount();

        public void destroyItem(ViewGroup parent, View view) {
            parent.removeView(view);
        }

        void setViewPagerObserver(DataSetObserver observer) {
            synchronized (this) {
                mViewPagerObserver = observer;
            }
        }

        public void notifyDataSetChange() {
            synchronized (this) {
                if (mViewPagerObserver != null) {
                    mViewPagerObserver.onChanged();
                }
            }
            mObservable.notifyChanged();
        }
    }

    public ViewAdapter getAdapter() {
        return mAdapter;
    }

    public interface OnViewChangeListener {
        static final int STATE_IDLE = -1;
        static final int STATE_DRAGGING = 1;

        void onViewScrolled(int position, float positionOffset);

        void onViewSelected(int position);

        void onViewStateChanged(int state);
    }

    private class PagerObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
