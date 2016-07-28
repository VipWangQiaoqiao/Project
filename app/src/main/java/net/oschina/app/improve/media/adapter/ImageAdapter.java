package net.oschina.app.improve.media.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.media.bean.Image;
import net.oschina.app.improve.media.config.ImageConfig;
import net.oschina.app.improve.media.config.ImageLoaderListener;


/**
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */
public class ImageAdapter extends BaseRecyclerAdapter<Image> {
    private ImageLoaderListener loader;
    private ImageConfig.SelectMode selectMode;

    public ImageAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    public int getItemViewType(int position) {
        Image image = getItem(position);
        if (image.getId() == 0)
            return 0;
        return 1;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        if (type == 0)
            return new CamViewHolder(mInflater.inflate(R.layout.item_list_cam, parent, false));
        return new ImageViewHolder(mInflater.inflate(R.layout.item_list_image, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Image item, int position) {
        if (getItemViewType(position) == 1) {
            ImageViewHolder h = (ImageViewHolder) holder;
            h.mCheckView.setSelected(item.isSelect());
            h.mMaskView.setVisibility(item.isSelect() ? View.VISIBLE : View.GONE);
            h.mCheckView.setVisibility(selectMode == ImageConfig.SelectMode.SINGLE_MODE ? View.GONE : View.VISIBLE);
            if (loader != null) {
                loader.displayImage(h.mImageView, item.getPath());
            }
        }
    }

    private static class CamViewHolder extends RecyclerView.ViewHolder {
        public CamViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setLoader(ImageLoaderListener loader) {
        this.loader = loader;
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        ImageView mCheckView;
        View mMaskView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_image);
            mCheckView = (ImageView) itemView.findViewById(R.id.cb_selected);
            mMaskView = itemView.findViewById(R.id.lay_mask);
        }
    }

    public ImageConfig.SelectMode getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(ImageConfig.SelectMode selectMode) {
        this.selectMode = selectMode;
    }
}
