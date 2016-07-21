package net.oschina.app.improve.tweet.service;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by JuQiu
 * on 16/7/21.
 */

@SuppressWarnings("WeakerAccess")
class TweetPublishModel implements Serializable {
    private String id;
    private String content;
    private String[] srcImages;
    private String[] cacheImages;
    private String cacheImagesToken;
    private int cacheImagesIndex;

    public TweetPublishModel() {
        id = UUID.randomUUID().toString();
    }

    public TweetPublishModel(String content, String[] images) {
        this();
        this.content = content;
        this.srcImages = images;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String[] getSrcImages() {
        return srcImages;
    }

    public String[] getCacheImages() {
        return cacheImages;
    }

    public String getCacheImagesToken() {
        return cacheImagesToken;
    }

    public int getCacheImagesIndex() {
        return cacheImagesIndex;
    }

    public void setCacheImages(String[] cacheImages) {
        this.cacheImages = cacheImages;
        this.srcImages = null;
    }

    public void setCacheImagesToken(String cacheImagesToken) {
        this.cacheImagesToken = cacheImagesToken;
    }

    public void setCacheImagesIndex(int cacheImagesIndex) {
        this.cacheImagesIndex = cacheImagesIndex;
    }

    public void remove() {

    }
}
