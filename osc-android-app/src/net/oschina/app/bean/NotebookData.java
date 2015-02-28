package net.oschina.app.bean;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("sticky")
public class NotebookData extends Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    @XStreamAlias("id")
    private int id;
    @XStreamAlias("timestamp")
    private long unixTime;
    @XStreamAlias("updateTime")
    private String date;
    @XStreamAlias("content")
    private String content;
    @XStreamAlias("color")
    private String colorText;

    private boolean star;
    private int color;

    private boolean checked; // view需要，非交互数据需要

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long time) {
        this.unixTime = time;
    }

    public String getColorText() {
        return colorText;
    }

    public void setColorText(String color) {
        this.colorText = color;
    }

    public int getColor() {
        if (this.color == 0) { // 客户端始终以当前手机上的颜色为准
            if ("blue".equals(colorText)) {
                this.color = 3;
            } else if ("red".equals(colorText)) {
                this.color = 2;
            } else if ("yellow".equals(colorText)) {
                this.color = 1;
            } else if ("purple".equals(colorText)) {
                this.color = 4;
            } else if ("green".equals(colorText)) {
                this.color = 0;
            }
        }
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}