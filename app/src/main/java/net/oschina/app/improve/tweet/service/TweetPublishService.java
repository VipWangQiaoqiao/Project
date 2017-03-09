package net.oschina.app.improve.tweet.service;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.ArrayMap;

import net.oschina.app.OSCApplication;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.utils.ListenAccountChangeReceiver;
import net.oschina.app.util.TLog;
import net.oschina.common.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 动弹发布服务
 * 专用于动弹发布
 */
public class TweetPublishService extends Service implements Contract.IService {
    private final static String TAG = TweetPublishService.class.getName();

    public static final String ACTION_RECEIVER_SEARCH_INCOMPLETE = "net.oschina.app.improve.tweet.service.action.receiver.SEARCH_INCOMPLETE";
    public static final String ACTION_RECEIVER_SEARCH_FAILED = "net.oschina.app.improve.tweet.service.action.receiver.SEARCH_FAILED";

    public static final String ACTION_PUBLISH = "net.oschina.app.improve.tweet.service.action.PUBLISH";
    public static final String ACTION_CONTINUE = "net.oschina.app.improve.tweet.service.action.CONTINUE";
    public static final String ACTION_DELETE = "net.oschina.app.improve.tweet.service.action.DELETE";
    public static final String ACTION_FAILED = "net.oschina.app.improve.tweet.service.action.FAILED";
    public static final String ACTION_SUCCESS = "net.oschina.app.improve.tweet.service.action.SUCCESS";
    public static final String ACTION_PROGRESS = "net.oschina.app.improve.tweet.service.action.PROGRESS";

    private static final String EXTRA_CONTENT = "net.oschina.app.improve.tweet.service.extra.CONTENT";
    public static final String EXTRA_PROGRESS = "net.oschina.app.improve.tweet.service.extra.CONTENT";
    private static final String EXTRA_IMAGES = "net.oschina.app.improve.tweet.service.extra.IMAGES";
    private static final String EXTRA_ABOUT = "net.oschina.app.improve.tweet.service.extra.ABOUT";
    private static final String EXTRA_ID = "net.oschina.app.improve.tweet.service.extra.ID";
    public static final String EXTRA_IDS = "net.oschina.app.improve.tweet.service.extra.IDS";

    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private Map<String, Contract.IOperator> mRunTasks = new ArrayMap<>();

