package net.oschina.app.improve.tweet.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.resource.ImageResource;

import org.kymjs.kjframe.bitmap.BitmapCreate;
import org.kymjs.kjframe.utils.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹发布服务
 * 专用于动弹发布
 */
public class TweetPublishService extends Service {
    private final static String TAG = TweetPublishService.class.getName();
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_PUBLISH = "net.oschina.app.improve.tweet.service.action.PUBLISH";
    private static final String ACTION_CONTINUE = "net.oschina.app.improve.tweet.service.action.CONTINUE";
    private static final String ACTION_DELETE = "net.oschina.app.improve.tweet.service.action.DELETE";

    private static final String EXTRA_CONTENT = "net.oschina.app.improve.tweet.service.extra.CONTENT";
    private static final String EXTRA_IMAGES = "net.oschina.app.improve.tweet.service.extra.IMAGES";
    private static final String EXTRA_ID = "net.oschina.app.improve.tweet.service.extra.ID";

    private static Map<Integer, Operator> mTasks = new ArrayMap<>();
    private static int mIndex = 1;

    private int id = 0;

    public TweetPublishService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("TweetPublishService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("TweetPublishService onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("TweetPublishService onStartCommand");
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 发起动弹发布服务
     */
    public static void startActionPublish(Context context, String content, List<String> images) {
        Intent intent = new Intent(context, TweetPublishService.class);
        intent.setAction(ACTION_PUBLISH);
        intent.putExtra(EXTRA_CONTENT, content);
        if (images != null && images.size() > 0) {
            String[] pubImages = new String[images.size()];
            images.toArray(pubImages);
            intent.putExtra(EXTRA_IMAGES, pubImages);
        }
        context.startService(intent);
    }

    protected void onHandleIntent(Intent intent) {
        id++;
        log("id:" + id);
        if (intent != null) {
            final String action = intent.getAction();

            log(action);

            if (ACTION_PUBLISH.equals(action)) {
                final String content = intent.getStringExtra(EXTRA_CONTENT);
                final String[] images = intent.getStringArrayExtra(EXTRA_IMAGES);
                handleActionPublish(content, images);
            } else if (ACTION_CONTINUE.equals(action)) {
                final int id = intent.getIntExtra(EXTRA_ID, 0);
                if (id == 0)
                    return;
                handleActionContinue(id);
            } else if (ACTION_DELETE.equals(action)) {
                final int id = intent.getIntExtra(EXTRA_ID, 0);
                if (id == 0)
                    return;
                handleActionDelete(id);
            }
        }
    }

    /**
     * 发布动弹,在后台服务中进行
     */
    private void handleActionPublish(String content, String[] images) {
        Operator operator = new Operator(content, images);
        operator.notifyMsg(getString(R.string.tweet_publishing));
        operator.run();
    }

    private void handleActionContinue(int id) {
        Operator operator = mTasks.get(id);
        log("id:" + id + " " + (operator != null));
        if (operator != null) {
            operator.notifyMsg(getString(R.string.tweet_publish_continue));
            operator.run();
        }
    }

    private void handleActionDelete(int id) {
        stopSelf();
        if (mTasks.containsKey(id)) {
            mTasks.remove(id);
        }
    }

    private int addTask(Operator operator) {
        mTasks.put(mIndex, operator);
        return mIndex++;
    }

    interface UploadImageCallback {
        void onUploadImageDone();
    }

    /**
     * 发布动弹执行者
     */
    private class Operator implements Runnable {
        private final int id;
        private final String content;

        private String[] images;
        private String[] cacheImages;
        private int uploadImagesIndex;
        private String uploadImagesToken;

        private boolean running;
        private boolean done;

        Operator(String content, String[] images) {
            this.content = content;
            this.images = images;

            id = addTask(this);
        }

        /**
         * 执行动弹发布操作
         */
        @Override
        public void run() {
            if (done) {
                mTasks.remove(id);
                return;
            }

            if (running)
                return;

            running = true;

            log(mTasks.size() + " " + id);

            if (images == null) {
                // 当没有图片的时候,直接进行发布动弹
                publish();
            } else {
                if (cacheImages == null) {
                    notifyMsg("准备图片中...");
                    cacheImages = saveImageToCache(images);
                    if (cacheImages == null) {
                        images = null;
                        notifyMsg("图片转存失败!");
                        publish();
                        return;
                    }
                }
                uploadImages(uploadImagesIndex, uploadImagesToken, cacheImages, new UploadImageCallback() {
                    @Override
                    public void onUploadImageDone() {
                        publish();
                    }
                });
            }
        }

        /**
         * 上传图片
         *
         * @param index    上次图片的坐标
         * @param token    上传Token
         * @param paths    上传的路径数组
         * @param runnable 完全上传完成时回调
         */
        private void uploadImages(final int index, final String token, final String[] paths, final UploadImageCallback runnable) {
            this.uploadImagesIndex = index;
            this.uploadImagesToken = token;

            if (index < 0 || index >= paths.length) {
                runnable.onUploadImageDone();
                return;
            }

            String path = paths[index];
            OSChinaApi.uploadImage(token, path, new LopperResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
                    notifyMsg(String.format("发送图片中...(%s/%s)", (index + 1), +paths.length));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    running = false;
                    notifyMsg(String.format("发送图片失败(%s/%s)", (index + 1), +paths.length));
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Type type = new TypeToken<ResultBean<ImageResource>>() {
                    }.getType();
                    ResultBean<ImageResource> resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        String token = resultBean.getResult().getToken();
                        uploadImages(index + 1, token, paths, runnable);
                    } else {
                        running = false;
                        notifyMsg("发送图片失败; " + resultBean.getMessage());
                    }
                }
            });

        }

        private String getCachePath() {
            return String.format("%s/Pictures/%s", getCacheDir().getAbsolutePath(), id);
        }

        /**
         * 保存文件到缓存中
         *
         * @param paths 原始路径
         * @return 返回存储后的路径
         */
        private String[] saveImageToCache(String[] paths) {
            List<String> ret = new ArrayList<>();
            final String cachePath = getCachePath();
            for (String path : paths) {
                try {
                    Bitmap bitmap = BitmapCreate.bitmapFromStream(
                            new FileInputStream(path), 256, 256);
                    String tempFile = String.format("%s/IMG_%s.png", cachePath, System.currentTimeMillis());
                    FileUtils.bitmapToFile(bitmap, tempFile);
                    bitmap.recycle();
                    ret.add(tempFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (ret.size() > 0) {
                String[] images = new String[ret.size()];
                ret.toArray(images);
                return images;
            }
            return null;
        }

        private void setDone() {
            done = true;
            mTasks.remove(id);

        }

        /**
         * 发布动弹
         */
        private void publish() {
            OSChinaApi.pubTweet(content, uploadImagesToken, null, new LopperResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    notifyMsg("发送失败; 点击重新发送.");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        setDone();
                        notifyMsgAndCancel("发送动弹成功~");
                    } else {
                        notifyMsg("发送失败; 点击重新发送.");
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    running = false;
                }
            });
        }

        /**
         * 执行通知操作
         *
         * @param msg 通知信息
         */
        private void notifyMsg(String msg) {
            log(msg);
            notifySimpleNotification(id,
                    getString(R.string.tweet_public),
                    msg, msg, true, true);
        }

        private void notifyMsgAndCancel(String msg) {
            notifyMsg(msg);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    cancelNotification(id);
                }
            }, 2800);
        }
    }

    private static void log(String str) {
        Log.e(TAG, str);
    }

    private void notifySimpleNotification(int id, String title, String ticker,
                                          String content, boolean ongoing, boolean autoCancel) {
        Intent serverIntent = new Intent(this, TweetPublishService.class);
        serverIntent.setAction(ACTION_CONTINUE);
        serverIntent.putExtra(EXTRA_ID, id);
        PendingIntent contentIntent = PendingIntent.getService(getApplicationContext(), id, serverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        serverIntent = new Intent(this, TweetPublishService.class);
        serverIntent.setAction(ACTION_DELETE);
        serverIntent.putExtra(EXTRA_ID, id);
        PendingIntent deleteIntent = PendingIntent.getService(getApplicationContext(), id, serverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(autoCancel)
                .setOngoing(false)
                .setContentIntent(contentIntent)
                .setDeleteIntent(deleteIntent)
                .setSmallIcon(R.drawable.ic_notification);

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(id, notification);
    }

    private void cancelNotification(int id) {
        NotificationManagerCompat.from(this).cancel(id);
    }
}
