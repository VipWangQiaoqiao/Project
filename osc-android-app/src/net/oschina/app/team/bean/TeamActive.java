package net.oschina.app.team.bean;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Team模块的动态JavaBean
 * 
 * @author kymjs
 * 
 */
@SuppressWarnings("serial")
@XStreamAlias("active")
public class TeamActive implements Serializable {
    @XStreamAlias("id")
    private String id;
    @XStreamAlias("body")
    private Body body;
    @XStreamAlias("reply")
    private String reply;
    @XStreamAlias("createTime")
    private String createTime;
    @XStreamAlias("author")
    private Author author;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @XStreamAlias("body")
    public class Body {
        @XStreamAlias("detail")
        private String detail;
        @XStreamAlias("code")
        private String code;
        @XStreamAlias("codeType")
        private String codeType;
        @XStreamAlias("image")
        private String image;
        @XStreamAlias("imageOrigin")
        private String imageOrigin;

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCodeType() {
            return codeType;
        }

        public void setCodeType(String codeType) {
            this.codeType = codeType;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getImageOrigin() {
            return imageOrigin;
        }

        public void setImageOrigin(String imageOrigin) {
            this.imageOrigin = imageOrigin;
        }
    }

    @XStreamAlias("author")
    public class Author {
        @XStreamAlias("id")
        private String id;
        @XStreamAlias("name")
        private String name;
        @XStreamAlias("portrait")
        private String portrait;

        public String getId() {
            return id;
        }

        public void setId(String id) {
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
    }
}
