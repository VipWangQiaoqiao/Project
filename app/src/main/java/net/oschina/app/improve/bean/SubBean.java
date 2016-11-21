package net.oschina.app.improve.bean;

import net.oschina.app.improve.bean.simple.Author;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class SubBean implements Serializable {
    private long id;
    private String title;
    private String body;
    private String pubDate;
    private String href;
    private int type;
    private Author author;
    private Image image;
    private Map<String, Object> extra;
    private String[] tags;
    private Statistics statistics;

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

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
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
