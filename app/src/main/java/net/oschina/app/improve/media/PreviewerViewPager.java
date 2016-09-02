package net.oschina.app.improve.media;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by thanatos on 16/8/5.
 */
public class PreviewerViewPager extends ViewPager {

    private boolean isInterceptable = false;
    private boolean isTransition = false;

    public PreviewerViewPager(Context context) {
        super(context);
    }

    public PreviewerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            isInterceptable = false;
        }

        boolean b = false;

        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            b = super.onInterceptTouchEvent(ev);
        }

        if (isTransition){
            int action = ev.getAction();
            ev.setAction(MotionEvent.ACTION_DOWN);
            super.onInterceptTouchEvent(ev);
            ev.setAction(action);
            isTransition = false;
        }

        if (ev.getAction() != MotionEvent.ACTION_MOVE || isInterceptable){
            b = super.onInterceptTouchEvent(ev);
        }

        return isInterceptable && b;
    }

    public void isInterceptable(boolean b){
        if (!isInterceptable && b) isTransition = true;
        this.isInterceptable = b;
    }
}
