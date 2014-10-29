package net.oschina.app.bean;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author HuangWenwei
 *
 * @date 2014年9月28日
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class BlogList extends Entity implements ListEntity {
	
	@XStreamAlias("blogsCount")
	private int blogsCount;
	
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

	public int getBlogsCount() {
		return blogsCount;
	}

	public void setBlogsCount(int blogsCount) {
		this.blogsCount = blogsCount;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

}
