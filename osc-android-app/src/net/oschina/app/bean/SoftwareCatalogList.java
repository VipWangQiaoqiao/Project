package net.oschina.app.bean;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class SoftwareCatalogList extends Entity implements ListEntity {
	
	@XStreamAlias("softwarecount")
	private int softwarecount;
	@XStreamAlias("softwareTypes")
	private List<SoftwareType> softwarecataloglist = new ArrayList<SoftwareType>();
	
	public int getSoftwarecount() {
		return softwarecount;
	}

	public void setSoftwarecount(int softwarecount) {
		this.softwarecount = softwarecount;
	}

	public List<SoftwareType> getSoftwarecataloglist() {
		return softwarecataloglist;
	}

	public void setSoftwarecataloglist(List<SoftwareType> softwarecataloglist) {
		this.softwarecataloglist = softwarecataloglist;
	}

	@Override
	public List<?> getList() {
		return softwarecataloglist;
	}

}