    private int mTaskCount;
    private volatile boolean mDoAddTask = false;
    private ListenAccountChangeReceiver mReceiver;

    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent) msg.obj, msg.arg1);
        }
    }


    /**
     * 发起动弹发布服务
     * <p>
     * 如果发布的动弹绑定到相关资讯等，则About节点不为NULL
     * 仅仅关注：{@link About.Share#id}, {@link About.Share#type}},
     * {@link About.Share#commitTweetId}, {@link About.Share#fromTweetId}
     */
    public static void startActionPublish(Context context, String content, List<String> images, About.Share aboutShare) {
        Intent intent = new Intent(context, TweetPublishService.class);
        intent.setAction(ACTION_PUBLISH);
        intent.putExtra(EXTRA_CONTENT, content);
        if (images != null && images.size() > 0) {
            String[] pubImages = new String[images.size()];
            images.toArray(pubImages);
            intent.putExtra(EXTRA_IMAGES, pubImages);
        }
        if (About.check(aboutShare)) {
            intent.putExtra(EXTRA_ABOUT, aboutShare);
        }
        context.startService(intent);
    }

    /**
     * 继续发送动弹
     *
     * @param context Context
     * @param modelId {@link TweetPublishModel#id}
     */
    public static void startActionContinue(Context context, String modelId) {
        Intent intent = new Intent(context, TweetPublishService.class);
        intent.setAction(ACTION_CONTINUE);
        intent.putExtra(EXTRA_ID, modelId);
        context.startService(intent);
    }

    /**
     * 删除该动弹
     *
     * @param context Context
     * @param modelId {@link TweetPublishModel#id}
     */
    public static void startActionDelete(Context context, String modelId) {
        Intent intent = new Intent(context, TweetPublishService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(EXTRA_ID, modelId);
        context.startService(intent);
    }


    /**
     * 查询发送失败动弹
     * 如果有将通过广播{@link #ACTION_RECEIVER_SEARCH_FAILED}通知
     */
    public static void startActionSearchFailed(Context context) {
        Intent intent = new Intent(context, TweetPublishService.class);
        intent.setAction(ACTION_RECEIVER_SEARCH_FAILED);
        context.startService(intent);
    }

    public TweetPublishService() {
    }

    private void addTask(int startId) {
        mDoAddTask = true;
        synchronized (TweetPublishService.this) {
            mTaskCount = mTaskCount + 1;
            log("removeTask:" + startId + " count:" + mTaskCount);
        }
        mDoAddTask = false;
    }

    private void removeTask(int startId) {
        synchronized (TweetPublishService.this) {
            mTaskCount = mTaskCount - 1;
            log("removeTask:" + startId + " count:" + mTaskCount + " doAdd:" + mDoAddTask);
            if (mTaskCount == 0 && !mDoAddTask)
                stopSelf();
        }
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

        HandlerThread thread = new HandlerThread(TweetPublishService.class.getSimpleName());
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mReceiver = ListenAccountChangeReceiver.start(this);
    }

    /**
     * You should not override this method for your IntentService. Instead,
     * override {@link #onHandleIntent}, which the system calls when the IntentService
     * receives a start request.
     *
     * @see android.app.Service#onStartCommand
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // In this, we add the task count
        addTask(startId);
        // Do message
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
        mReceiver.destroy();
        log("onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 在线程中处理请求数据
     *
     * @param intent  请求的数据
     * @param startId 启动服务的Id
     */
    private void onHandleIntent(Intent intent, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            log("onHandleIntent:" + startId);
            log(action);

            if (ACTION_PUBLISH.equals(action)) {
                final String content = intent.getStringExtra(EXTRA_CONTENT);
                final String[] images = intent.getStringArrayExtra(EXTRA_IMAGES);
                final About.Share share = (About.Share) intent.getSerializableExtra(EXTRA_ABOUT);
                handleActionPublish(content, images, share, startId);
            } else {
                if (ACTION_CONTINUE.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_ID);
                    if (id == null || handleActionContinue(id, startId)) {
                        removeTask(startId);
                    }
                } else if (ACTION_DELETE.equals(action)) {
                    final String id = intent.getStringExtra(EXTRA_ID);
                    if (id == null || handleActionDelete(id, startId)) {
                        removeTask(startId);
                    }
                } else if (ACTION_RECEIVER_SEARCH_FAILED.equals(action)) {
                    handleActionSearch();
                    removeTask(startId);
                }
            }
        }
    }

    /**
     * 发布动弹,在后台服务中进行
     */
    private void handleActionPublish(String content, String[] images, About.Share share, int startId) {
        TweetPublishModel model = new TweetPublishModel(content, images, share);
        TweetPublishCache.save(getApplicationContext(), model.getId(), model);
        Contract.IOperator operator = new TweetPublishOperator(model, this, startId);
        operator.run();
    }

    /**
     * 继续发送动弹
     *
     * @param id      动弹Id
     * @param startId 服务Id
     * @return 返回是否销毁当前服务
     */
    private boolean handleActionContinue(String id, int startId) {
        Contract.IOperator operator = mRunTasks.get(id);
        if (operator != null) {
            // 正在运行, 不做操作
            return true;
        }
        TweetPublishModel model = TweetPublishCache.get(getApplicationContext(), id);
        if (model != null) {
            operator = new TweetPublishOperator(model, this, startId);
            operator.run();
            return false;
        }
        return true;
    }

    /**
     * 移除动弹
     * 该动弹的缓存将进行清空
     *
     * @param id      动弹Id
     * @param startId 服务Id
     * @return 返回是否销毁当前服务
     */
    private boolean handleActionDelete(String id, int startId) {
        Contract.IOperator operator = mRunTasks.get(id);
        if (operator != null)
            operator.stop();
        TweetPublishCache.remove(getApplicationContext(), id);
        // In this we need remove the notify
        notifyCancel(id.hashCode());
        return true;
    }

    /**
     * 进行查询操作,
     * 查询当前发送失败的动弹
     */
    private boolean handleActionSearch() {
        List<TweetPublishModel> models = TweetPublishCache.list(getApplicationContext());
        if (models == null || models.size() == 0)
            return false;

        final Map<String, Contract.IOperator> runningMap = mRunTasks;
        List<String> ids = new ArrayList<>();

        for (TweetPublishModel model : models) {
            if (!runningMap.containsKey(model.getId())) {
                ids.add(model.getId());
            }
        }

        // If have failed task, we need callback
        if (ids.size() > 0) {
            Intent intent = new Intent(ACTION_RECEIVER_SEARCH_FAILED);
            intent.putExtra(EXTRA_IDS, CollectionUtil.toArray(ids, String.class));
            sendBroadcast(intent);
            return true;
        }

        return false;
    }

    @Override
    public String getCachePath(String id) {
        return TweetPublishCache.getImageCachePath(getApplicationContext(), id);
    }

    @Override
    public void start(String id, Contract.IOperator operator) {
        if (!mRunTasks.containsKey(id)) {
            mRunTasks.put(id, operator);
        }
    }

    @Override
    public void stop(String id, int startId) {
        if (mRunTasks.containsKey(id)) {
            mRunTasks.remove(id);
        }
        // stop self
        removeTask(startId);
    }

    @Override
    public void updateModelCache(String id, TweetPublishModel model) {
        if (model == null)
            TweetPublishCache.remove(getApplicationContext(), id);
        else
            TweetPublishCache.save(getApplicationContext(), id, model);
    }

    @Override
    public void notifyMsg(int notifyId, String modelId, boolean haveReDo, boolean haveDelete, int resId, Object... values) {
//        PendingIntent contentIntent = null;
//        if (haveReDo) {
//            Intent intent = new Intent(this, TweetPublishService.class);
//            intent.setAction(ACTION_CONTINUE);
//            intent.putExtra(EXTRA_ID, modelId);
//            contentIntent = PendingIntent.getService(getApplicationContext(), notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }
//
//        PendingIntent deleteIntent = null;
//        if (haveDelete) {
//            Intent intent = new Intent(this, TweetPublishService.class);
//            intent.setAction(ACTION_DELETE);
//            intent.putExtra(EXTRA_ID, modelId);
//            deleteIntent = PendingIntent.getService(getApplicationContext(), notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }
//
        String content = getString(resId, values);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setTicker(content)
//                .setContentTitle(getString(R.string.tweet_publish_title))
//                .setContentText(content)
//                .setAutoCancel(haveDelete)
//                .setOngoing(!haveReDo)
//                .setContentIntent(contentIntent)
//                .setDeleteIntent(deleteIntent)
//                .setSmallIcon(R.mipmap.ic_notification);

        //Notification notification = builder.build();
        //NotificationManagerCompat.from(this).notify(notifyId, notification);

        Intent intent = new Intent(ACTION_PROGRESS);
        intent.putExtra(EXTRA_PROGRESS, content);
        sendBroadcast(intent);

        log(content);
    }

    @Override
    public void notifyCancel(int notifyId) {
        NotificationManagerCompat.from(this).cancel(notifyId);
    }

    public static void log(String str) {
        TLog.log(TAG, str);
    }
}
