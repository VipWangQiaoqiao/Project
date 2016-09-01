package net.oschina.app.improve.media.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.media.bean.ImageFolder;
import net.oschina.app.improve.media.config.ImageLoaderListener;


/**
 * Created by huanghaibin_dev
 * on 2016/7/13.
 * <p>
 * Changed by qiujuer
 * on 2016/09/01
 */

public class ImageFolderAdapter extends BaseRecyclerAdapter<ImageFolder> {
    private ImageLoaderListener loader;

    public ImageFolderAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new FolderViewHolder(mInflater.inflate(R.layout.item_list_folder, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, ImageFolder item, int position) {
        FolderViewHolder h = (FolderViewHolder) holder;
        h.tv_name.setText(item.getName());
        h.tv_size.setText(String.format("(%s)", item.getImages().size()));
        if (loader != null) {
            loader.displayImage(h.iv_image, item.getAlbumPath());
        }
    }

    public void setLoader(ImageLoaderListener loader) {
        this.loader = loader;
    }

    private static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_name, tv_size;

        public FolderViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_folder);
            tv_name = (TextView) itemView.findViewById(R.id.tv_folder_name);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }
}
