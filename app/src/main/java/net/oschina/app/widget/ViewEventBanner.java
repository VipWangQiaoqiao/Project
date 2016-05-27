package net.oschina.app.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;

import net.oschina.app.R;
import net.oschina.app.bean.Banner;

/**
 * Created by huanghaibin
 * on 16-5-23.
 */
public class ViewEventBanner extends RelativeLayout implements View.OnClickListener {
    private Banner banner;
    private ImageView iv_event_banner_img,iv_event_banner_bg;
    private TextView tv_event_banner_title,tv_event_banner_body,tv_event_pub_date;

    public ViewEventBanner(Context context) {
        super(context, null);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_event_banner, this, true);
        iv_event_banner_img = (ImageView) findViewById(R.id.iv_event_banner_img);
        iv_event_banner_bg = (ImageView) findViewById(R.id.iv_event_banner_bg);
        tv_event_banner_title = (TextView) findViewById(R.id.tv_event_banner_title);
        tv_event_banner_body = (TextView) findViewById(R.id.tv_event_banner_body);
        tv_event_pub_date = (TextView) findViewById(R.id.tv_event_pub_date);
        setOnClickListener(this);
    }

    public void initData(RequestManager manager, Banner banner) {
        this.banner = banner;
        tv_event_banner_title.setText(banner.getName());
        tv_event_banner_body.setText(banner.getDetail());
        tv_event_pub_date.setText(banner.getPubDate());
        manager.load(banner.getImg()).into(iv_event_banner_img);
        manager.load(banner.getImg()).into(iv_event_banner_bg);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getContext(),banner.getName(),Toast.LENGTH_LONG).show();
    }
}
