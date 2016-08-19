package net.oschina.app.improve.notice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import net.oschina.app.improve.tweet.service.TweetPublishService;

public class NoticeService extends Service {
    private static final String TAG = NoticeService.class.getName();
    private static final String FLAG_ACTION_REFRESH = TAG + "_REFRESH";
    private static final String FLAG_ACTION_CLEAR = TAG + "_CLEAR";
    private static final String FLAG_ACTION_EXIT = TAG + "_EXIT";

    private static final String EXTRA_TYPE = "type";

    private static final long INTERVAL = 1000 * 20;
    private AlarmManager mAlarmMgr;

    static void startAction(Context context) {
        Intent intent = new Intent(context, NoticeService.class);
        context.startService(intent);
    }

    static void startActionClear(Context context, int type) {
        Intent intent = new Intent(context, NoticeService.class);
        intent.setAction(FLAG_ACTION_CLEAR);
        intent.putExtra(EXTRA_TYPE, type);
        context.startService(intent);
    }

    static void startActionExit(Context context) {
        Intent intent = new Intent(context, NoticeService.class);
        intent.setAction(FLAG_ACTION_EXIT);
        context.startService(intent);
    }

    public NoticeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("NoticeService", "onStartCommand:" + intent.getAction());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("NoticeService", "onCreate");
        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        startRequestAlarm();
    }

    @Override
    public void onDestroy() {
        cancelRequestAlarm();
        super.onDestroy();
    }

    private void startRequestAlarm() {
        cancelRequestAlarm();
        mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 1000, INTERVAL,
                getOperationIntent());
    }

    private void cancelRequestAlarm() {
        mAlarmMgr.cancel(getOperationIntent());
    }

    private PendingIntent getOperationIntent() {
        Intent intent = new Intent(this, NoticeService.class);
        intent.setAction(FLAG_ACTION_REFRESH);
        return PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 请求是否有新通知
     */
    private void requestNotice() {
    }

    private void clearNotice(int type) {
    }
}
