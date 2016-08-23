package net.oschina.app.improve.tweet.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import net.qiujuer.genius.ui.Ui;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class ClipView extends FrameLayout implements Runnable {
    private static final float INC = 16f / 1000;
    private Interpolator INTERPOLATOR = new DecelerateInterpolator(2);

    private float mInx = INC;
    private Path mPath = new Path();
    private float mCurProgress;
    private boolean mDone;

    private float mStartRadius, mEndRadius;
    private float mStartPointX, mEndPointX;
    private float mStartPointY, mEndPointY;

    public ClipView(Context context) {
        super(context);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mDone) {
            super.dispatchDraw(canvas);
            return;
        }
        if (mCurProgress > 0) {
            int saveCount = canvas.save();
            canvas.clipPath(mPath);
            super.dispatchDraw(canvas);
            canvas.drawColor(mColor);
            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    public void run() {
        if (mCurProgress > 1 || mCurProgress < 0) {
            mDone = true;
            return;
        }

        mCurProgress = mCurProgress + mInx;

        doDirect(INTERPOLATOR.getInterpolation(mCurProgress));

        // post next
        postDelayed(this, 16);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mStartPointX = w / 2;
        mStartPointY = h;

        mEndPointX = mStartPointX;
        mEndPointY = h / 2;

        mEndRadius = (float) Math.sqrt(mEndPointY * mEndPointY + mEndPointX * mEndPointX);
        mStartRadius = 20;
    }

    private int mColor;
    private void doDirect(float progress) {
        mColor = Ui.changeColorAlpha(0x24cf5f, (int) (200-(200*progress)));

        Path path = mPath;
        path.reset();
        float radius = mStartRadius + (mEndRadius - mStartRadius) * progress;
        float pointX = mStartPointX + (mEndPointX - mStartPointX) * progress;
        float pointY = mStartPointY + (mEndPointY - mStartPointY) * progress;
        path.addCircle(pointX, pointY, radius, Path.Direction.CW);

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

        //mPaint.setAlpha(255 - (int) (255 * progress));

        invalidate(l, t, r, b);
    }

    public void start(float startX, float startY) {
        if (mCurProgress > 1) {
            return;
        }
        mCurProgress = 0;
        mInx = INC;
        mDone = false;

        mPaint.setColor(Color.BLUE);

        post(this);
    }

    public void exit(Runnable runnable) {
        mCurProgress = 1;
        mInx = -INC;
        mDone = false;
        post(this);
        postDelayed(runnable, 420);
    }
}
