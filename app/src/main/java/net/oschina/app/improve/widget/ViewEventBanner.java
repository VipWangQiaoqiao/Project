package net.oschina.app.improve.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.Banner;
import net.oschina.app.improve.detail.general.EventDetailActivity;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public class ViewEventBanner extends RelativeLayout implements View.OnClickListener {
    private Banner banner;
    private ImageView mImageEnent;

    public ViewEventBanner(Context context) {
        super(context, null);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_event_banner, this, true);
        mImageEnent = (ImageView) findViewById(R.id.iv_event);
        setOnClickListener(this);
    }

    public void initData(RequestManager manager, Banner banner) {
        this.banner = banner;
        manager.load(banner.getImg())
                .placeholder(R.mipmap.event_cover_default)
                .error(R.mipmap.event_cover_default)
                .into(mImageEnent);
    }

    @Override
    public void onClick(View v) {
        if (banner != null)
            EventDetailActivity.show(getContext(), banner.getId());
    }
}
