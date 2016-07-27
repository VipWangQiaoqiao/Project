package net.oschina.app.improve.media;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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
    private ImageFolderAdapter mAdapter;
    private RecyclerView mFolderView;

    public ImageFolderPopupWindow(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.popup_window_folder, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View content = getContentView();
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mFolderView = (RecyclerView) content.findViewById(R.id.rv_popup_folder);
        mFolderView.setLayoutManager(new LinearLayoutManager(context));
        setOutsideTouchable(true);
        setFocusable(true);
    }

    public void setAdapter(ImageFolderAdapter adapter) {
        this.mAdapter = adapter;
        mFolderView.setAdapter(adapter);
    }

    public void setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }
}
