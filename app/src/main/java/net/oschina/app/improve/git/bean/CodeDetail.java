package net.oschina.app.improve.git.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 代码详情
 * Created by haibin
 * on 2016/12/9.
 */
@SuppressWarnings("unused")
public class CodeDetail implements Serializable {

    public static final String  ENCODING_BASE64 = "base64";

    @SerializedName("file_name")
    private String fileName;

    @SerializedName("file_path")
    private String filePath;

    private int size;

    private String encoding;

    private String content;

    private String ref;

    @SerializedName("blob_id")
    private String blobId;

    @SerializedName("commit_id")
    private String commitId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
