package net.oschina.app.improve.detail.general;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.bean.Report;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.account.activity.LoginActivity;
import net.oschina.app.improve.bean.News;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.detail.v2.DetailActivity;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.improve.detail.v2.ReportDialog;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class QuestionDetailActivity extends DetailActivity {
    public static void show(Context context, SubBean bean) {
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id) {
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(News.TYPE_QUESTION);
        bean.setId(id);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, long id, boolean isFav) {
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        Bundle bundle = new Bundle();
        SubBean bean = new SubBean();
        bean.setType(News.TYPE_QUESTION);
        bean.setId(id);
        bean.setFavorite(isFav);
        bundle.putSerializable("sub_bean", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        mCommentHint = "我要回答";
        super.initWidget();
    }

    @Override
    protected DetailFragment getDetailFragment() {
        return QuestionDetailFragment.newInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_question_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_comment);
        if (item != null) {
            View action = item.getActionView();
            if (action != null) {
                View tv = action.findViewById(R.id.tv_comment_count);
                if (tv != null) {
                    mCommentCountView = (TextView) tv;
                    if (mBean.getStatistics() != null)
                        mCommentCountView.setText(mBean.getStatistics().getComment() + "");
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_comment:
                break;
            case R.id.menu_report:
                if (!AccountHelper.isLogin()) {
                    LoginActivity.show(this);
                    return false;
                }
                toReport(mBean.getId(), mBean.getHref());
                break;
        }
        return false;
    }

    protected void toReport(long id, String href) {
        ReportDialog.create(this, id, href, Report.TYPE_QUESTION).show();
    }
}
