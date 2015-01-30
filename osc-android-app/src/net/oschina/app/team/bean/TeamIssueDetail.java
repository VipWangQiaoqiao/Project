package net.oschina.app.team.bean;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.oschina.app.bean.Entity;
import net.oschina.app.team.bean.TeamIssue.ToUser;

/**
 * 团队任务实体类
 * 
 * TeamIssueDetail.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 *
 * @data 2015-1-27 下午7:44:38
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class TeamIssueDetail extends Entity {
    @XStreamAlias("state")
    private String state;

    @XStreamAlias("stateLevel")
    private int stateLevel;

    @XStreamAlias("priority")
    private String priority;

    @XStreamAlias("source")
    private String source;

    @XStreamAlias("title")
    private String title;

    @XStreamAlias("description")
    private String description;

    @XStreamAlias("createTime")
    private String createTime;

    @XStreamAlias("updateTime")
    private String updateTime;

    @XStreamAlias("acceptTime")
    private String acceptTime;

    @XStreamAlias("author")
    private Author author;

    @XStreamAlias("toUser")
    private ToUser toUser;

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public int getStateLevel() {
	return stateLevel;
    }

    public void setStateLevel(int stateLevel) {
	this.stateLevel = stateLevel;
    }

    public String getPriority() {
	return priority;
    }

    public void setPriority(String priority) {
	this.priority = priority;
    }

    public String getSource() {
	return source;
    }

    public void setSource(String source) {
	this.source = source;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getCreateTime() {
	return createTime;
    }

    public void setCreateTime(String createTime) {
	this.createTime = createTime;
    }

    public String getUpdateTime() {
	return updateTime;
    }

    public void setUpdateTime(String updateTime) {
	this.updateTime = updateTime;
    }

    public String getAcceptTime() {
	return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
	this.acceptTime = acceptTime;
    }

    public Author getAuthor() {
	return author;
    }

    public void setAuthor(Author author) {
	this.author = author;
    }

    public ToUser getToUser() {
	return toUser;
    }

    public void setToUser(ToUser toUser) {
	this.toUser = toUser;
    }

    @XStreamAlias("toUser")
    public class ToUser implements Serializable {

	@XStreamAlias("id")
	private int id;

	@XStreamAlias("name")
	private String name;

	@XStreamAlias("portrait")
	private String portrait;

	public int getId() {
	    return id;
	}

	public void setId(int id) {
	    this.id = id;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getPortrait() {
	    return portrait;
	}

	public void setPortrait(String portrait) {
	    this.portrait = portrait;
	}
    }
}

