package net.oschina.app.improve.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import net.oschina.app.R;

/**
 * Image流布局
 * Created by huanghaibin_dev
 * on 2016/7/18.
 */
public class FlowImageLayout extends LinearLayout {
    private float mHorizontalSpace;
    private float mVerticalSpace;

    public FlowImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowImageLayout);
        mHorizontalSpace = a.getDimension(R.styleable.FlowImageLayout_flow_horizontal_space, 6);
        mVerticalSpace = a.getDimension(R.styleable.FlowImageLayout_flow_vertical_space, 6);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 计算测量宽度
     *
     * @param widthMeasureSpec widthMeasureSpec
     * @return measureWidth
     */
    private int measureWidth(int widthMeasureSpec) {
        return 0;
    }

    /**
     * 计算测量高度
     *
     * @param heightMeasureSpec heightMeasureSpec
     * @return measureHeight
     */
    private int measureHeight(int heightMeasureSpec) {
        return 0;
    }
}
