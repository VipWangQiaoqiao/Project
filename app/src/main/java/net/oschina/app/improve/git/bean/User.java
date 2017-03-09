package net.oschina.app.improve.git.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by haibin
 * on 2016/11/4.
 */
@SuppressWarnings("unused")
public class User implements Serializable {

    private long id;

    private String username;

    private String name;

    private String bio;

    @SerializedName("weibo")
    private String weiBo;

    private String blog;

    @SerializedName("theme_id")
    private int themeId;

    private String state;

    @SerializedName("created_at")
    private String createdData;

    private String portrait;

    private String email;

    @SerializedName("new_portrait")
    private String newPortrait;

    @SerializedName("private_token")
    private String privateToken;

    @SerializedName("is_admin")
    private boolean isAdmin;

    @SerializedName("can_create_group")
    private boolean canCreateGroup;

    @SerializedName("can_create_project")
    private boolean canCreateProject;

    @SerializedName("can_create_team")
    private boolean canCreateTeam;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getWeiBo() {
        return weiBo;
    }

    public void setWeiBo(String weiBo) {
        this.weiBo = weiBo;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreatedData() {
        return createdData;
    }

    public void setCreatedData(String createdData) {
        this.createdData = createdData;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPortrait() {
        return newPortrait;
    }

    public void setNewPortrait(String newPortrait) {
        this.newPortrait = newPortrait;
    }

    public String getPrivateToken() {
        return privateToken;
    }

    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isCanCreateGroup() {
        return canCreateGroup;
    }

    public void setCanCreateGroup(boolean canCreateGroup) {
        this.canCreateGroup = canCreateGroup;
    }

    public boolean isCanCreateProject() {
        return canCreateProject;
    }

    public void setCanCreateProject(boolean canCreateProject) {
        this.canCreateProject = canCreateProject;
    }

    public boolean isCanCreateTeam() {
        return canCreateTeam;
    }

    public void setCanCreateTeam(boolean canCreateTeam) {
        this.canCreateTeam = canCreateTeam;
    }
}
