package net.oschina.app.improve.pay.bean;

import java.io.Serializable;

/**
 * Created by thanatos on 16/10/11.
 */

public class ResultBean<T> implements Serializable {

    private int code;
    private String message;
    private String time;
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
