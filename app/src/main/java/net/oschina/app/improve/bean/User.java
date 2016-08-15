package net.oschina.app.improve.bean;

import java.io.Serializable;

/**
 * Created by thanatos on 16/8/15.
 */
public class User implements Serializable{

    public static final int RELATION_TYPE_BOTH = 0x01;// 双方互为粉丝
    public static final int RELATION_TYPE_ONLY_FANS_HIM = 0x02;// 你单方面关注他
    public static final int RELATION_TYPE_ONLY_FANS_ME = 0x03;// 只有他关注我
    public static final int RELATION_TYPE_NULL = 0x04;// 互不关注

    private long id;
    private String name;
    private String portrait;
    private String desc;
    private int score;
    private int gender;
    private int fansCount;
    private int followCount;
    private int tweetCount;
    private int blogCount;
    private int answerCount;
    private int discussCount;
    private int relation;

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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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

    public int getTweetCount() {
        return tweetCount;
    }

    public void setTweetCount(int tweetCount) {
        this.tweetCount = tweetCount;
    }

    public int getBlogCount() {
        return blogCount;
    }

    public void setBlogCount(int blogCount) {
        this.blogCount = blogCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getDiscussCount() {
        return discussCount;
    }

    public void setDiscussCount(int discussCount) {
        this.discussCount = discussCount;
    }

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }
}
