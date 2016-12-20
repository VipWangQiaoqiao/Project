package net.oschina.app.improve.detail.general;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.v2.DetailActivity;
import net.oschina.app.improve.detail.v2.DetailFragment;

import butterknife.Bind;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class EventDetailActivity extends DetailActivity {
    private MenuItem mMenuFav;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.iv_event)
    ImageView mImageEvent;

    @Bind(R.id.header_view)
    View mHeaderView;

    public static void show(Context context, SubBean bean) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_event_detail_v2;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
        setSupportActionBar(mToolbar);
    }

    @Override
    protected DetailFragment getDetailFragment() {
        return EventDetailFragment.newInstance();
    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        mHeaderView.getLayoutParams().height = 400;
        String[] href;
        SubBean.Image image = mBean.getImage();
        if (image == null)
            return;
        href = image.getHref();
        if (href == null)
            return;
        getImageLoader().load(href.length > 0 ? href[0] : "").into(mImageEvent);
        mImageEvent.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        mMenuFav = menu.getItem(0);
        if (mBean.isFavorite())
            mMenuFav.setIcon(R.mipmap.icon_record);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                break;
            case R.id.menu_fav:
                break;
        }
        if (item.getItemId() == R.id.menu_share) {
            toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
        }
        return true;
    }
}
