package net.oschina.app.improve.detail.general;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.db.DBManager;
import net.oschina.app.improve.detail.v2.DetailActivity;
import net.oschina.app.improve.detail.v2.DetailFragment;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class BlogDetailActivity extends DetailActivity {
    public static void show(Context context, SubBean bean) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(News.TYPE_BLOG);
        bean.setId(id);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id, boolean isFav) {
        Intent intent = new Intent(context, BlogDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(News.TYPE_BLOG);
        bean.setId(id);
        bean.setFavorite(isFav);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected DetailFragment getDetailFragment() {
        return BlogDetailFragment.newInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && mBehavior != null) {
            mBehavior.setIsComment(1);
            DBManager.from(getApplicationContext())
                    .where("operate_time=?", String.valueOf(mBehavior.getOperateTime()))
                    .update(mBehavior);
        }
    }
}
