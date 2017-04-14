package net.oschina.app.improve.bean;

/**
 * Created by huanghaibin
 * on 16-6-13.
 */
public class EventDetail extends Event {
    public static final int APPLY_STATUS_UN_SIGN = -1;
    public static final int APPLY_STATUS_AUDIT = 0;
    public static final int APPLY_STATUS_CONFIRMED = 1;
    public static final int APPLY_STATUS_PRESENTED = 2;
    public static final int APPLY_STATUS_CANCELED = 3;
    public static final int APPLY_STATUS_REFUSED = 4;

    private String author;
    private int authorId;
    private String authorPortrait;
    private int commentCount;
    private int viewCount;
    private String spot;
    private String location;
    private String city;
    private String costDesc;
    private boolean favorite;
    private EventRemark remark;
    private int applyStatus;
    private String invitationImg;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorPortrait() {
        return authorPortrait;
    }

    public void setAuthorPortrait(String authorPortrait) {
        this.authorPortrait = authorPortrait;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getSpot() {
        return spot;
    }

    public void setSpot(String spot) {
        this.spot = spot;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCostDesc() {
        return costDesc;
    }

    public void setCostDesc(String costDesc) {
        this.costDesc = costDesc;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public EventRemark getRemark() {
        return remark;
    }

    public void setRemark(EventRemark remark) {
        this.remark = remark;
    }

    public int getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(int applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getInvitationImg() {
        return invitationImg;
    }

    public void setInvitationImg(String invitationImg) {
        this.invitationImg = invitationImg;
    }

   public static class EventRemark {
        private String tip;
        private String select;

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public String getSelect() {
            return select;
        }

        public void setSelect(String select) {
            this.select = select;
        }
    }
}
