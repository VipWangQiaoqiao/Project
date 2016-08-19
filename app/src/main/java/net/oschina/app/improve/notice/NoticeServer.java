package net.oschina.app.improve.notice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.api.remote.OSChinaApi;

import cz.msebera.android.httpclient.Header;

public class NoticeServer extends Service {
    private static final String TAG = NoticeServer.class.getName();

    static final String FLAG_BROADCAST_REFRESH = TAG + "_BROADCAST_REFRESH";

    private static final String FLAG_ACTION_FIRST = TAG + "_FIRST";
    private static final String FLAG_ACTION_REFRESH = TAG + "_REFRESH";
    private static final String FLAG_ACTION_CLEAR = TAG + "_CLEAR";
    private static final String FLAG_ACTION_EXIT = TAG + "_EXIT";

    private static final String EXTRA_TYPE = "type";
    static final String EXTRA_BEAN = "bean";

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
        // First notify
        sendBroadcastToManager(NoticeBean.getInstance(this));
    }

    @Override
    public void onDestroy() {
        cancelRequestAlarm();
        super.onDestroy();
    }

    // The count 0~15, 20s->15*60s
    private int mAlarmCount = ALARM_INTERVAL_MIN;
    private final static int ALARM_INTERVAL_MIN = 0;
    private final static int ALARM_INTERVAL_MAX = 15;

    private final static int ALARM_INTERVAL_MIN_SECOND = 20;
    private final static int ALARM_INTERVAL_MAX_SECOND = 15 * 60;

    private void registerFirstAlarm() {
        long interval = getAlarmInterval(0);
        registerAlarmByInterval(interval);
    }

    private void registerCurrentAlarm(boolean increase) {
        if (increase) {
            mAlarmCount++;
            if (mAlarmCount > ALARM_INTERVAL_MAX)
                mAlarmCount = ALARM_INTERVAL_MAX;
        } else {
            mAlarmCount--;
            if (mAlarmCount < ALARM_INTERVAL_MIN)
                mAlarmCount = ALARM_INTERVAL_MIN;
        }
        Log.e(TAG, "registerCurrentAlarm:increase:" + increase);
        long interval = getAlarmInterval(mAlarmCount);
        registerAlarmByInterval(interval);
    }

    private void registerAlarmByInterval(long interval) {
        cancelRequestAlarm();
        mAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + interval, interval, getOperationIntent());
        Log.e(TAG, "registerAlarmByInterval interval:" + interval);
    }


    private long getAlarmInterval(int count) {
        mAlarmCount = count;

        float progress = mAlarmCount / (float) ALARM_INTERVAL_MAX;
        progress = new AccelerateInterpolator(2.5f).getInterpolation(progress);

        int millisecond = (int) (1000 * (ALARM_INTERVAL_MIN_SECOND + ((ALARM_INTERVAL_MAX_SECOND - ALARM_INTERVAL_MIN_SECOND) * progress)));
        Log.e(TAG, "getAlarmInterval:mAlarmCount:" + mAlarmCount + " progress:" + progress + " millisecond:" + millisecond);
        return millisecond;
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
            registerFirstAlarm();
        }
    }

    private boolean mRunning;

    private synchronized void refreshNotice() {
        Log.e(TAG, "refreshNotice: mRunning:" + mRunning);
        if (mRunning)
            return;

        mRunning = true;
        OSChinaApi.getNotice(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                doNetFinish(null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (!TextUtils.isEmpty(responseString)) {
                    try {
                        NoticeBean bean = new Gson().fromJson(responseString, NoticeBean.class);
                        doNetFinish(bean);
                    } catch (Exception e) {
                        onFailure(statusCode, headers, responseString, e.fillInStackTrace());
                    }
                } else {
                    onFailure(statusCode, headers, responseString, null);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                mRunning = false;
            }
        });
    }

    private void doNetFinish(NoticeBean bean) {
        Log.e(TAG, "doNetFinish:" + (bean == null ? "null" : bean.toString()));

        if (bean != null && bean.getAllCount() > 0) {
            NoticeBean notice = NoticeBean.getInstance(this)
                    .add(bean)
                    .save(this);
            // Send to manager
            sendBroadcastToManager(notice);
            // To register alarm
            registerCurrentAlarm(false);
        } else {
            // To register alarm
            registerCurrentAlarm(true);
        }
    }

    private void clearNotice(int type) {
        Log.e(TAG, "clearNotice:" + type);
        NoticeBean notice = NoticeBean.getInstance(this);
        switch (type) {
            case NoticeManager.FLAG_CLEAR_MENTION:
                notice.setMention(0);
                break;
            case NoticeManager.FLAG_CLEAR_LETTER:
                notice.setLetter(0);
                break;
            case NoticeManager.FLAG_CLEAR_REVIEW:
                notice.setReview(0);
                break;
            case NoticeManager.FLAG_CLEAR_FANS:
                notice.setFans(0);
                break;
            case NoticeManager.FLAG_CLEAR_LIKE:
                notice.setLike(0);
                break;
        }
        notice.save(this);
        sendBroadcastToManager(notice);
    }

    private void sendBroadcastToManager(NoticeBean bean) {
        Intent intent = new Intent(FLAG_BROADCAST_REFRESH);
        intent.putExtra(EXTRA_BEAN, bean);
        sendBroadcast(intent);
        Log.e(TAG, "sendBroadcastToManager:" + bean.toString());
    }

}
