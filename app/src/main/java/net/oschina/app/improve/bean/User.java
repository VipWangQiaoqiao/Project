package net.oschina.app.improve.bean;

import java.io.Serializable;

/**
 * 用户信息类
 */
public class User implements Serializable {
    public static final int RELATION_TYPE_BOTH = 0x01;// 双方互为粉丝
    public static final int RELATION_TYPE_ONLY_FANS_HIM = 0x02;// 你单方面关注他
    public static final int RELATION_TYPE_ONLY_FANS_ME = 0x03;// 只有他关注我
    public static final int RELATION_TYPE_NULL = 0x04;// 互不关注

    public static final int GENDER_UNKNOW = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    // Base
    private long id;
    private String name;
    private String portrait;
    // More
    private int gender;
    private String desc;
    private int relation;
    //个性后缀
    private String suffix;
    private More more;
    private Statistics statistics;

    public User() {
        more = new More();
        statistics = new Statistics();
    }

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
        return "User{" + "id=" + id +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                "gender=" + gender +
                ", desc='" + desc + '\'' +
                ", relation=" + relation +
                ", suffix='" + suffix + '\'' +
                ", more=" + more +
                ", statistics=" + statistics +
                '}';
    }


    public static class More implements Serializable {
        private String joinDate;
        private String city;
        private String expertise;
        private String platform;
        private String company;
        private String position;

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

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        @Override
        public String toString() {
            return "More{" +
                    "joinDate='" + joinDate + '\'' +
                    ", city='" + city + '\'' +
                    ", expertise='" + expertise + '\'' +
                    ", platform='" + platform + '\'' +
                    ", company='" + company + '\'' +
                    ", position='" + position + '\'' +
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
}
