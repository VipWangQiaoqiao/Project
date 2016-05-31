package net.oschina.app.bean.base;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public class ResultBean<T> {
    private T result;
    private int code;
    private String message;
    private String time;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

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

    public boolean isSuccess() {
        return code == 1 && result != null;
    }
}
