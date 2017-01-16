package net.oschina.app.improve.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import net.oschina.app.improve.media.Util;

/**
 * 判断View是否在屏幕范围内
 * Created by haibin
 * on 2017/1/13.
 */

public class ScreenView extends View {
    public ScreenView(Context context) {
        super(context);
    }

    public ScreenView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isViewInScreen() {

        int screenWidth = Util.getScreenWidth(getContext());

        int screenHeight = Util.getScreenHeight(getContext());

        Rect rect = new Rect(0, 0, screenWidth, screenHeight);

        int[] location = new int[2];

        getLocationInWindow(location);

        return getLocalVisibleRect(rect);
    }
}
