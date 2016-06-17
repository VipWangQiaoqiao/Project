package net.oschina.app.improve.bean;

import java.util.List;

/**
 * 问答详情bean
 */
public class QuestionDetail extends Question {

    private boolean favorite;
    private String href;
    private List<String> tags;

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

