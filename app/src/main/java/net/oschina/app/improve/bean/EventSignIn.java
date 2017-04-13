package net.oschina.app.improve.bean;

import java.io.Serializable;

/**
 * Created by fei
 * on 2016/12/1.
 * desc: 活动签到bean
 */

public class EventSignIn implements Serializable {

    private int optStatus;  //1: 成功 2: 活动进行中未报名 3: 活动已结束／活动报名已截止 4: 已签到
    private String message; //1.您是开源软件作者，本次活动免费～ 2.需缴纳50元！（金额由活动决定）3.未报名，不可签到 4.活动已结束／活动报名已经截止 5已签到
    private int cost; //缴纳金额（单位分）
    private String href; //分享推广的链接
    private String costMessage;

    public int getOptStatus() {
        return optStatus;
    }

    public void setOptStatus(int optStatus) {
        this.optStatus = optStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getCostMessage() {
        return costMessage;
    }

    public void setCostMessage(String costMessage) {
        this.costMessage = costMessage;
    }

    @Override
    public String toString() {
        return "EventSignIn{" +
                "optStatus=" + optStatus +
                ", message='" + message + '\'' +
                ", cost=" + cost +
                ", href='" + href + '\'' +
                '}';
    }
}
