package net.oschina.app.improve.bean;

import net.oschina.app.improve.bean.simple.About;

import java.util.List;

/**
 * Created by fei on 2016/6/20.
 * desc:
 */
public class SoftwareDetail extends Software {

    private String extName; //软件别名
    private String logo;//logo，有null的情况哦
    private String body; //软件资讯内容
    private String author;//发布者
    private long authorId;//发布者id
    private String authorPortrait;//用户头像url
    private String license;//软件license信息
    private String homePage;//首页
    private String document;//文档
    private String download;//下载地址
    private String language;//开发语言
    private String supportOS;//支持平台
    private String collectionDate; //收录时间
    private String pubDate;//发布时间
    private int commentCount; //评论量，实际是动弹量
    private int viewCount;//浏览量
    private boolean favorite;//是否收藏
    private boolean recommend;//是否推荐
    private String identification; //唯一标示
    private List<About> abouts;  //相关推荐

    public String getExtName() {
        return extName;
    }

    public void setExtName(String extName) {
        this.extName = extName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPortrait() {
        return authorPortrait;
    }

    public void setAuthorPortrait(String authorPortrait) {
        this.authorPortrait = authorPortrait;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSupportOS() {
        return supportOS;
    }

    public void setSupportOS(String supportOS) {
        this.supportOS = supportOS;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public List<About> getAbouts() {
        return abouts;
    }

    public void setAbouts(List<About> abouts) {
        this.abouts = abouts;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    @Override
    public String toString() {
        return "SoftwareDetail{" +
                "extName='" + extName + '\'' +
                ", logo='" + logo + '\'' +
                ", body='" + body + '\'' +
                ", author='" + author + '\'' +
                ", authorId=" + authorId +
                ", authorPortrait='" + authorPortrait + '\'' +
                ", license='" + license + '\'' +
                ", homePage='" + homePage + '\'' +
                ", document='" + document + '\'' +
                ", download='" + download + '\'' +
                ", language='" + language + '\'' +
                ", supportOS='" + supportOS + '\'' +
                ", collectionDate='" + collectionDate + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", commentCount=" + commentCount +
                ", viewCount=" + viewCount +
                ", favorite=" + favorite +
                ", recommend=" + recommend +
                ", identification='" + identification + '\'' +
                ", abouts=" + abouts +
                '}';
    }
}
