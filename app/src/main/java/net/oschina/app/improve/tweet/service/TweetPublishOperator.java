package net.oschina.app.improve.tweet.service;

import android.graphics.Bitmap;

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
 * Created by JuQiu
 * on 16/7/21.
 * 动弹发布执行者
 */
class TweetPublishOperator implements Runnable, Contract.IOperator {
    private final int serviceStartId;
    private final int notificationId;
    private Contract.IService service;
    private TweetPublishModel model;

    interface UploadImageCallback {
        void onUploadImageDone();

        void onUploadImage(int index, String token);
    }

    TweetPublishOperator(TweetPublishModel model, Contract.IService service, int startId) {
        this.model = model;
        this.notificationId = model.getId().hashCode();
        this.serviceStartId = startId;
        this.service = service;
    }

    /**
     * 执行动弹发布操作
     */
    @Override
    public void run() {
        // call to service
        this.service.start(model.getId(), this);
        // notify
        notifyMsg(R.string.tweet_publishing);

        // doing
        final TweetPublishModel model = this.model;
        if (model.getSrcImages() == null && model.getCacheImages() == null) {
            // 当没有图片的时候,直接进行发布动弹
            publish();
        } else {
            if (model.getCacheImages() == null) {
                notifyMsg(R.string.tweet_image_wait);
                final String cacheDir = service.getCachePath(model.getId());
                model.setCacheImages(saveImageToCache(cacheDir, model.getSrcImages()));
                if (model.getCacheImages() == null) {
                    notifyMsg(R.string.tweet_image_wait_failed);
                    publish();
                    return;
                }
            }
            // 开始上传图片,并回调进度
            uploadImages(model.getCacheImagesIndex(), model.getCacheImagesToken(), model.getCacheImages(),
                    new UploadImageCallback() {
                        @Override
                        public void onUploadImageDone() {
                            publish();
                        }

                        @Override
                        public void onUploadImage(int index, String token) {
                            model.setCacheImagesIndex(index);
                            model.setCacheImagesToken(token);
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
        // check done
        if (index < 0 || index >= paths.length) {
            runnable.onUploadImageDone();
            return;
        }

        // call progress
        runnable.onUploadImage(index, token);

        final String path = paths[index];

        OSChinaApi.uploadImage(token, path, new LopperResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                notifyMsg(R.string.tweet_image_publishing, String.valueOf(index + 1), String.valueOf(paths.length));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
                setError(R.string.tweet_image_publish_failed, String.valueOf(index + 1), String.valueOf(paths.length));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<ImageResource>>() {
                    }.getType();
                    ResultBean<ImageResource> resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        String token = resultBean.getResult().getToken();
                        uploadImages(index + 1, token, paths, runnable);
                    } else {
                        onFailure(statusCode, headers, responseString, new Throwable(resultBean.getMessage()));
                    }
                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, e);
                }
            }
        });
    }

    /**
     * 保存文件到缓存中
     *
     * @param cacheDir 缓存文件夹
     * @param paths    原始路径
     * @return 转存后的路径
     */
    private String[] saveImageToCache(String cacheDir, String[] paths) {
        List<String> ret = new ArrayList<>();
        for (String path : paths) {
            try {
                Bitmap bitmap = BitmapCreate.bitmapFromStream(
                        new FileInputStream(path), 256, 256);
                String tempFile = String.format("%s/IMG_%s.png", cacheDir, System.currentTimeMillis());
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

    @Override
    public void stop() {
        final Contract.IService service = this.service;
        if (service != null) {
            this.service = null;
            service.stop(model.getId(), serviceStartId);
        }
    }

    /**
     * 发布动弹
     */
    private void publish() {
        OSChinaApi.pubTweet(model.getContent(), model.getCacheImagesToken(), null, new LopperResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (throwable != null)
                    throwable.printStackTrace();
                setError(R.string.tweet_publish_failed);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean>() {
                    }.getType();
                    ResultBean resultBean = new Gson().fromJson(responseString, type);
                    if (resultBean.isSuccess()) {
                        setSuccess();
                    } else {
                        onFailure(statusCode, headers, responseString, new Throwable(resultBean.getMessage()));
                    }
                } catch (Exception e) {
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void notifyMsg(int resId, Object... values) {
        notifyMsg(false, resId, values);
    }

    private void notifyMsg(boolean done, int resId, Object... values) {
        Contract.IService service = this.service;
        if (service != null) {
            service.notifyMsg(notificationId, model.getId(), done, done, resId, values);
        }
    }

    private void setSuccess() {
        notifyMsg(R.string.tweet_publish_success);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Contract.IService service = this.service;
        if (service != null) {
            service.notifyCancel(notificationId);
        }
        stop();
    }

    private void setError(int resId, Object... values) {
        notifyMsg(true, resId, values);
        stop();
    }
}
