package net.oschina.app.improve.detail.general;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.oschina.app.R;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.v2.DetailActivity;
import net.oschina.app.improve.detail.v2.DetailFragment;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class EventDetailActivity extends DetailActivity {

    public static void show(Context context, SubBean bean) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected DetailFragment getDetailFragment() {
        return EventDetailFragment.newInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {
            toShare(mBean.getTitle(), mBean.getBody(), mBean.getHref());
        }
        return true;
    }
}
