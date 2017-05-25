package net.oschina.app.improve.detail.db;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 用户习惯收集字段
 * Created by haibin on 2017/5/22.
 */
@SuppressWarnings("all")
@Table(tableName = "behavior")
public class Behavior implements Serializable {

    @PrimaryKey(autoincrement = true, column = "id")
    @SerializedName("index")
    private int id;

    /**
     * 设备唯一编码
     */
    @Column(column = "uuid")
    private String uuid;

    /**
     * 文章链接
     */
    @Column(column = "url", isNotNull = true)
    private String url;

    /**
     * 用户id
     */
    @Column(column = "user", isNotNull = true)
    private long user;

    /**
     * 用户名
     */
    @SerializedName("user_name")
    @Column(column = "user_name", isNotNull = true)
    private String userName;

    /**
     *
     */
    @Column(column = "operation", isNotNull = true)
    private String operation;

    /**
     * 操作时间
     */
    @SerializedName("operate_time")
    @Column(column = "operate_time", isNotNull = true)
    private long operateTime;

    /**
     * 文章类型
     */
    @SerializedName("operate_type")
    @Column(column = "operate_type", isNotNull = true)
    private int operateType;

    /**
     * 是否评论
     */
    @SerializedName("is_comment")
    @Column(column = "is_comment")
    private int isComment;

    /**
     * 是否赞
     */
    @SerializedName("is_voteup")
    @Column(column = "is_voteup")
    private int isVoteup;

    /**
     * 是否收藏
     */
    @SerializedName("is_collect")
    @Column(column = "is_collect")
    private int isCollect;

    /**
     * 是否分享
     */
    @SerializedName("is_share")
    @Column(column = "is_share")
    private int isShare;

    /**
     * 网络
     */
    @Column(column = "network")
    private String network;

    /**
     * 阅读时间
     */
    @Column(column = "stay")
    private long stay;

    /**
     * 地理位置
     */
    @Column(column = "location")
    private String location;

    /**
     * 设备，Android、IOS
     */
    @Column(column = "device", isNotNull = true)
    private String device;

    /**
     * 操作系统版本 MEIZU PRO6s 6.0.1
     */
    @Column(column = "os", isNotNull = true)
    private String os;

    /**
     * app版本号
     */
    @Column(column = "version", isNotNull = true)
    private String version;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(long operateTime) {
        this.operateTime = operateTime;
    }

    public int getIsComment() {
        return isComment;
    }

    public void setIsComment(int isComment) {
        this.isComment = isComment;
    }

    public int getIsVoteup() {
        return isVoteup;
    }

    public void setIsVoteup(int isVoteup) {
        this.isVoteup = isVoteup;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public int getIsShare() {
        return isShare;
    }

    public void setIsShare(int isShare) {
        this.isShare = isShare;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public long getStay() {
        return stay;
    }

    public void setStay(long stay) {
        this.stay = stay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
