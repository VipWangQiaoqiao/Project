package net.oschina.app.team.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.oschina.app.bean.Entity;

/**
 * Reply.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 * 
 * @data 2015-1-30 下午3:58:27
 */
@SuppressWarnings("serial")
@XStreamAlias("reply")
public class TeamReply extends Entity {
    
    public final static String REPLY_TYPE_ISSUE = "issue";
    public final static String REPLY_TYPE_DIARY = "diary";
    public final static String REPLY_TYPE_DISSUCC = "disscuss";

    public final static int CLIENT_MOBILE = 2;
    public final static int CLIENT_ANDROID = 3;
    public final static int CLIENT_IPHONE = 4;
    public final static int CLIENT_WINDOWS_PHONE = 5;

    @XStreamAlias("content")
    private String content;

    @XStreamAlias("createTime")
    private String createTime;

    @XStreamAlias("author")
    private Author author;

    @XStreamAlias("appclient")
    private int appClient;

    public int getAppClient() {
	return appClient;
    }

    public void setAppClient(int appClient) {
	this.appClient = appClient;
    }

    public String getContent() {
	return content;
    }

    public void setContent(String content) {
	this.content = content;
    }

    public String getCreateTime() {
	return createTime;
    }

    public void setCreateTime(String createTime) {
	this.createTime = createTime;
    }

    public Author getAuthor() {
	return author;
    }

    public void setAuthor(Author author) {
	this.author = author;
    }
}
