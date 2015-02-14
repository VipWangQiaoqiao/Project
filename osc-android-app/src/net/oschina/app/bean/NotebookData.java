package net.oschina.app.bean;

import java.io.Serializable;

public class NotebookData extends Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private long unixTime;
    private String date;
    private String content;

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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}