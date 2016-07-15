package net.oschina.app.improve.media;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import net.oschina.app.R;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.media.adapter.ImageFolderAdapter;

/**
 * Created by huanghaibin_dev
 * on 2016/7/14.
 */

public class ImageFolderPopupWindow extends PopupWindow {
    private ImageFolderAdapter adapter;
    private RecyclerView rv_folder;

    public ImageFolderPopupWindow(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.popup_window_folder, null), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rv_folder = (RecyclerView) getContentView().findViewById(R.id.rv_popup_folder);
        rv_folder.setLayoutManager(new LinearLayoutManager(context));
        setOutsideTouchable(true);
        setFocusable(true);
    }

    public void setAdapter(ImageFolderAdapter adapter) {
        this.adapter = adapter;
        rv_folder.setAdapter(adapter);
    }

    public void setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener listener) {
        adapter.setOnItemClickListener(listener);
    }


}
