package net.oschina.app.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 动弹实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 * @changed 2014-01-21
 * @difference 1.添加语音动弹属性
 */
@SuppressWarnings("serial")
@XStreamAlias("tweet")
public class Tweet extends Entity {
	
	public final static int CATALOG_LATEST = 0;
	public final static int CATALOG_HOT = -1;
	public final static int CATALOG_MINE = 3;
	
	public final static int CLIENT_MOBILE = 2;
	public final static int CLIENT_ANDROID = 3;
	public final static int CLIENT_IPHONE = 4;
	public final static int CLIENT_WINDOWS_PHONE = 5;
	public final static int CLIENT_WECHAT=6;

	@XStreamAlias("portrait")
	private String portrait;
	@XStreamAlias("author")
	private String author;
	@XStreamAlias("authorid")
	private int authorid;
	@XStreamAlias("body")
	private String body;
	@XStreamAlias("appclient")
	private int appclient;
	@XStreamAlias("commentCount")
	private int commentCount;
	@XStreamAlias("pubDate")
	private String pubDate;
	@XStreamAlias("imgSmall")
	private String imgSmall;
	@XStreamAlias("imgBig")
	private String imgBig;
	@XStreamAlias("attach")
	private String attach;
	
/*	public File getImageFile() {
		return imageFile;
	}
	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}
	private File imageFile;*/
	
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getAuthorid() {
		return authorid;
	}
	public void setAuthorid(int authorid) {
		this.authorid = authorid;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public int getAppclient() {
		return appclient;
	}
	public void setAppclient(int appclient) {
		this.appclient = appclient;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}
	public String getImgSmall() {
		return imgSmall;
	}
	public void setImgSmall(String imgSmall) {
		this.imgSmall = imgSmall;
	}
	public String getImgBig() {
		return imgBig;
	}
	public void setImgBig(String imgBig) {
		this.imgBig = imgBig;
	}
	public static int getCatalogLatest() {
		return CATALOG_LATEST;
	}
	public static int getCatalogHot() {
		return CATALOG_HOT;
	}
	public static int getCatalogMine() {
		return CATALOG_MINE;
	}
	
}
