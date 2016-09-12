package net.oschina.app.improve.bean.simple;

import java.io.Serializable;

/**
 * 来源：消息：AT我
 * Created by huanghaibin_dev
 * on 2016/8/16.
 */
public class Origin implements Serializable {
    public static final int ORIGIN_TYPE_LINK = 0;          // 链接新闻
    public static final int ORIGIN_TYPE_SOFTWARE = 1;      // 软件推荐
    public static final int ORIGIN_TYPE_DISCUSS = 2;       // 讨论区帖子
    public static final int ORIGIN_TYPE_BLOG = 3;          // 博客
    public static final int ORIGIN_TYPE_TRANSLATION = 4;   // 翻译文章
    public static final int ORIGIN_TYPE_ACTIVE = 5;        // 活动类型
    public static final int ORIGIN_TYPE_NEWS = 6;          // 资讯类型
    public static final int ORIGIN_TYPE_TWEETS = 100;        // 动弹

    private long id;
    private String desc;
    private String href;
    private int type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
