package net.oschina.app.improve.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by JuQiu
 * on 16/7/15.
 */

public class TweetPublishImageLayout extends FrameLayout {

    public TweetPublishImageLayout(Context context) {
        super(context);
    }

    public TweetPublishImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TweetPublishImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TweetPublishImageLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightSize == 0) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
            return;
        }

        if (widthSize == 0) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
            return;
        }

        if (widthSize > heightSize)
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        else
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        */
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
