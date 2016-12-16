package net.oschina.app.improve.bean;

import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Author;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class SubBean implements Serializable {
    private String cacheKey;

    private long id;
    private String title;
    private String body;
    private String pubDate;
    private String href;
    private int type;
    private boolean favorite;
    private String summary;
    private Author author;
    private Image image;
    private HashMap<String, Object> extra;
    private String[] tags;
    private Statistics statistics;
    private ArrayList<About> abouts;
    private Software software;
    public boolean isOriginal() {
        if (tags != null) {
            for (String tag : tags) {
                if ("original".equalsIgnoreCase(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAD() {
        if (tags != null) {
            for (String tag : tags) {
                if ("ad".equalsIgnoreCase(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isStick() {
        if (tags != null) {
            for (String tag : tags) {
                if ("stick".equalsIgnoreCase(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRecommend() {
        if (tags != null) {
            for (String tag : tags) {
                if ("recommend".equalsIgnoreCase(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public HashMap<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(HashMap<String, Object> extra) {
        this.extra = extra;
    }

    public ArrayList<About> getAbouts() {
        return abouts;
    }

    public void setAbouts(ArrayList<About> abouts) {
        this.abouts = abouts;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public String getKey() {
        if (cacheKey == null)
            cacheKey = String.format("t:%s,id:%s", getType(), getId() == 0 ? getHref().hashCode() : getId());
        return cacheKey;
    }

    public static class Image implements Serializable {
        private int type;
        private String[] href;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String[] getHref() {
            return href;
        }

        public void setHref(String[] href) {
            this.href = href;
        }
    }

    public static class Statistics implements Serializable {
        private int comment;
        private int view;

        public int getComment() {
            return comment;
        }

        public void setComment(int comment) {
            this.comment = comment;
        }

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }
    }
}
