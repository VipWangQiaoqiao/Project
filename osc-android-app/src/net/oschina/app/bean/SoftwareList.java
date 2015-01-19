package net.oschina.app.bean;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class SoftwareList extends Entity implements ListEntity {
	
	public final static String PREF_READED_SOFTWARE_LIST = "readed_software_list.pref";
	
	public final static String CATALOG_RECOMMEND = "recommend";
	public final static String CATALOG_TIME = "time";
	public final static String CATALOG_VIEW = "view";
	public final static String CATALOG_LIST_CN = "list_cn";

	@XStreamAlias("softwarecount")
	private int softwarecount;
	@XStreamAlias("pagesize")
	private int pagesize;
	@XStreamAlias("softwares")
	private List<SoftwareDec> softwarelist = new ArrayList<SoftwareDec>();

	public int getSoftwarecount() {
		return softwarecount;
	}

	public void setSoftwarecount(int softwarecount) {
		this.softwarecount = softwarecount;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public List<SoftwareDec> getSoftwarelist() {
		return softwarelist;
	}

	public void setSoftwarelist(List<SoftwareDec> softwarelist) {
		this.softwarelist = softwarelist;
	}

	@Override
	public List<?> getList() {
		return softwarelist;
	}

	@XStreamAlias("software")
	public class SoftwareDec extends Entity {
		@XStreamAlias("name")
		private String name;
		@XStreamAlias("description")
		private String description;
		@XStreamAlias("url")
		private String url;

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

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
}
