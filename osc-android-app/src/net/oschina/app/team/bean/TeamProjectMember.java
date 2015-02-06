package net.oschina.app.team.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import net.oschina.app.bean.Entity;

/**
 * TeamProjectMember.java
 * 
 * @author 火蚁(http://my.oschina.net/u/253900)
 *
 * @data 2015-2-6 上午10:36:51
 */
@SuppressWarnings("serial")
@XStreamAlias("member")
public class TeamProjectMember extends Entity {
    
    @XStreamAlias("name")
    private String name;
    
    @XStreamAlias("portrait")
    private String portrait;
    
    @XStreamAlias("gender")
    private String gender;
    
    @XStreamAlias("teamRole")
    private String teamRole;
    
    @XStreamAlias("projectId")
    private int projectId;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTeamRole() {
        return teamRole;
    }

    public void setTeamRole(String teamRole) {
        this.teamRole = teamRole;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}

