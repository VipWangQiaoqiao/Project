package net.oschina.open.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by fei on 2016/10/10.
 * desc:
 */

public class Share implements Serializable {
    private String appName;
    private int appShareIcon;
    private String title;
    private String summary;
    private String content;
    private String description;
    private String url;
    private int bitmapResID;
    private String imageUrl;
    private Bitmap thumbBitmap;
    private String localPath;

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getAppShareIcon() {
        return appShareIcon;
    }

    public void setAppShareIcon(int appShareIcon) {
        this.appShareIcon = appShareIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getBitmapResID() {
        return bitmapResID;
    }

    public void setBitmapResID(int bitmapResID) {
        this.bitmapResID = bitmapResID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }
}
