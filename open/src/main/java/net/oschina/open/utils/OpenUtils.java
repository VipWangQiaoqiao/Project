package net.oschina.open.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;

/**
 * Created by fei
 * on 2016/10/17.
 * desc:
 */

public class OpenUtils {

    public static Bitmap getShareBitmap(Context context, @IdRes int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System
                .currentTimeMillis();
    }
}
