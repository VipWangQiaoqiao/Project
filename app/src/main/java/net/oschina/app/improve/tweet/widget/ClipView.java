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
import android.widget.FrameLayout;

import net.qiujuer.genius.ui.Ui;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class ClipView extends FrameLayout {
    private static boolean IS_UP_KITKAT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    private Path mPath;
    private boolean mIsAnim;
    private float mStartRadius, mEndRadius;
    private float mStartPointX, mEndPointX;
    private float mStartPointY, mEndPointY;
    private int mColor;
    private float mProgress;
    private boolean mIsEnter = false;

    public ClipView(Context context) {
        super(context);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!IS_UP_KITKAT) {
            super.dispatchDraw(canvas);
            return;
        }
        if (mProgress <= 0)
            return;
        if (mIsAnim && mPath != null) {
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

        mStartPointX = w / 2;
        mStartPointY = h - 40;

        mEndPointX = mStartPointX;
        mEndPointY = h / 2;

        mEndRadius = (float) Math.sqrt(mEndPointY * mEndPointY + mEndPointX * mEndPointX);
        mStartRadius = 20;
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

    private ValueAnimator mEnterAnimator;

    public void start(float startX, float startY, final Runnable runnable) {
        if (!IS_UP_KITKAT || mIsEnter) {
            return;
        }

        if (mPath == null) {
            mPath = new Path();
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mIsEnter = true;
        if (mEnterAnimator == null) {
            ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
            animation.setDuration(480);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (float) animation.getAnimatedValue();
                    doUpdate(mProgress);
                }

            });
            animation.addListener(new AnimatorListenerAdapter() {
                private boolean isCancel;

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mIsAnim = false;
                    if (!isCancel && runnable != null)
                        runnable.run();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mIsAnim = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    isCancel = true;
                }
            });
            animation.setInterpolator(new DecelerateInterpolator());
            mEnterAnimator = animation;
        } else {
            mEnterAnimator.cancel();
            mEnterAnimator.setFloatValues(0f, 1f);
        }
        mEnterAnimator.start();
    }

    private ValueAnimator mExitAnimator;

    public void exit(final Runnable runnable) {
        if (!IS_UP_KITKAT) {
            runnable.run();
            return;
        }

        if (!mIsEnter) {
            runnable.run();
            return;
        }

        if (mPath == null) {
            mPath = new Path();
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mIsEnter = false;

        if (mEnterAnimator != null)
            mEnterAnimator.cancel();

        if (mExitAnimator == null) {
            ValueAnimator animation = ValueAnimator.ofFloat(mProgress, 0f);
            animation.setDuration(360);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (float) animation.getAnimatedValue();
                    doUpdate(mProgress);
                }

            });
            animation.addListener(new AnimatorListenerAdapter() {
                private boolean isCancel;

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mIsAnim = false;
                    if (!isCancel && runnable != null)
                        runnable.run();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mIsAnim = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    isCancel = true;
                }
            });
            animation.setInterpolator(new AccelerateInterpolator());
            mExitAnimator = animation;
        } else {
            mExitAnimator.cancel();
            mExitAnimator.setFloatValues(mProgress, 0);
        }
        mExitAnimator.start();
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
        return mIsEnter && !mIsAnim && super.dispatchTouchEvent(ev);
    }
}
