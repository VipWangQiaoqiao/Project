package net.oschina.app.improve.share.bean;

import java.io.Serializable;

/**
 * Created by fei on 2016/10/10.
 * desc:
 */

public class Share implements Serializable {

    public static final int SHARE_SESSION = 1;
    public static final int SHARE_TIMELINE = 0;

    private String title;
    private String content;
    private String url;
    private int shareScene;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getShareScene() {
        return shareScene;
    }

    public void setShareScene(int shareScene) {
        this.shareScene = shareScene;
    }

    @Override
    public String toString() {
        return "Share{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", shareScene=" + shareScene +
                '}';
    }
}
