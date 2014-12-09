package net.oschina.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 搜索实体类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年12月5日 上午11:19:44
 * 
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class SearchList extends Entity implements ListEntity {

	public final static String CATALOG_ALL = "all";
	public final static String CATALOG_NEWS = "news";
	public final static String CATALOG_POST = "post";
	public final static String CATALOG_SOFTWARE = "software";
	public final static String CATALOG_BLOG = "blog";
	public final static String CATALOG_CODE = "code";

	@XStreamAlias("pagesize")
	private int pageSize;

	@XStreamAlias("results")
	private List<SearchResult> resultlist = new ArrayList<SearchResult>();

	/**
	 * 搜索结果实体类
	 */
	@XStreamAlias("result")
	public static class SearchResult implements Serializable {
		@XStreamAlias("objid")
		private int objid;
		@XStreamAlias("type")
		private int type;
		@XStreamAlias("title")
		private String title;
		@XStreamAlias("url")
		private String url;
		@XStreamAlias("pubDate")
		private String pubDate;
		@XStreamAlias("author")
		private String author;

		public int getObjid() {
			return objid;
		}

		public void setObjid(int objid) {
			this.objid = objid;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

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

		public String getPubDate() {
			return pubDate;
		}

		public void setPubDate(String pubDate) {
			this.pubDate = pubDate;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}
	}

	public int getPageSize() {
		return pageSize;
	}

	public List<SearchResult> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List<SearchResult> resultlist) {
		this.resultlist = resultlist;
	}

	@Override
	public List<?> getList() {
		return resultlist;
	}
}
