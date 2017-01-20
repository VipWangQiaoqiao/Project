package net.oschina.app.improve.emoji;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by haibin
 * on 2017/1/20.
 */

public class EmojiViewPager extends ViewPager {
    public EmojiViewPager(Context context) {
        super(context);
    }

    public EmojiViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(ev);
    }
}
