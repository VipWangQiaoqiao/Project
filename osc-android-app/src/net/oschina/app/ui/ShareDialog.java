package net.oschina.app.ui;

import net.oschina.app.R;
import net.oschina.app.ui.dialog.CommonDialog;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * 分享界面dialog
 * 
 * @author kymjs
 * 
 */
public class ShareDialog extends CommonDialog implements
        android.view.View.OnClickListener {

    public interface OnSharePlatformClick {
        void onPlatformClick(int id);
    }

    private OnSharePlatformClick mListener;

    private ShareDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    @SuppressLint("InflateParams")
    private ShareDialog(Context context, int defStyle) {
        super(context, defStyle);
        View shareView = getLayoutInflater().inflate(
                R.layout.dialog_cotent_share, null);
        shareView.findViewById(R.id.ly_share_qq).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_copy_link)
                .setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_more_option).setOnClickListener(
                this);
        shareView.findViewById(R.id.ly_share_sina_weibo).setOnClickListener(
                this);
        shareView.findViewById(R.id.ly_share_weichat).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_weichat_circle)
                .setOnClickListener(this);
        setContent(shareView, 0);
    }

    public ShareDialog(Context context) {
        this(context, R.style.dialog_bottom);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    public void setOnPlatformClickListener(OnSharePlatformClick lis) {
        mListener = lis;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (mListener != null) {
            mListener.onPlatformClick(id);
        }
    }
}
