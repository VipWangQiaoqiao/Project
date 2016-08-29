package net.oschina.app.improve.bean;

import net.oschina.app.improve.bean.simple.Author;

import java.io.Serializable;

/**
 * Created by thanatos on 16/8/15.
 */
public class User extends Author{

    public static final int RELATION_TYPE_BOTH = 0x01;// 双方互为粉丝
    public static final int RELATION_TYPE_ONLY_FANS_HIM = 0x02;// 你单方面关注他
    public static final int RELATION_TYPE_ONLY_FANS_ME = 0x03;// 只有他关注我
    public static final int RELATION_TYPE_NULL = 0x04;// 互不关注

    public static final int GENDER_UNKNOW = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

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

    /**
     *
     * @return 0:未填, 1:male, 2:female
     */
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
