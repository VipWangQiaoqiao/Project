package net.oschina.app.improve.tweet.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;

import org.kymjs.kjframe.bitmap.BitmapCreate;
import org.kymjs.kjframe.utils.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 动弹发布服务
 * 专用于动弹发布
 */
public class TweetPublishService extends IntentService {
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
        }
    }


    private void publish(final String content, final String imageToken) {
        OSChinaApi.pubTweet(content, imageToken, null, new LopperResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //Toast.makeText(TweetPublishActivity.this, "发送动弹失败~", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //Toast.makeText(TweetPublishActivity.this, "发送动弹成功~", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void uploadImageAndPublish(final String content, final List<String> paths) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                List<String> uploadPaths = saveImageToCache(paths);
                if (paths == null || paths.size() <= 0)
                    return;
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

    private void uploadImages(final int index, final String token, final List<String> paths, final UploadImageCallback runnable) {
        if (index < 0 || index >= paths.size())
            runnable.onDone(token);

        OSChinaApi.uploadImage(token, paths.get(index), new LopperResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });

    }

    private List<String> saveImageToCache(List<String> paths) {
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
        return ret;
    }
}
