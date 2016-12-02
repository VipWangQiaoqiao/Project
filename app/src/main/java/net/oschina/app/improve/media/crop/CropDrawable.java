package net.oschina.app.improve.media.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import net.oschina.app.improve.media.Util;

/**
 * Created by haibin
 * on 2016/12/1.
 */
@SuppressWarnings("ALL")
public class CropDrawable extends Drawable {
    private Context mContext;
    private int offset = 50;

    private Paint mCirclePaint = new Paint();//圆角
    private Paint mLinePaint = new Paint();//正方形框

    private int mCropWidth = 800, mCropHeight = 800;

    private static final int RADIUS = 20;

    private int mLeft, mRight, mTop, mBottom;

    public CropDrawable(Context mContext) {
        this.mContext = mContext;
        initPaint();
    }

    private void initPaint() {
        mLinePaint.setARGB(200, 50, 50, 50);
        mLinePaint.setStrokeWidth(2f);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.WHITE);

        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.FILL);//实心圆
        mCirclePaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        int width = Util.getScreenWidth(mContext);
        int height = Util.getScreenHeight(mContext);
        mLeft = (width - mCropWidth) / 2;
        mTop = (height - mCropHeight) / 2;
        mRight = (width + mCropWidth) / 2;
        mBottom = (height + mCropHeight) / 2;
        Rect rect = new Rect(mLeft, mTop, mRight, mBottom);
        canvas.drawRect(rect, mLinePaint);
        canvas.drawCircle(rect.left, rect.top + mCropHeight, RADIUS, mCirclePaint);
        canvas.drawCircle(rect.right, rect.top + mCropHeight, RADIUS, mCirclePaint);
        canvas.drawCircle(rect.left, rect.bottom - mCropHeight, RADIUS, mCirclePaint);
        canvas.drawCircle(rect.right, rect.bottom - mCropHeight, RADIUS, mCirclePaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    public void offset(int x, int y) {
        getBounds().offset(x, y);
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(new Rect(mLeft, mTop, mRight, mBottom));
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public void setRegion(Rect rect) {
        int width = Util.getScreenWidth(mContext);
        int height = Util.getScreenHeight(mContext);
        rect.set((width - mCropWidth) / 2, (height - mCropHeight) / 2, (width + mCropWidth) / 2, (height + mCropHeight) / 2);
    }

    public int getLeft() {
        return mLeft;
    }


    public int getRight() {
        return mRight;
    }


    public int getTop() {
        return mCropHeight;
    }


    public int getBottom() {
        return mBottom;
    }

    public void setCropWidth(int mCropWidth) {
        this.mCropWidth = mCropWidth;
    }

    public void setCropHeight(int mCropHeight) {
        this.mCropHeight = mCropHeight;
    }
}
