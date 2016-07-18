package net.oschina.app.improve.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 类太阳系星球转动
 * Created by thanatos on 16/7/14.
 */
public class SolarSystemView extends ImageView{

    private int paintCount;
    private float pivotX;
    private float pivotY;
    private Paint mTrackPaint;
    private Paint mPlanetPaint;
    private List<Planet> planets;


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

    public void setPivotPoint(float x, float y){
        this.pivotX = x;
        this.pivotY = y;
        paintCount = 0;
        invalidate();
    }

    public void addPlanets(List<Planet> planets){
        this.planets.addAll(planets);
    }

    public void addPlanets(Planet planet){
        this.planets.add(planet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (planets.size() == 0) return;
        if (canvas.getWidth() <=0 || canvas.getHeight() <= 0) return;
        int count = canvas.save();
        for (Planet planet : planets){
            mTrackPaint.setStrokeWidth(planet.getTrackWidth());
            mTrackPaint.setColor(planet.getTrackColor());
            mPlanetPaint.setColor(planet.getColor());
            canvas.drawCircle(pivotX, pivotY, planet.getRadius(), mTrackPaint);
            double y;
            double x;
            float angle;
            if (planet.isClockwise()){
                angle = (planet.getOriginAngle() + paintCount * planet.getAngleRate()) % 360;
            }else {
                angle = 360 - (planet.getOriginAngle() + paintCount * planet.getAngleRate()) % 360;
            }
            x = Math.cos(angle) * planet.getRadius() + pivotX;
            y = Math.sin(angle) * planet.getRadius() + pivotY;
            canvas.drawCircle((int) x, (int) y, planet.getSelfRadius(), mPlanetPaint);
        }
        canvas.restoreToCount(count);
        ++paintCount;
        postInvalidateDelayed(33);
    }

    public static class Planet{
        private int mRadius = 100;
        private int mSelfRadius = 6;
        private int mTrackWidth = 2;
        private int mColor = 0XFF24E28E;
        private int mTrackColor = 0XFF24E28E;
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
