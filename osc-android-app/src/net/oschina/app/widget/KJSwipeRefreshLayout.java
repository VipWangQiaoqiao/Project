package net.oschina.app.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 基于google的可上下拉控件的改版，重写了触摸屏判断逻辑，方便实现嵌入容器View中
 * 
 * @author kymjs (kymjs123@gmail.com)
 * 
 */
public class KJSwipeRefreshLayout extends SwipeRefreshLayout {
    private float y;

    public KJSwipeRefreshLayout(Context context) {
        super(context);
    }

    public KJSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // switch (event.getAction()) {
        // case MotionEvent.ACTION_DOWN:
        // y = event.getY();
        // break;
        // case MotionEvent.ACTION_MOVE:
        // if (Math.abs(event.getY() - y) < 1) {
        // super.dispatchTouchEvent(event);
        // return true;
        // }
        // break;
        // default:
        // break;
        // }
        return super.dispatchTouchEvent(event);
    }
}