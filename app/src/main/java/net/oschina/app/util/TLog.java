package net.oschina.app.util;

import android.util.Log;

import com.loopj.android.http.BuildConfig;

public class TLog {
    private static final String LOG_TAG = "OSChinaLog";
    public static boolean DEBUG = BuildConfig.DEBUG;

    private TLog() {
    }

    public static void error(String log) {
        if (DEBUG)
            Log.e(LOG_TAG, "" + log);
    }

    public static void log(String log) {
        if (DEBUG)
            Log.i(LOG_TAG, log);
    }

    public static void log(String tag, String log) {
        if (DEBUG)
            Log.i(tag, log);
    }
}
