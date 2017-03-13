package net.oschina.app.improve.git.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by haibin
 * on 2016/11/4.
 */
@SuppressWarnings("unused")
public class Project implements Serializable {
    private long id;

    private String name;

    private String description;

    private String img;

    @SerializedName("default_branch")
    private String defaultBranch;

    private User owner;

    private String path;

    @SerializedName("path_with_namespace")
    private String pathWithNamespace;

    @SerializedName("issues_enabled")
    private boolean issuesEnabled;

    @SerializedName("pull_requests_enabled")
    private boolean pullRequestsEnabled;

    @SerializedName("wiki_enabled")
    private boolean wikiEnabled;

    @SerializedName("created_at")
    private Date createdTime;

    private NameSpace namespace;

    @SerializedName("last_push_at")
    private Date lastPushTime;

    private String language;

    @SerializedName("parent_id")
    private Integer parentId;

    @SerializedName("forks_count")
    private Integer forksCount;

    @SerializedName("stars_count")
    private Integer starsCount;

    @SerializedName("watches_count")
    private Integer watchesCount;

    private boolean stared;

    private boolean watched;

    private String relation;

    private int recomm;

    @SerializedName("real_path")
    private String realPath;

    @SerializedName("svn_url_to_repo")
    private String svnUrl;

    @SerializedName("issue_count")
    private int issueCount;
    @SerializedName("pull_request_count")
    private int pullRequestCount;

    private String readme;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathWithNamespace() {
        return pathWithNamespace;
    }

    public void setPathWithNamespace(String pathWithNamespace) {
        this.pathWithNamespace = pathWithNamespace;
    }

    public boolean issuesEnabled() {
        return issuesEnabled;
    }

    public void setIssuesEnabled(boolean issuesEnabled) {
        this.issuesEnabled = issuesEnabled;
    }

    public boolean isPullRequestsEnabled() {
        return pullRequestsEnabled;
    }

    public void setPullRequestsEnabled(boolean pullRequestsEnabled) {
        this.pullRequestsEnabled = pullRequestsEnabled;
    }

    public boolean isWikiEnabled() {
        return wikiEnabled;
    }

    public void setWikiEnabled(boolean wikiEnabled) {
        this.wikiEnabled = wikiEnabled;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public NameSpace getNamespace() {
        return namespace;
    }

    public void setNamespace(NameSpace namespace) {
        this.namespace = namespace;
    }

    public Date getLastPushTime() {
        return lastPushTime;
    }

    public void setLastPushTime(Date lastPushTime) {
        this.lastPushTime = lastPushTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getForksCount() {
        return forksCount;
    }

    public void setForksCount(Integer forksCount) {
        this.forksCount = forksCount;
    }

    public Integer getStarsCount() {
        return starsCount;
    }

    public void setStarsCount(Integer starsCount) {
        this.starsCount = starsCount;
    }

    public Integer getWatchesCount() {
        return watchesCount;
    }

    public void setWatchesCount(Integer watchesCount) {
        this.watchesCount = watchesCount;
    }

    public boolean isStared() {
        return stared;
    }

    public void setStared(boolean stared) {
        this.stared = stared;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public int getRecomm() {
        return recomm;
    }

    public void setRecomm(int recomm) {
        this.recomm = recomm;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getSvnUrl() {
        return svnUrl;
    }

    public void setSvnUrl(String svnUrl) {
        this.svnUrl = svnUrl;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getReadme() {
        return readme;
    }

    public void setReadme(String readme) {
        this.readme = readme;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public int getPullRequestCount() {
        return pullRequestCount;
    }

    public void setPullRequestCount(int pullRequestCount) {
        this.pullRequestCount = pullRequestCount;
    }
}
