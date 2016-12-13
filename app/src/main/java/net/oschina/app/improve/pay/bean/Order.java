package net.oschina.app.improve.pay.bean;

import java.io.Serializable;

/**
 * Created by thanatos on 16/10/11.
 */

public class Order implements Serializable {

    public static final String TYPE_ALIPAY = "alipay"; // 支付宝支付
    public static final String TYPE_WEPAY = "wepay"; // 微信支付

    private long objType;
    private long objId;
    private float money;
    private String attach;
    private String payInfo;
    private String message;
    private String pubDate;

    public long getObjType() {
        return objType;
    }

    public void setObjType(long objType) {
        this.objType = objType;
    }

    public long getObjId() {
        return objId;
    }

    public void setObjId(long objId) {
        this.objId = objId;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
}
