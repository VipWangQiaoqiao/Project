package net.oschina.app.improve.tweet.service;

import net.oschina.app.improve.bean.simple.About;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by JuQiu
 * on 16/7/21.
 */

@SuppressWarnings("WeakerAccess")
public class TweetPublishModel implements Serializable {
    private String id;
    private long date;
    private String content;
    private String[] srcImages;
    private String[] cacheImages;
    private String cacheImagesToken;
    private int cacheImagesIndex;
    private String errorString;
    private long aboutId;
    private int aboutType;
    private String aboutImage;

    public TweetPublishModel() {
        id = UUID.randomUUID().toString();
        date = System.currentTimeMillis();
    }

    public TweetPublishModel(String content, String[] images, About about) {
        this();
        this.content = content;
        this.srcImages = images;
        if (about != null) {
            this.aboutId = about.getId();
            this.aboutType = about.getType();
            this.aboutImage = about.getImage();
        }
    }

    public String getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public About getAbout() {
        if (aboutId > 0 && aboutType > 0) {
            About about = new About();
            about.setId(aboutId);
            about.setType(aboutType);
            about.setImage(aboutImage);
            return about;
        }
        return null;
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

    public void setCacheImagesInfo(int cacheImagesIndex, String cacheImagesToken) {
        this.cacheImagesToken = cacheImagesToken;
        this.cacheImagesIndex = cacheImagesIndex;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }
}
