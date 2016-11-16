package net.oschina.app.improve.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by thanatosx on 2016/11/15.
 */

public class RippleIntroView extends RelativeLayout implements Runnable {

    private int mMaxRadius = 70;
    private int mInterval = 20;
    private int count = 0;

    private Paint mRipplePaint;
    private Paint mCirclePaint;
    private Path mArcPath;

    public RippleIntroView(Context context) {
        this(context, null);
    }

    public RippleIntroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleIntroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mRipplePaint = new Paint();
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setColor(Color.WHITE);
        mRipplePaint.setStrokeWidth(2.f);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(Color.WHITE);

        mArcPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        View child = getChildAt(0);
        View child2 = getChildAt(1);
        if (child == null) return;

        final int width = child.getWidth();
        final int height = child.getHeight();
        if (width == 0 || height == 0) return;
        final float px = child.getX() + width / 2;
        final float py = child.getY() + height / 2;

        final int rw = width / 2;
        final int rh = height / 2;
        double radius = Math.pow(rw * rw + rh * rh, 1 / 2.f);

        int save = canvas.save();
        for (int step = count; step <= mMaxRadius; step += mInterval){
            mRipplePaint.setAlpha(255 * (mMaxRadius - step) / mMaxRadius);
            canvas.drawCircle(px, py, (float) (rw + step), mRipplePaint);
        }
        mArcPath.reset();
        mArcPath.moveTo(px, py + rh + mInterval);
        mArcPath.quadTo(px, child2.getY() - mInterval, (float) (child2.getX() + child2.getWidth() * 0.618), child2.getY() - mInterval);
        mRipplePaint.setAlpha(255);
        canvas.drawPath(mArcPath, mRipplePaint);

        canvas.drawCircle(px, py + rh + mInterval, 6, mCirclePaint);

        canvas.restoreToCount(save);
        postDelayed(this, 80);
    }

    @Override
    public void run() {
        removeCallbacks(this);
        count += 2;
        count %= mInterval;
        invalidate();
    }
}
