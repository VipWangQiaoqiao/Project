package net.oschina.app.improve.notice;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.OSCApplication;
import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.main.MainActivity;
import net.oschina.app.improve.utils.ListenAccountChangeReceiver;
import net.oschina.app.util.TLog;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by JuQiu
 * on 16/8/18.
 */
public class NoticeServer extends Service {
    private static final String TAG = NoticeServer.class.getName();

    static final String FLAG_BROADCAST_REFRESH = TAG + "_BROADCAST_REFRESH";
    static final String FLAG_BROADCAST_REQUEST = TAG + "_BROADCAST_REQUEST";

    private static final String FLAG_ACTION_FIRST = TAG + "_FIRST";
    private static final String FLAG_ACTION_REFRESH = TAG + "_REFRESH";
    private static final String FLAG_ACTION_CLEAR = TAG + "_CLEAR";
    private static final String FLAG_ACTION_EXIT = TAG + "_EXIT";
    private static final String FLAG_ACTION_ARRIVED = TAG + "_ARRIVED";

    private static final String EXTRA_TYPE = "type";
    static final String EXTRA_BEAN = "bean";

    private AlarmManager mAlarmMgr;
    private ListenAccountChangeReceiver mListenAccountChangeReceiver;

    static void startAction(Context context) {
        Intent intent = new Intent(context, NoticeServer.class);
        intent.setAction(FLAG_ACTION_FIRST);
        context.startService(intent);
        log("startAction");
    }

    static void clearAction(Context context, int type) {
        Intent intent = new Intent(context, NoticeServer.class);
        intent.setAction(FLAG_ACTION_CLEAR);
        intent.putExtra(EXTRA_TYPE, type);
        context.startService(intent);
        log("clearAction");
    }

    static void exitAction(Context context) {
        Intent intent = new Intent(context, NoticeServer.class);
        intent.setAction(FLAG_ACTION_EXIT);
        context.startService(intent);
        log("exitAction");
    }

    static void arrivedMsgAction(Context context, NoticeBean bean) {
        int serviceUid = android.os.Process.getUidForName("net.oschina.app.notice.NoticeServer");
        int mUid = android.os.Process.myUid();

        log("arrivedMsgAction: serviceUid:" + serviceUid + " mUid:" + mUid);

        if (mUid == serviceUid)
            return;

        Intent intent = new Intent(context, NoticeServer.class);
        intent.setAction(FLAG_ACTION_ARRIVED);
        intent.putExtra(EXTRA_BEAN, bean);
        context.startService(intent);
        log("arrivedMsgAction");
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
        // First init the Client
        Application application = getApplication();
        if (application instanceof OSCApplication) {
            OSCApplication.reInit();
        }

        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        mListenAccountChangeReceiver = ListenAccountChangeReceiver.start(this);
    }

    @Override
    public void onDestroy() {
        mListenAccountChangeReceiver.destroy();
        log("onDestroy");
        super.onDestroy();
    }

    private final static int ALARM_INTERVAL_SECOND = 100000;

    private void registerNextAlarm() {
        cancelRequestAlarm();
        mAlarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + ALARM_INTERVAL_SECOND, ALARM_INTERVAL_SECOND, getOperationIntent());
        log("registerAlarmByInterval interval:" + ALARM_INTERVAL_SECOND);
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
            refreshNoticeForNet();
        } else if (FLAG_ACTION_CLEAR.equals(action)) {
            clearNoticeForNet(intent.getIntExtra(EXTRA_TYPE, 0));
        } else if (FLAG_ACTION_EXIT.equals(action)) {
            cancelRequestAlarm();
            stopSelf();
        } else if (FLAG_ACTION_FIRST.equals(action)) {
            registerNextAlarm();
            // First notify
            sendBroadcastToManager(NoticeBean.getInstance(this));
        } else if (FLAG_ACTION_ARRIVED.equals(action)) {
            // Arrived new message
            NoticeBean bean = (NoticeBean) intent.getSerializableExtra(EXTRA_BEAN);
            if (bean == null)
                return;
            doNewMessage(bean);
        }
    }

    private void doNetFinish(ResultBean bean) {
        log("doNetFinish:" + (bean == null ? "null" : bean.toString()));
        if (bean != null && bean.isOk()
                && bean.getNotice() != null) {
            doNewMessage(bean.getNotice());
        } else {
            // To register alarm
            registerNextAlarm();
        }
    }

    private void doNewMessage(@NonNull NoticeBean bean) {
        log("doNewMessage:" + (bean.toString()));
        NoticeBean notice = NoticeBean.getInstance(this);
        if (bean.equals(notice))
            return;
        notice.set(bean).save(this);
        // Send to manager
        sendBroadcastToManager(notice);
        // Send to notification
        sendNotification(notice);
        // To register alarm
        registerNextAlarm();
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


    private boolean mRunning;

    private synchronized void refreshNoticeForNet() {
        log("refreshNoticeForNet: mRunning:" + mRunning);

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
                        Type type = new TypeToken<ResultBean>() {
                        }.getType();
                        ResultBean bean = new Gson().fromJson(responseString, type);
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

    private synchronized void clearNoticeForNet(int flag) {
        log("clearNoticeForNet: flag:" + flag);

        if (flag == 0 || NoticeBean.getInstance(this).getAllCount() <= 0)
            return;

        if ((flag & NoticeManager.FLAG_CLEAR_MENTION) == NoticeManager.FLAG_CLEAR_MENTION
                || (flag & NoticeManager.FLAG_CLEAR_LETTER) == NoticeManager.FLAG_CLEAR_LETTER
                || (flag & NoticeManager.FLAG_CLEAR_REVIEW) == NoticeManager.FLAG_CLEAR_REVIEW
                || (flag & NoticeManager.FLAG_CLEAR_FANS) == NoticeManager.FLAG_CLEAR_FANS
                || (flag & NoticeManager.FLAG_CLEAR_LIKE) == NoticeManager.FLAG_CLEAR_LIKE) {
            mRunning = true;
            OSChinaApi.clearNotice(flag, new TextHttpResponseHandler() {
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
                            Type type = new TypeToken<ResultBean>() {
                            }.getType();
                            ResultBean bean = new Gson().fromJson(responseString, type);
                            doNetFinish(bean);
                        } catch (Exception e) {
                            e.printStackTrace();
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
    }

    static void log(String str) {
        TLog.d(TAG, str);
    }
}
