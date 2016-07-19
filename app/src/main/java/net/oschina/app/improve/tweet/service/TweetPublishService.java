package net.oschina.app.improve.tweet.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

import cz.msebera.android.httpclient.Header;

/**
 * 动弹发布服务
 * 专用于动弹发布
 */
public class TweetPublishService extends IntentService {
    private final static String TAG = TweetPublishService.class.getName();
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_PUBLISH = "net.oschina.app.improve.tweet.service.action.PUBLISH";

    private static final String EXTRA_CONTENT = "net.oschina.app.improve.tweet.service.extra.CONTENT";
    private static final String EXTRA_IMAGES = "net.oschina.app.improve.tweet.service.extra.IMAGES";
    private static final String EXTRA_ID = "net.oschina.app.improve.tweet.service.extra.ID";

    public TweetPublishService() {
        super("TweetPublishService");
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

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PUBLISH.equals(action)) {
                final String content = intent.getStringExtra(EXTRA_CONTENT);
                final String[] images = intent.getStringArrayExtra(EXTRA_IMAGES);
                handleActionPublish(content, images);
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

    interface UploadImageCallback {
        void onUploadImageDone();
    }

    /**
     * 发布动弹执行者
     */
    private class Operator implements UploadImageCallback, Runnable {
        private final int id = (int) System.currentTimeMillis();
        private final String content;
        private String[] images;

        private int index;
        private String token;

        Operator(String content, String[] images) {
            this.content = content;
            this.images = images;
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
            this.token = token;

            if (index < 0 || index >= paths.length) {
                runnable.onUploadImageDone();
                return;
            }
            String path = paths[index];

            notifyMsg("发送图片: token:" + token + " path:" + path);

            OSChinaApi.uploadImage(token, path, new LopperResponseHandler() {

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    uploadImages(index, token, paths, runnable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Type type = new TypeToken<ResultBean<ImageResource>>() {
                    }.getType();
                    ResultBean<ImageResource> resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        String token = resultBean.getResult().getToken();
                        notifyMsg("发送图片成功~" + token);
                        uploadImages(index + 1, token, paths, runnable);
                    } else {
                        notifyMsg("发送图片失败~ " + resultBean.getCode() + " " + resultBean.getMessage());
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
                            new FileInputStream(path), 512, 512);
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

        /**
         * 执行动弹发布操作
         */
        @Override
        public void run() {
            if (images == null) {
                // 当没有图片的时候,直接进行发布动弹操作
                onUploadImageDone();
            } else {
                String[] uploadPaths = saveImageToCache(images);
                if (uploadPaths == null) {
                    notifyMsg("图片转存失败~");
                    onUploadImageDone();
                    return;
                }
                uploadImages(0, null, uploadPaths, this);
            }
        }

        /**
         * 当图片上传完成时回调操作
         */
        @Override
        public void onUploadImageDone() {
            OSChinaApi.pubTweet(content, token, null, new LopperResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    notifyMsg("发送动弹失败~");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    notifyMsg("发送动弹成功~");
                    cancelNotify();
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
                    msg, msg, true, false);
        }

        private void cancelNotify() {
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
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ID, id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(autoCancel)
                .setOngoing(ongoing)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification);

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(id, notification);
    }

    private void cancelNotification(int id) {
        NotificationManagerCompat.from(this).cancel(id);
    }
}
