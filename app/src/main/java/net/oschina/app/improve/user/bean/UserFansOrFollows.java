package net.oschina.app.improve.user.bean;

import net.oschina.app.improve.bean.simple.Author;

import java.io.Serializable;

/**
 * Created by fei on 2016/8/24.
 * desc:
 */

public class UserFansOrFollows extends Author implements Serializable {
    private String desc;
    private More more;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
