package net.oschina.app.improve.main.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import net.oschina.app.R;


/**
 * Created by haibin
 * on 2016/11/24.
 */
@SuppressWarnings("unused")
public class CircleBannerIndicator extends View implements BannerIndicator {
    private float mRadius;
    private float mIndicatorRadius;
    private final Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mCurrentPage;
    private float mPageOffset;
    private boolean mCenterHorizontal;

    private float mIndicatorSpace;

    private BannerView.OnBannerChangeListener mOnViewChangeListener;
    private BannerView mBannerView;

    public CircleBannerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleBannerIndicator);

        mCenterHorizontal = a.getBoolean(R.styleable.CircleBannerIndicator_circle_banner_indicator_centerHorizontal, true);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(a.getColor(R.styleable.CircleBannerIndicator_circle_banner_indicator_color, 0x0000ff));
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(a.getColor(R.styleable.CircleBannerIndicator_circle_banner_indicator_stroke_color, 0x000000));
        mPaintStroke.setStrokeWidth(a.getDimension(R.styleable.CircleBannerIndicator_circle_banner_indicator_stroke_width, 0));
        mPaintIndicator.setStyle(Paint.Style.FILL);
        mPaintIndicator.setColor(a.getColor(R.styleable.CircleBannerIndicator_circle_banner_indicator_fill_color, 0x0000ff));
        mRadius = a.getDimension(R.styleable.CircleBannerIndicator_circle_banner_indicator_radius, 10);
        mIndicatorSpace = a.getDimension(R.styleable.CircleBannerIndicator_circle_banner_indicator_space, 20);
        mIndicatorRadius = a.getDimension(R.styleable.CircleBannerIndicator_circle_banner_indicator_indicator_radius, 10);
        if (mIndicatorRadius < mRadius) mIndicatorRadius = mRadius;
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBannerView == null) {
            return;
        }
        final int count = mBannerView.getAdapter().getCount();

        if (count <= 1) {
            return;
        }

        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }

        int width = getWidth();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();

        final float circleAndSpace = 2 * mRadius + mIndicatorSpace;//直径+圆的间隔
        final float yOffset = getHeight() / 2;//竖直方向圆心偏移量，剧中对齐
        float xOffset = paddingLeft + mRadius;//水平方向圆心偏移量

        //如果采用水平居中对齐
        if (mCenterHorizontal) {
            xOffset = (width - count * 2 * mRadius - (count - 1) * mIndicatorSpace) / 2 - mRadius;
        }

        float cX;
        float cY;

        float strokeRadius = mRadius;
        //如果绘制外圆
        if (mPaintStroke.getStrokeWidth() > 0) {
            strokeRadius -= mPaintStroke.getStrokeWidth() / 2.0f;
        }

        //绘制所有圆点
        for (int i = 0; i < count; i++) {

            cX = xOffset + (i * circleAndSpace);//计算下个圆绘制起点偏移量
            cY = yOffset;

            //绘制圆
            if (mPaintFill.getAlpha() > 0) {
                canvas.drawCircle(cX, cY, strokeRadius, mPaintFill);
            }

            //绘制外圆
            if (strokeRadius != mRadius) {
                canvas.drawCircle(cX, cY, mRadius, mPaintStroke);
            }
        }

        float cx = mCurrentPage * circleAndSpace;

        cX = xOffset + cx;
        cY = yOffset;
        canvas.drawCircle(cX, cY, mIndicatorRadius, mPaintIndicator);
    }

    @Override
    public void bindBannerView(BannerView view) {
        if (view == null)
            return;
        if (view.getAdapter() == null) {
            throw new IllegalStateException("BannerView does not set adapter");
        }
        this.mBannerView = view;
        this.mBannerView.addOnBannerChangeListener(this);
        invalidate();
    }

    @Override
    public void setCurrentItem(int currentItem) {
        if (mBannerView == null) {
            throw new IllegalStateException("indicator has not bind BannerView");
        }
        //mBannerView.setCurrentItem(currentItem);
        mCurrentPage = currentItem;
        invalidate();
    }

    @Override
    public void setOnViewChangeListener(BannerView.OnBannerChangeListener listener) {

    }

    @Override
    public void notifyDataSetChange() {
        invalidate();
        requestLayout();
    }

    @Override
    public void onViewScrolled(int position, float positionOffset) {

    }

    @Override
    public void onViewSelected(int position) {
        mCurrentPage = position;
        invalidate();
    }

    @Override
    public void onViewStateChanged(int state) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int width;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (mBannerView == null)) {
            width = specSize;
        } else {
            final int count = mBannerView.getAdapter().getCount();
            width = (int) (getPaddingLeft() + getPaddingRight()
                    + (count * 2 * mRadius) + (mIndicatorRadius - mRadius) * 2 + (count - 1) * mIndicatorSpace);
            if (specMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, specSize);
            }
        }
        return width;
    }

    private int measureHeight(int measureSpec) {
        int height;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            if (specMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, specSize);
            }
        }
        return height;
    }
}
