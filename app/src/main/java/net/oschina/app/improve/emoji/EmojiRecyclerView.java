package net.oschina.app.improve.emoji;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.ViewParent;

import net.oschina.app.improve.face.FaceRecyclerView;

/**
 * Created by haibin
 * on 2017/1/20.
 */

class EmojiRecyclerView extends FaceRecyclerView {

    EmojiRecyclerView(Context context, FaceRecyclerView.OnFaceClickListener listener) {
        super(context, listener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ViewParent parent = this;
        while (!((parent = parent.getParent()) instanceof ViewPager)) ;
        parent.requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
