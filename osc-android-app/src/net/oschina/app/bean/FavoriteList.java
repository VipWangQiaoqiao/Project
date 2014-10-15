package net.oschina.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 收藏实体类
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年10月14日 下午2:27:39
 *
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class FavoriteList extends Entity implements ListEntity {

	public final static int TYPE_ALL = 0x00;
	public final static int TYPE_SOFTWARE = 0x01;
	public final static int TYPE_POST = 0x02;
	public final static int TYPE_BLOG = 0x03;
	public final static int TYPE_NEWS = 0x04;
	public final static int TYPE_CODE = 0x05;
	
	@XStreamAlias("pagesize")
	private int pageSize;
	@XStreamAlias("favorites")
	private List<Favorite> favoritelist = new ArrayList<Favorite>();

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pagesize) {
		this.pageSize = pagesize;
	}

	public List<Favorite> getFavoritelist() {
		return favoritelist;
	}

	public void setFavoritelist(List<Favorite> favoritelist) {
		this.favoritelist = favoritelist;
	}

	@Override
	public List<?> getList() {
		return favoritelist;
	}
	
	/**
	 * 收藏实体类
	 */
	@XStreamAlias("favorite")
	public static class Favorite implements Serializable {
		@XStreamAlias("objid")
		public int objid;
		@XStreamAlias("type")
		public int type;
		@XStreamAlias("title")
		public String title;
		@XStreamAlias("url")
		public String url;
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
	}
}
