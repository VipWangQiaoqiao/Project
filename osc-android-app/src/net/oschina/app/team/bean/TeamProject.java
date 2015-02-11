package net.oschina.app.team.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.oschina.app.bean.Entity;

/** 
 * 团队项目实体类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2015年1月14日 下午3:18:53
 * 
 */

@SuppressWarnings("serial")
@XStreamAlias("project")
public class TeamProject extends Entity {
    
    public final static String GITOSC = "Git@OSC";
    public final static String GITHUB = "GitHub";

    @XStreamAlias("source")
    private String source;

    @XStreamAlias("team")
    private String team;

    @XStreamAlias("git")
    private TeamGit git;

    public String getSource() {
	return source;
    }

    public void setSource(String source) {
	this.source = source;
    }

    public String getTeam() {
	return team;
    }

    public void setTeam(String team) {
	this.team = team;
    }

    public TeamGit getGit() {
	return git;
    }

    public void setGit(TeamGit git) {
	this.git = git;
    }
}
