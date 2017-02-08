package net.oschina.app.improve.utils;

import android.content.Context;
import android.content.res.Resources;

import net.oschina.app.util.TDevice;
import net.oschina.app.util.TLog;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */

public class StatusBarHeightUtil {
    private static int STATUS_BAR_HEIGHT = 0;
    private final static String STATUS_BAR_DEF_PACKAGE = "android";
    private final static String STATUS_BAR_DEF_TYPE = "dimen";
    private final static String STATUS_BAR_NAME = "status_bar_height";
    private final static String STATUS_CLASS_NAME = "com.android.internal.R$dimen";
    private final static String STATUS_CLASS_FIELD = "status_bar_height";


    public static synchronized int getStatusBarHeight(final Context context) {
        if (STATUS_BAR_HEIGHT > 0)
            return STATUS_BAR_HEIGHT;

        Resources resources = context.getResources();
        int resourceId = context.getResources().
                getIdentifier(STATUS_BAR_NAME, STATUS_BAR_DEF_TYPE, STATUS_BAR_DEF_PACKAGE);
        if (resourceId > 0) {
            STATUS_BAR_HEIGHT = context.getResources().getDimensionPixelSize(resourceId);
            TLog.d("StatusBarHeightUtil",
                    String.format("Get status bar height %s", STATUS_BAR_HEIGHT));
        } else {
            try {
                Class<?> clazz = Class.forName(STATUS_CLASS_NAME);
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField(STATUS_CLASS_FIELD)
                        .get(object).toString());
                STATUS_BAR_HEIGHT = resources.getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
                return (int) TDevice.dp2px(25);
            }
        }
        return STATUS_BAR_HEIGHT;
    }
}
