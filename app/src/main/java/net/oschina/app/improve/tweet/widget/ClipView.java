package net.oschina.app.improve.tweet.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import net.qiujuer.genius.ui.Ui;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class ClipView extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    private static boolean IS_UP_KITKAT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    private Path mPath;
    private boolean mInAnim = IS_UP_KITKAT;
    private float mStartRadius, mEndRadius;
    private float mStartPointX, mEndPointX;
    private float mStartPointY, mEndPointY;
    private int mColor;
    private float mProgress;

    private int[] mStartLocation;
    private int[] mStartSize;

    public ClipView(Context context) {
        super(context);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setup(int[] startLocation, int[] startSize) {
        if (startLocation == null)
            startLocation = new int[]{0, 0};
        if (startSize == null)
            startSize = new int[]{0, 0};
        mStartLocation = startLocation;
        mStartSize = startSize;
        requestLayout();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!IS_UP_KITKAT) {
            super.dispatchDraw(canvas);
            return;
        }
        if (mProgress <= 0)
            return;
        if (mInAnim && mPath != null) {
            int saveCount = canvas.save();
            canvas.clipPath(mPath);
            super.dispatchDraw(canvas);
            canvas.drawColor(mColor);
            canvas.restoreToCount(saveCount);
        } else {
            super.dispatchDraw(canvas);
        }
    }

    @Override
    public void setLayerType(int layerType, Paint paint) {
        if (paint == null && getLayerType() == layerType) {
            return;
        }
        if (IS_UP_KITKAT)
            layerType = View.LAYER_TYPE_SOFTWARE;
        super.setLayerType(layerType, paint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mStartLocation == null || mStartSize == null)
            return;

        int[] selfLocation = new int[2];
        getLocationOnScreen(selfLocation);

        int pointX = mStartLocation[0] - selfLocation[0];
        int pointY = mStartLocation[1] - selfLocation[1];

        int sizePointX = mStartSize[0] >> 1;
        int sizePointY = mStartSize[1] >> 1;

        mStartPointX = pointX + sizePointX;
        mStartPointY = pointY + sizePointY;

        // Check the point by start
        if (mStartPointX < 0) {
            mStartPointX = sizePointX;
        }
        if (mStartPointX > w) {
            mStartPointX = w - sizePointX;
        }
        if (mStartPointY < 0) {
            mStartPointY = sizePointY;
        }
        if (mStartPointY > h) {
            mStartPointY = h - sizePointY;
        }

        // End
        mEndPointX = w >> 1;
        mEndPointY = h >> 1;

        mStartRadius = Math.min(sizePointX, sizePointY);
        mEndRadius = (float) Math.sqrt(mEndPointY * mEndPointY + mEndPointX * mEndPointX);
    }


    private void doUpdate(float progress) {
        mColor = Ui.changeColorAlpha(0x24cf5f, (int) (240 - (240 * progress)));

        Path path = mPath;
        path.reset();
        float radius = mStartRadius + (mEndRadius - mStartRadius) * progress;
        float pointX = mStartPointX + (mEndPointX - mStartPointX) * progress;
        float pointY = mStartPointY + (mEndPointY - mStartPointY) * progress;
        path.addCircle(pointX, pointY, radius, Path.Direction.CW);
        invalidate();
    }

    private ValueAnimator mAnimator;

    private ValueAnimator getAnimator(boolean isEnter, int time, Runnable runnable) {
        mDoRunnable = runnable;
        final float nowProgress = mProgress;

        if (mAnimator == null) {
            ValueAnimator animation = ValueAnimator.ofFloat(nowProgress, isEnter ? 1f : 0f);
            animation.addUpdateListener(this);
            animation.addListener(mAnimatorListenerAdapter);
            mAnimator = animation;
        } else {
            mAnimator.cancel();
            mAnimator.setFloatValues(nowProgress, isEnter ? 1f : 0f);
        }

        Interpolator interpolator;
        if (isEnter)
            interpolator = new DecelerateInterpolator();
        else
            interpolator = new AccelerateInterpolator();
        mAnimator.setInterpolator(interpolator);
        mAnimator.setDuration((long) (time * (isEnter ? 1 - nowProgress : nowProgress)));

        return mAnimator;
    }

    public void start(Runnable runnable) {
        if (!IS_UP_KITKAT) {
            runnable.run();
            return;
        }

        if (mPath == null) {
            mPath = new Path();
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        getAnimator(true, 480, runnable).start();
    }

    public void exit(Runnable runnable) {
        if (!IS_UP_KITKAT) {
            runnable.run();
            return;
        }

        if (mPath == null) {
            mPath = new Path();
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        getAnimator(false, 380, runnable).start();
    }

    @Override
    protected final boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            insets.left = 0;
            insets.top = 0;
            insets.right = 0;
        }
        return super.fitSystemWindows(insets);
    }

    /*
    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            insets = insets.replaceSystemWindowInsets(0, 0, 0,
                    insets.getSystemWindowInsetBottom());
        }
        return insets;
    }
    */

    /**
     * If in animation or finish state, we InterceptTouchEvent
     *
     * @param ev ev
     * @return can do
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !mInAnim && super.dispatchTouchEvent(ev);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mProgress = (float) animation.getAnimatedValue();
        doUpdate(mProgress);
    }

    private Runnable mDoRunnable;

    private AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {
        private boolean isCancel;

        @Override
        public void onAnimationCancel(Animator animation) {
            isCancel = true;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mInAnim = true;
            isCancel = false;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mInAnim = false;
            Runnable runnable = mDoRunnable;
            if (!isCancel && runnable != null) {
                runnable.run();
                mDoRunnable = null;
            }
        }
    };
}
