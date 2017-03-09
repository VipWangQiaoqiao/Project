package net.oschina.app.improve.git.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by haibin
 * on 2016/11/4.
 */
@SuppressWarnings("unused")
public class NameSpace implements Serializable {
    private String address;
    private String avatar;

    @SerializedName("created_at")
    private Date createdAt;

    private String description;
    private String email;

    @SerializedName("enterprise_id")
    private int enterpriseId;
    private long id;
    private int level;
    private String location;
    private String name;

    @SerializedName("owner_id")
    private long ownerId;

    private String path;

    @SerializedName("updated_at")
    private Date updatedDate;

    private String url;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
