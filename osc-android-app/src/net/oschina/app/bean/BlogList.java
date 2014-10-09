package net.oschina.app.bean;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author HuangWenwei
 *
 * @date 2014年9月28日
 */
@XStreamAlias("oschina")
public class BlogList extends Entity implements ListEntity {
	
	public static final int CATALOG_USER = 1;
	public static final int CATALOG_LATEST = 2;
	public static final int  CATALOG_RECOMMEND = 3;
	
	@XStreamAlias("pagesize")
	private int pagesize;
	
	@XStreamAlias("blogs")
	private List<Blog> bloglist = new ArrayList<Blog>();

	public int getPageSize() {
		return pagesize;
	}

	public void setPageSize(int pageSize) {
		this.pagesize = pageSize;
	}

	public List<Blog> getBloglist() {
		return bloglist;
	}

	public void setBloglist(List<Blog> bloglist) {
		this.bloglist = bloglist;
	}

	@Override
	public List<?> getList() {
		return bloglist;
	}

}
