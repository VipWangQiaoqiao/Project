package net.oschina.app.improve.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 类太阳系星球转动
 * Created by thanatosx on 16/7/14.
 */
public class SolarSystemView extends View implements Runnable {

    private int paintCount;
    private float pivotX;
    private float pivotY;
    private Paint mTrackPaint;
    private Paint mPlanetPaint;
    private Paint mBackgroundPaint;
    private List<Planet> planets;
    private Bitmap mCacheBitmap;

    public SolarSystemView(Context context) {
        super(context);
    }

    public SolarSystemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SolarSystemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        planets = new ArrayList<>();

        mTrackPaint = new Paint();
        mTrackPaint.setStyle(Paint.Style.STROKE);
        mTrackPaint.setAntiAlias(true);

        mPlanetPaint = new Paint();
        mPlanetPaint.setStyle(Paint.Style.FILL);
        mPlanetPaint.setAntiAlias(true);
    }

    public void setPivotPoint(float x, float y) {
        pivotX = x;
        pivotY = y;
        paintCount = 0;
        prepare();
    }

    public void addPlanets(List<Planet> planets) {
        this.planets.addAll(planets);
    }

    public void addPlanets(Planet planet) {
        planets.add(planet);
    }

    public void clear(){
        planets.clear();
    }

    public synchronized void prepare(){
        if (planets.size() == 0) return;

        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
        mCacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mCacheBitmap);
        if (getBackground() != null){
            getBackground().draw(canvas);
        }
        if (mBackgroundPaint != null && mBackgroundPaint.getShader() != null){
            canvas.drawRect(0, 0, getWidth(), getHeight(), mBackgroundPaint);
        }
        for (Planet planet : planets) {
            mTrackPaint.setStrokeWidth(planet.getTrackWidth());
            mTrackPaint.setColor(planet.getTrackColor());
            canvas.drawCircle(pivotX, pivotY, planet.getRadius(), mTrackPaint);
        }
        postRepaint();
    }

    /**
     * 设置背景渐变
     * 设置中心点之后再做此事
     * @param x pivot x
     * @param y pivot y
     * @param r radius of gradient
     * @param sc start color
     * @param ec end color
     */
    public void setRadialGradient(float x, float y, float r, int sc, int ec){
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setShader(new RadialGradient(x, y, r, sc, ec, Shader.TileMode.CLAMP));
        prepare();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        prepare();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (planets.size() == 0) return;

        int count = canvas.save();
        if (mCacheBitmap != null) canvas.drawBitmap(mCacheBitmap, 0, 0, mPlanetPaint);
        for (Planet planet : planets) {
            double y;
            double x;
            float angle;
            if (planet.isClockwise()) {
                angle = (planet.getOriginAngle() + paintCount * planet.getAngleRate()) % 360;
            } else {
                angle = 360 - (planet.getOriginAngle() + paintCount * planet.getAngleRate()) % 360;
            }
            x = Math.cos(angle) * planet.getRadius() + pivotX;
            y = Math.sin(angle) * planet.getRadius() + pivotY;
            mPlanetPaint.setColor(planet.getColor());
            canvas.drawCircle((float) x, (float) y, planet.getSelfRadius(), mPlanetPaint);
        }
        canvas.restoreToCount(count);
        ++paintCount;
        if (paintCount < 0) paintCount = 0;
    }

    private void postRepaint(){
        removeCallbacks(this);
        postDelayed(this, 33);
    }

    @Override
    public void run() {
        invalidate();
        postRepaint();
    }

    public static class Planet {
        private int mRadius = 100;
        private int mSelfRadius = 6;
        private int mTrackWidth = 1;
        private int mColor = 0XFF6FDB94;
        private int mTrackColor = 0XFF6FDB94;
        private float mAngleRate = 0.01F;
        private int mOriginAngle = 0;
        private boolean isClockwise = true;

        public int getRadius() {
            return mRadius;
        }

        public void setRadius(int mRadius) {
            this.mRadius = mRadius;
        }

        public int getSelfRadius() {
            return mSelfRadius;
        }

        public void setSelfRadius(int mSelfRadius) {
            this.mSelfRadius = mSelfRadius;
        }

        public int getTrackWidth() {
            return mTrackWidth;
        }

        public void setTrackWidth(int mTrackWidth) {
            this.mTrackWidth = mTrackWidth;
        }

        public int getColor() {
            return mColor;
        }

        public void setColor(int mColor) {
            this.mColor = mColor;
        }

        public int getTrackColor() {
            return mTrackColor;
        }

        public void setTrackColor(int mTrackColor) {
            this.mTrackColor = mTrackColor;
        }

        public float getAngleRate() {
            return mAngleRate;
        }

        public void setAngleRate(float mAngleRate) {
            this.mAngleRate = mAngleRate;
        }

        public boolean isClockwise() {
            return isClockwise;
        }

        public void setClockwise(boolean clockwise) {
            isClockwise = clockwise;
        }

        public int getOriginAngle() {
            return mOriginAngle;
        }

        public void setOriginAngle(int mOriginAngle) {
            this.mOriginAngle = mOriginAngle;
        }
    }
}
