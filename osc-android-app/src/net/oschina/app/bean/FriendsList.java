package net.oschina.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 好友实体类
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年11月6日 上午11:17:36
 * 
 */
@SuppressWarnings("serial")
@XStreamAlias("oschina")
public class FriendsList extends Entity implements ListEntity {

    public final static int TYPE_FANS = 0x00;
    public final static int TYPE_FOLLOWER = 0x01;

    @XStreamAlias("friends")
    private List<Friend> friendlist = new ArrayList<Friend>();

    /**
     * 好友实体类
     * 
     * @author FireAnt（http://my.oschina.net/LittleDY）
     * @created 2014年11月6日 上午11:37:31
     * 
     */
    @XStreamAlias("friend")
    public static class Friend implements Serializable {
        @XStreamAlias("userid")
        private int userid;
        @XStreamAlias("name")
        private String name;
        @XStreamAlias("portrait")
        private String portrait;
        @XStreamAlias("expertise")
        private String expertise;
        @XStreamAlias("gender")
        private int gender;

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
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

        public String getExpertise() {
            return expertise;
        }

        public void setExpertise(String expertise) {
            this.expertise = expertise;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }
    }

    public List<Friend> getFriendlist() {
        return friendlist;
    }

    public void setFriendlist(List<Friend> resultlist) {
        this.friendlist = resultlist;
    }

    @Override
    public List<?> getList() {
        return friendlist;
    }
}
