package net.oschina.app.improve.tweet.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import net.qiujuer.genius.ui.Ui;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class ClipView extends FrameLayout {
    private Path mPath = new Path();
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
        if (mProgress <= 0)
            return;
        if (mIsAnim) {
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

        /*
        int w = getWidth();
        int h = getHeight();

        int l = (int) (pointX - radius);
        int t = (int) (pointY - radius);
        int r = (int) (pointX + radius);
        int b = (int) (pointY + radius);

        if (l < 0)
            l = 0;
        if (t < 0)
            t = 0;
        if (r > w)
            r = w;
        if (b > h)
            b = h;

        //invalidate(l, t, r, b);
        */
        invalidate();

    }

    public void start(float startX, float startY) {
        if (mIsEnter) {
            return;
        }
        mIsEnter = true;
        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setDuration(520);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                doUpdate(mProgress);
            }

        });
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnim = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsAnim = true;
            }
        });
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public void exit(final Runnable runnable) {
        if (!mIsEnter) {
            return;
        }
        mIsEnter = false;
        ValueAnimator animation = ValueAnimator.ofFloat(1f, 0f);
        animation.setDuration(420);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                doUpdate(mProgress);
            }

        });
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnim = false;
                runnable.run();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsAnim = true;
            }
        });
        animation.setInterpolator(new AccelerateInterpolator());
        animation.start();
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

    @Override
    public final WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            insets = insets.replaceSystemWindowInsets(0, 0, 0,
                    insets.getSystemWindowInsetBottom());
        }
        return insets;
    }
}
