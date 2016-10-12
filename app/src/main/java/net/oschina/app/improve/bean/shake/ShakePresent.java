package net.oschina.app.improve.bean.shake;

import java.io.Serializable;

/**
 * Created by haibin
 * on 2016/10/12.
 */

public class ShakePresent implements Serializable {
    private String name;
    private String pic;
    private String href;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
