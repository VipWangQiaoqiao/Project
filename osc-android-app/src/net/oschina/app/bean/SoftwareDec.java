package net.oschina.app.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("serial")
@XStreamAlias("software")
public class SoftwareDec extends Entity{
	
	public final static String CATALOG_RECOMMEND = "recommend";
	public final static String CATALOG_TIME = "time";
	public final static String CATALOG_VIEW = "view";
	public final static String CATALOG_LIST_CN = "list_cn";
	
	
	
	@XStreamAlias("name")
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@XStreamAlias("description")
	private String des;
	@XStreamAlias("url")
	private String url;

}