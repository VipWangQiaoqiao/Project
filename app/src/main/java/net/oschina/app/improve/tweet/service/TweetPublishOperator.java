package net.oschina.app.improve.tweet.service;

import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.resource.ImageResource;
import net.oschina.app.improve.utils.PicturesCompressor;

import java.io.File;
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
                // change the model
                model.setCacheImages(saveImageToCache(cacheDir, model.getSrcImages()));
                // update to cache file
                service.updateModelCache(model.getId(), model);

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
                            model.setCacheImagesInfo(index, token);
                            // update to cache file
                            service.updateModelCache(model.getId(), model);
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
        // call progress
        runnable.onUploadImage(index, token);

        // check done
        if (index < 0 || index >= paths.length) {
            runnable.onUploadImageDone();
            return;
        }

        final String path = paths[index];

        OSChinaApi.uploadImage(token, path, new LopperResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                notifyMsg(R.string.tweet_image_publishing, String.valueOf(paths.length - index));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String error = "";
                String response = responseString == null ? "" : responseString;
                if (throwable != null) {
                    throwable.printStackTrace();
                    error = throwable.getMessage();
                    if (error.contains("UnknownHostException")
                            || error.contains("Read error: ssl")
                            || error.contains("Connection timed out")) {
                        saveError("Upload", "network error");
                    } else {
                        saveError("Upload", response + " " + error);
                    }
                }
                TweetPublishService.log(String.format("Upload image onFailure, statusCode:[%s] responseString:%s throwable:%s",
                        statusCode, response, error));
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
                        File file = new File(path);
                        TweetPublishService.log(String.format("Upload name:[%s] size:[%s] error:%s",
                                file.getAbsolutePath(), file.length(), resultBean.getMessage()));
                        saveError("Upload", resultBean.getMessage());
                        onFailure(statusCode, headers, responseString, null);
                    }
                } catch (Exception e) {
                    saveError("Upload", "response parse error「" + responseString + "」");
                    onFailure(statusCode, headers, responseString, null);
                }
            }
        });
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
                String error = "";
                String response = responseString == null ? "" : responseString;
                if (throwable != null) {
                    throwable.printStackTrace();
                    error = throwable.getMessage();
                    if (error.contains("UnknownHostException")
                            || error.contains("Read error: ssl")
                            || error.contains("Connection timed out")) {
                        saveError("Publish", "network error");
                    } else {
                        saveError("Publish", response + " " + error);
                    }
                }

                TweetPublishService.log(String.format("Publish tweet onFailure, statusCode:[%s] responseString:%s throwable:%s",
                        statusCode, response, error));
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
                        saveError("Publish", resultBean.getMessage());
                        onFailure(statusCode, headers, responseString, null);
                    }
                } catch (Exception e) {
                    saveError("Publish", "response parse error「" + responseString + "」");
                    onFailure(statusCode, headers, responseString, null);
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
            Thread.sleep(1600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Contract.IService service = this.service;
        if (service != null) {
            // clear the cache
            service.updateModelCache(model.getId(), null);
            // hide the notify
            service.notifyCancel(notificationId);
        }
        stop();
    }

    private void setError(int resId, Object... values) {
        notifyMsg(true, resId, values);
        stop();
    }


    // Max upload 860KB
    private static final long MAX_UPLOAD_LENGTH = 860 * 1024;

    /**
     * 保存文件到缓存中
     *
     * @param cacheDir 缓存文件夹
     * @param paths    原始路径
     * @return 转存后的路径
     */
    private static String[] saveImageToCache(String cacheDir, String[] paths) {
        List<String> ret = new ArrayList<>();
        byte[] buffer = new byte[PicturesCompressor.DEFAULT_BUFFER_SIZE];
        BitmapFactory.Options options = PicturesCompressor.createOptions();
        for (String path : paths) {
            File sourcePath = new File(path);
            if (!sourcePath.exists())
                continue;
            try {
                String name = sourcePath.getName();
                String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
                String tempFile = String.format("%s/IMG_%s.%s", cacheDir, System.currentTimeMillis(), ext);
                if (PicturesCompressor.compressImage(path, tempFile,
                        MAX_UPLOAD_LENGTH, 80,
                        1280, 1280 * 6,
                        buffer, options, true)) {
                    TweetPublishService.log("OPERATOR doImage " + tempFile + " " + new File(tempFile).length());

                    // verify the picture ext.
                    tempFile = PicturesCompressor.verifyPictureExt(tempFile);

                    ret.add(tempFile);
                }
            } catch (Exception e) {
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

    private void saveError(String cmd, String log) {
        model.setErrorString(String.format("%s | %s", cmd, log));
        // update to cache file save error log
        service.updateModelCache(model.getId(), model);
    }
}
