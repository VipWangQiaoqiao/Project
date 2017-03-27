package net.oschina.app.improve.bean.simple;

import java.io.Serializable;

/**
 * Created by huanghaibin_dev
 * on 2016/7/18.
 */
public class Author implements Serializable {
    protected long id;
    protected String name;
    protected String portrait;
    protected int relation;
    protected int gender;
    private Identity identity;

    public Author() {
        relation = 4;
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

    public int getRelation() {
        return relation;
    }

    public void setRelation(int relation) {
        this.relation = relation;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                ", relation=" + relation +
                ", gender=" + gender +
                ", identity=" + identity +
                '}';
    }

    public static class Identity implements Serializable {
        public boolean officialMember;
        public boolean softwareAuthor;
    }
}
