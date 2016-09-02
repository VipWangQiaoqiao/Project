package net.oschina.app.improve.user.bean;

import java.io.Serializable;

/**
 * Created by fei on 2016/8/24.
 * desc:
 */

public class UserFansOrFollows implements Serializable {
    private long id;
    private String name;
    private String portrait;//0 未知 1 男 2 女
    private int gender;
    private String desc;
    private int relation;
    private More more;

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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
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

    @Override
    public String toString() {
        return "UserFansOrFollows{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                ", gender=" + gender +
                ", desc='" + desc + '\'' +
                ", relation=" + relation +
                ", more=" + more +
                '}';
    }
}
