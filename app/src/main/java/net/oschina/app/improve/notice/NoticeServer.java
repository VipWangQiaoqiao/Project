package net.oschina.app.improve.notice;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.BuildConfig;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.main.MainActivity;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JuQiu
 * on 16/8/18.
 */
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
        log("startAction");
    }

    static void startActionClear(Context context, int type) {
        Intent intent = new Intent(context, NoticeServer.class);
        intent.setAction(FLAG_ACTION_CLEAR);
        intent.putExtra(EXTRA_TYPE, type);
        context.startService(intent);
        log("startActionClear");
    }

    static void startActionExit(Context context) {
        Intent intent = new Intent(context, NoticeServer.class);
        intent.setAction(FLAG_ACTION_EXIT);
        context.startService(intent);
        log("startActionExit");
    }

    public NoticeServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            log("onStartCommand: intent is null.");
            return super.onStartCommand(null, flags, startId);
        }
        String action = intent.getAction();
        log("onStartCommand:" + action);
        if (action != null) {
            handleAction(action, intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("onCreate");
        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        super.onDestroy();
    }

    private final static int ALARM_INTERVAL_MIN = 0;
    private final static int ALARM_INTERVAL_MAX = 15;

    private final static int ALARM_INTERVAL_MIN_SECOND = 20;
    private final static int ALARM_INTERVAL_MAX_SECOND = 15 * 60;
    // The count 0~15, 20s->15*60s
    private static int mAlarmCount = ALARM_INTERVAL_MIN;

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
        log("registerCurrentAlarm:increase:" + increase);
        long interval = getAlarmInterval(mAlarmCount);
        registerAlarmByInterval(interval);
    }

    private void registerAlarmByInterval(long interval) {
        cancelRequestAlarm();
        mAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + interval, interval, getOperationIntent());
        log("registerAlarmByInterval interval:" + interval);
    }


    private long getAlarmInterval(int count) {
        mAlarmCount = count;

        float progress = mAlarmCount / (float) ALARM_INTERVAL_MAX;
        progress = new AccelerateInterpolator(2.2f).getInterpolation(progress);

        int millisecond = (int) (1000 * (ALARM_INTERVAL_MIN_SECOND + ((ALARM_INTERVAL_MAX_SECOND - ALARM_INTERVAL_MIN_SECOND) * progress)));
        log("getAlarmInterval:mAlarmCount:" + mAlarmCount + " progress:" + progress + " millisecond:" + millisecond);
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
            cancelRequestAlarm();
            stopSelf();
        } else if (FLAG_ACTION_FIRST.equals(action)) {
            registerFirstAlarm();
            // First notify
            sendBroadcastToManager(NoticeBean.getInstance(this));
        }
    }

    private boolean mRunning;

    private synchronized void refreshNotice() {
        log("refreshNotice: mRunning:" + mRunning);
        if (mRunning)
            return;

        mRunning = true;
        OSChinaApi.getNotice(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                log("onFailure:" + statusCode + " " + responseString);
                doNetFinish(null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                log("onSuccess:" + statusCode + " " + responseString);
                if (!TextUtils.isEmpty(responseString)) {
                    try {
                        Type type = new TypeToken<ResultBean<NoticeBean>>() {
                        }.getType();
                        ResultBean<NoticeBean> bean = new Gson().fromJson(responseString, type);
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

    private void doNetFinish(ResultBean<NoticeBean> bean) {
        log("doNetFinish:" + (bean == null ? "null" : bean.toString()));
        if (bean != null && bean.isSuccess()
                && bean.getResult() != null
                && bean.getResult().getAllCount() > 0) {
            NoticeBean request = bean.getResult();
            NoticeBean notice = NoticeBean.getInstance(this)
                    .add(request)
                    .save(this);
            // To register alarm
            registerCurrentAlarm(false);
            // Send to manager
            sendBroadcastToManager(notice);
            // Send to notification
            sendNotification(notice);
        } else {
            // To register alarm
            registerCurrentAlarm(true);
        }
    }

    private void clearNotice(int type) {
        log("clearNotice:" + type);
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
            case NoticeManager.FLAG_CLEAR_ALL:
                notice.clear();
                break;
        }
        notice.save(this);
        sendBroadcastToManager(notice);
        sendNotification(notice);
    }

    private void sendBroadcastToManager(NoticeBean bean) {
        log("sendBroadcastToManager:" + bean.toString());
        Intent intent = new Intent(FLAG_BROADCAST_REFRESH);
        intent.putExtra(EXTRA_BEAN, bean);
        sendBroadcast(intent);
    }

    private final static int NOTIFY_ID = 0x11111111;

    @SuppressLint("StringFormatMatches")
    private void sendNotification(NoticeBean bean) {
        if (bean == null || bean.getAllCount() == 0) {
            clearNotification();
            return;
        }

        log("sendNotification:" + bean.toString());

        StringBuilder sb = new StringBuilder();
        if (bean.getMention() > 0) {
            sb.append(getString(R.string.mention_count, bean.getMention())).append(" ");
        }
        if (bean.getLetter() > 0) {
            sb.append(getString(R.string.letter_count, bean.getLetter())).append(" ");
        }
        if (bean.getReview() > 0) {
            sb.append(getString(R.string.review_count, bean.getReview()))
                    .append(" ");
        }
        if (bean.getFans() > 0) {
            sb.append(getString(R.string.fans_count, bean.getFans()));
        }
        if (bean.getLike() > 0) {
            sb.append(getString(R.string.like_count, bean.getLike()));
        }
        String content = sb.toString();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.ACTION_NOTICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFY_ID, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this)
                .setTicker(content)
                .setContentTitle(getString(R.string.you_have_unread_messages, bean.getAllCount()))
                .setContentText(content)
                .setAutoCancel(true)
                .setOngoing(false)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_notification);

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(NOTIFY_ID, notification);
    }

    private void clearNotification() {
        log("clearNotification");
        NotificationManagerCompat.from(this).cancel(NOTIFY_ID);
    }

    static void log(String str) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, str);
    }
}
