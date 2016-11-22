package net.oschina.app.improve.bean;

import java.io.Serializable;

/**
 * Created by qiujuer
 * on 2016/11/22.
 * <p>
 * 主要值
 */
public class PrimaryBean implements Serializable {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
