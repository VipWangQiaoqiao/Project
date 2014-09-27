package net.oschina.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import android.util.Xml;

/**
 * 新闻实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressWarnings("serial")
@XStreamAlias("news")
public class News implements Serializable {
	
	public final static int NEWSTYPE_NEWS = 0x00;//0 新闻
	public final static int NEWSTYPE_SOFTWARE = 0x01;//1 软件
	public final static int NEWSTYPE_POST = 0x02;//2 帖子
	public final static int NEWSTYPE_BLOG = 0x03;//3 博客

	@XStreamAlias("title")
	private String title;
	
	@XStreamAlias("url")
	private String url;
	
	@XStreamAlias("body")
	private String body;
	
	@XStreamAlias("author")
	private String author;
	
	@XStreamAlias("authorid")
	private int authorId;
	
	@XStreamAlias("commentcount")
	private int commentCount;
	
	@XStreamAlias("pubdate")
	private String pubDate;
	
	@XStreamAlias("softwarelink")
	private String softwareLink;
	
	@XStreamAlias("softwarename")
	private String softwareName;
	
	@XStreamAlias("favorite")
	private int favorite;
	
	@XStreamAlias("newtype")
	private NewsType newType;
	
	@XStreamAlias("news")
	private List<Relative> relatives;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
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

	public String getSoftwareLink() {
		return softwareLink;
	}

	public void setSoftwareLink(String softwareLink) {
		this.softwareLink = softwareLink;
	}

	public String getSoftwareName() {
		return softwareName;
	}

	public void setSoftwareName(String softwareName) {
		this.softwareName = softwareName;
	}

	public int getFavorite() {
		return favorite;
	}

	public void setFavorite(int favorite) {
		this.favorite = favorite;
	}

	public NewsType getNewType() {
		return newType;
	}

	public void setNewType(NewsType newType) {
		this.newType = newType;
	}

	public List<Relative> getRelatives() {
		return relatives;
	}

	public void setRelatives(List<Relative> relatives) {
		this.relatives = relatives;
	}

	public class NewsType implements Serializable{
		@XStreamAlias("type")
		public int type;
		@XStreamAlias("attachment")
		public String attachment;
		@XStreamAlias("authoruid2")
		public int authoruid2;
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getAttachment() {
			return attachment;
		}
		public void setAttachment(String attachment) {
			this.attachment = attachment;
		}
		public int getAuthoruid2() {
			return authoruid2;
		}
		public void setAuthoruid2(int authoruid2) {
			this.authoruid2 = authoruid2;
		}
	} 
	
	public static class Relative implements Serializable{
		@XStreamAlias("title")
		public String title;
		@XStreamAlias("url")
		public String url;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	} 
}
