package net.oschina.app.improve.bean;

import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.bean.simple.Author;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haibin
 * on 2016/10/26.
 */

public class SubBean implements Serializable {
    private static final long serialVersionUID = -5343222344464021662L;
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
    private List<Image> images;
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
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
        private String href;
        private String thumb;
        private int w;
        private int h;
        private String type;
        private String name;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Statistics implements Serializable {
        private int comment;
        private int view;
        private int like;
        private int transmit;
        private int favCount;

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

        public int getLike() {
            return like;
        }

        public void setLike(int like) {
            this.like = like;
        }

        public int getTransmit() {
            return transmit;
        }

        public void setTransmit(int transmit) {
            this.transmit = transmit;
        }

        public int getFavCount() {
            return favCount;
        }

        public void setFavCount(int favCount) {
            this.favCount = favCount;
        }
    }
}
