package net.oschina.app.improve.detail.general;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.v2.DetailActivity;
import net.oschina.app.improve.detail.v2.DetailFragment;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class SoftwareDetailActivity extends DetailActivity {

    public static void show(Context context, SubBean bean) {
        Intent intent = new Intent(context, SoftwareDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, String ident) {
        Intent intent = new Intent(context, SoftwareDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(1);
        bundle.putSerializable("sub_bean", bean);
        bundle.putString("ident", ident);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    public static void show(Context context, long id) {
        Intent intent = new Intent(context, SoftwareDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setId(id);
        bean.setType(1);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id, boolean isFav) {
        Intent intent = new Intent(context, SoftwareDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setId(id);
        bean.setType(1);
        bean.setFavorite(isFav);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected DetailFragment getDetailFragment() {
        return SoftwareDetailFragment.newInstance();
    }
}
