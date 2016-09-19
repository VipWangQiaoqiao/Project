package net.oschina.app.improve.bean;

import net.oschina.app.improve.bean.simple.Author;

import java.io.Serializable;

/**
 * Created by thanatos on 16/8/31.
 */

public class UserV2 extends Author {

    private int gender;
    private String desc;
    private int relation;
    private String suffix; // 个性后缀
    private More more;
    private Statistics statistics;


    public static class More implements Serializable {
        private String joinDate;
        private String city;
        private String expertise;
        private String platform;

        public String getJoinDate() {
            return joinDate;
        }

        public void setJoinDate(String joinDate) {
            this.joinDate = joinDate;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getExpertise() {
            return expertise;
        }

        public void setExpertise(String expertise) {
            this.expertise = expertise;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        @Override
        public String toString() {
            return "More{" +
                    "joinDate='" + joinDate + '\'' +
                    ", city='" + city + '\'' +
                    ", expertise='" + expertise + '\'' +
                    ", platform='" + platform + '\'' +
                    '}';
        }
    }

    public static class Statistics implements Serializable {
        private int score;
        private int tweet;
        private int collect;
        private int fans;
        private int follow;
        private int blog;
        private int answer;
        private int discuss;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getTweet() {
            return tweet;
        }

        public void setTweet(int tweet) {
            this.tweet = tweet;
        }

        public int getCollect() {
            return collect;
        }

        public void setCollect(int collect) {
            this.collect = collect;
        }

        public int getFans() {
            return fans;
        }

        public void setFans(int fans) {
            this.fans = fans;
        }

        public int getFollow() {
            return follow;
        }

        public void setFollow(int follow) {
            this.follow = follow;
        }

        public int getBlog() {
            return blog;
        }

        public void setBlog(int blog) {
            this.blog = blog;
        }

        public int getAnswer() {
            return answer;
        }

        public void setAnswer(int answer) {
            this.answer = answer;
        }

        public int getDiscuss() {
            return discuss;
        }

        public void setDiscuss(int discuss) {
            this.discuss = discuss;
        }

        @Override
        public String toString() {
            return "Statistics{" +
                    "score=" + score +
                    ", tweet=" + tweet +
                    ", collect=" + collect +
                    ", fans=" + fans +
                    ", follow=" + follow +
                    ", blog=" + blog +
                    ", answer=" + answer +
                    ", discuss=" + discuss +
                    '}';
        }
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public More getMore() {
        return more;
    }

    public void setMore(More more) {
        this.more = more;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String toString() {
        return "UserV2{" +
                "gender=" + gender +
                ", desc='" + desc + '\'' +
                ", relation=" + relation +
                ", suffix='" + suffix + '\'' +
                ", more=" + more +
                ", statistics=" + statistics +
                '}';
    }
}
