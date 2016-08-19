package net.oschina.app.improve.user.bean;

import java.io.Serializable;

/**
 * Created by fei on 2016/8/19.
 * desc:   user info module
 */

public class UserInfo implements Serializable {

    private long id;
    private String name;
    private String portrait;
    private int gender;
    private String desc;
    private int score;
    private int tweetCount;
    private int collectCount;
    private int fansCount;
    private int followCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTweetCount() {
        return tweetCount;
    }

    public void setTweetCount(int tweetCount) {
        this.tweetCount = tweetCount;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                ", gender=" + gender +
                ", desc='" + desc + '\'' +
                ", score=" + score +
                ", tweetCount=" + tweetCount +
                ", collectCount=" + collectCount +
                ", fansCount=" + fansCount +
                ", followCount=" + followCount +
                '}';
    }
}
