package net.oschina.app.improve.git.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * 代码片段
 * Created by haibin on 2017/5/10.
 */
@SuppressWarnings("unused")
public class Gist implements Serializable {
    private String url;
    private String id;
    private String name;
    private User owner;
    private String language;
    private String description;
    private String content;
    @SerializedName("comment_counts")
    private int commentCounts;
    @SerializedName("start_counts")
    private int startCounts;
    @SerializedName("fork_counts")
    private int forkCounts;
    @SerializedName("created_at")
    private Date createdDate;
    @SerializedName("updated_at")
    private Date lastUpdateDate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCommentCounts() {
        return commentCounts;
    }

    public void setCommentCounts(int commentCounts) {
        this.commentCounts = commentCounts;
    }

    public int getStartCounts() {
        return startCounts;
    }

    public void setStartCounts(int startCounts) {
        this.startCounts = startCounts;
    }

    public int getForkCounts() {
        return forkCounts;
    }

    public void setForkCounts(int forkCounts) {
        this.forkCounts = forkCounts;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
