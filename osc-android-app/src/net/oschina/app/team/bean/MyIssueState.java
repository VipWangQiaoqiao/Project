package net.oschina.app.team.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 我的任务中要显示的状态
 * 
 * @author kymjs (https://github.com/kymjs)
 * 
 */
@XStreamAlias("oschina")
public class MyIssueState {
    @XStreamAlias("opened")
    private String opened;
    @XStreamAlias("outdate")
    private String outdate;
    @XStreamAlias("closed")
    private String closed;
    @XStreamAlias("all")
    private String all;

    public String getOpened() {
        return opened;
    }

    public void setOpened(String opened) {
        this.opened = opened;
    }

    public String getOutdate() {
        return outdate;
    }

    public void setOutdate(String outdate) {
        this.outdate = outdate;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }
}
