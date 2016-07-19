package net.oschina.app.improve.tweet.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
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

    private static final String EXTRA_CONTENT = "net.oschina.app.improve.tweet.service.extra.CONtENT";
    private static final String EXTRA_IMAGES = "net.oschina.app.improve.tweet.service.extra.IMAGES";

    public TweetPublishService() {
        super("TweetPublishService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
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
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPublish(String content, String[] images) {
        if (images == null) {
            publish(content, null);
        } else {
            uploadImageAndPublish(content, images);
        }
    }


    private void publish(final String content, final String imageToken) {
        OSChinaApi.pubTweet(content, imageToken, null, new LopperResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                log("发送动弹失败~");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                log("发送动弹成功~");
            }
        });
    }

    private void uploadImageAndPublish(final String content, final String[] paths) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                String[] uploadPaths = saveImageToCache(paths);
                if (uploadPaths == null) {
                    log("图片压缩存储失败~");
                    return;
                }
                uploadImages(0, null, uploadPaths, new UploadImageCallback() {
                    @Override
                    public void onDone(String token) {
                        publish(content, token);
                    }
                });
            }
        });
    }

    interface UploadImageCallback {
        void onDone(String token);
    }

    private void uploadImages(final int index, final String token, final String[] paths, final UploadImageCallback runnable) {
        if (index < 0 || index >= paths.length) {
            runnable.onDone(token);
            return;
        }
        String path = paths[index];

        log("发送图片: token:" + token + " path:" + path);

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
                    log("发送图片成功~" + token);
                    uploadImages(index + 1, token, paths, runnable);
                } else {
                    log("发送图片失败~ " + resultBean.getCode() + " " + resultBean.getMessage());
                }
            }
        });

    }

    private String[] saveImageToCache(String[] paths) {
        List<String> ret = new ArrayList<>();
        final String sdCardPath = FileUtils.getSDCardPath();
        for (String path : paths) {
            try {
                Bitmap bitmap = BitmapCreate.bitmapFromStream(
                        new FileInputStream(path), 512, 512);
                String temp = String.format("%s/OSChina/Pictures/IMG_%s.png", sdCardPath, System.currentTimeMillis());
                FileUtils.bitmapToFile(bitmap, temp);
                bitmap.recycle();
                ret.add(temp);
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

    private static void log(String str) {
        Log.e(TAG, str);
    }
}
