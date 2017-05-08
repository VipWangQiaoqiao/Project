package net.oschina.app;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import net.oschina.app.improve.main.ErrorActivity;

/**
 * Created by JuQiu
 * on 2016/9/13.
 */

public class AppCrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static AppCrashHandler INSTANCE = new AppCrashHandler();
    private Context mContext;

    private AppCrashHandler() {
    }

    public static AppCrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;

        Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (exceptionHandler == this)
            return;
        mDefaultHandler = exceptionHandler;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ErrorActivity.show(mContext, ex.getMessage());
        if (mDefaultHandler != null && (BuildConfig.DEBUG || (!handleException(ex)))) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        ex.printStackTrace();


        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "OSC异常；正准备重启！！", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        AppContext.getInstance().clearAppCache();


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return true;
    }
}
