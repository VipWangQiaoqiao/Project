package net.oschina.app.improve.git.bean;

import java.io.Serializable;

/**
 * 代码仓库
 * Created by haibin
 * on 2016/12/9.
 */
@SuppressWarnings("unused")
public class Tree implements Serializable {

    private static final String CODE_TYPE_FOLDER = "tree";//文件夹
    private static final String CODE_TYPE_FILE = "blob";//文件

    private String name;
    private String type;
    private String id;
    private String mode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isFile() {
        return CODE_TYPE_FILE.equals(type);
    }
}
