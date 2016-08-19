package net.oschina.app.improve.notice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.api.remote.OSChinaApi;

import cz.msebera.android.httpclient.Header;

public class NoticeServer extends Service {
    private static final String TAG = NoticeServer.class.getName();
    private static final String FLAG_ACTION_FIRST = TAG + "_FIRST";
    private static final String FLAG_ACTION_REFRESH = TAG + "_REFRESH";
    private static final String FLAG_ACTION_CLEAR = TAG + "_CLEAR";
    private static final String FLAG_ACTION_EXIT = TAG + "_EXIT";

    private static final String EXTRA_TYPE = "type";
    private AlarmManager mAlarmMgr;

    static void startAction(Context context) {
        Intent intent = new Intent(context, NoticeServer.class);
        intent.setAction(FLAG_ACTION_FIRST);
        context.startService(intent);
        Log.e(TAG, "startAction");
    }

    static void startActionClear(Context context, int type) {
        Intent intent = new Intent(context, NoticeServer.class);
        intent.setAction(FLAG_ACTION_CLEAR);
        intent.putExtra(EXTRA_TYPE, type);
        context.startService(intent);
        Log.e(TAG, "startActionClear");
    }

    static void startActionExit(Context context) {
        Intent intent = new Intent(context, NoticeServer.class);
        intent.setAction(FLAG_ACTION_EXIT);
        context.startService(intent);
        Log.e(TAG, "startActionExit");
    }

    public NoticeServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.e(TAG, "onStartCommand:" + action);
        if (action != null) {
            handleAction(action, intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        refreshNextAlarm();
    }

    @Override
    public void onDestroy() {
        cancelRequestAlarm();
        super.onDestroy();
    }

    private void refreshNextAlarm() {
        cancelRequestAlarm();
        long interval = getAlarmInterval();
        mAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + interval, interval, getOperationIntent());
    }

    private int count = 1;

    private long getAlarmInterval() {
        count++;
        return 1000 * 5 * count;
    }

    private void cancelRequestAlarm() {
        mAlarmMgr.cancel(getOperationIntent());
    }

    private PendingIntent getOperationIntent() {
        Intent intent = new Intent(this, NoticeServer.class);
        intent.setAction(FLAG_ACTION_REFRESH);
        return PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void handleAction(String action, Intent intent) {
        if (FLAG_ACTION_REFRESH.equals(action)) {
            refreshNotice();
        } else if (FLAG_ACTION_CLEAR.equals(action)) {
            clearNotice(intent.getIntExtra(EXTRA_TYPE, 0));
        } else if (FLAG_ACTION_EXIT.equals(action)) {
            stopSelf();
        } else if (FLAG_ACTION_FIRST.equals(action)) {
            refreshNotice();
        }
    }

    private boolean mRunning;

    private synchronized void refreshNotice() {
        Log.e(TAG, "refreshNotice:" + mRunning);
        if (mRunning)
            return;

        mRunning = true;
        OSChinaApi.getNotice(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRunning = false;
            }
        });
    }

    private void clearNotice(int type) {
        Log.e(TAG, "clearNotice:" + type);
    }
}
