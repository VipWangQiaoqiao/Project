package net.oschina.open.bean;

import java.io.Serializable;

/**
 * Created by fei on 2016/10/10.
 * desc:
 */

public class Share implements Serializable {

    public static final int SHARE_SESSION = 1;
    public static final int SHARE_TIMELINE = 0;

    private String appName;
    private int appShareIcon;
    private String title;
    private String summary;
    private String content;
    private String description;
    private String defaultText;
    private String url;
    private int bitmapResID;
    private int shareScene;

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

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
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

    public int getShareScene() {
        return shareScene;
    }

    public void setShareScene(int shareScene) {
        this.shareScene = shareScene;
    }
}
