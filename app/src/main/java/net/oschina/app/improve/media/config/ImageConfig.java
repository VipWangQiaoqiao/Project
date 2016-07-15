package net.oschina.app.improve.media.config;


import net.oschina.app.improve.media.bean.Image;

import java.util.List;

/**
 * 图片选择配置器,可自由扩展选择模式
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */
@SuppressWarnings("All")
public class ImageConfig {

    private int selectCount;
    private SelectMode selectMode;
    private MediaMode mediaMode;
    private List<Image> selectedImage;
    private ImageLoaderListener loaderListener;
    private SelectedCallBack callBack;

    private ImageConfig() {

    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public SelectMode getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(SelectMode selectMode) {
        this.selectMode = selectMode;
    }

    public MediaMode getMediaMode() {
        return mediaMode;
    }

    public void setMediaMode(MediaMode mediaMode) {
        this.mediaMode = mediaMode;
    }

    public List<Image> getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(List<Image> selectedImage) {
        this.selectedImage = selectedImage;
    }

    public ImageLoaderListener getLoaderListener() {
        return loaderListener;
    }

    public void setLoaderListener(ImageLoaderListener loaderListener) {
        this.loaderListener = loaderListener;
    }

    public SelectedCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(SelectedCallBack callBack) {
        this.callBack = callBack;
    }

    public static ImageConfig Build() {
        ImageConfig config = new ImageConfig();
        // config.selectMode(SelectMode.SINGLE_MODE);
        config.mediaMode = MediaMode.ONLY_IMAGE_MODE;
        return config;
    }

    public ImageConfig selectCount(int count) {
        this.selectCount = count;
        return this;
    }

    public ImageConfig selectMode(SelectMode mode) {
        this.selectMode = mode;
        return this;
    }

    public ImageConfig mediaMode(MediaMode mode) {
        this.mediaMode = mode;
        return this;
    }

    public ImageConfig loaderListener(ImageLoaderListener listener) {
        this.loaderListener = listener;
        return this;
    }

    public ImageConfig selectedImages(List<Image> images) {
        this.selectedImage = images;
        return this;
    }

    public ImageConfig callBack(SelectedCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    public enum SelectMode {
        SINGLE_MODE,//单选
        MULTI_MODE//多选
    }

    public enum MediaMode {
        ONLY_IMAGE_MODE,//只有图片
        HAVE_CAM_MODE//带相机
    }
}
