package net.oschina.app.improve.account.bean;

import java.io.Serializable;

/**
 * Created by fei
 * on 2016/10/26.
 * desc:
 */

public class PhoneToken implements Serializable {

    private String phone;
    private String token;
    private String expireDate;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public String toString() {
        return "PhoneToken{" +
                "phone='" + phone + '\'' +
                ", token='" + token + '\'' +
                ", expireDate='" + expireDate + '\'' +
                '}';
    }
}
