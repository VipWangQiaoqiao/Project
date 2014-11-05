package net.oschina.app.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@SuppressWarnings("serial")
@XStreamAlias("softwareType")
public class SoftwareType extends Entity {

	@XStreamAlias("name")
	private String name;
	@XStreamAlias("tag")
	private int tag;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	
}
