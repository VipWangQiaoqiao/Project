package net.oschina.app.improve.git.gist.detail;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.git.bean.Gist;
import net.oschina.app.ui.empty.EmptyLayout;

import butterknife.Bind;

/**
 * 代码片段详情
 * Created by haibin on 2017/5/10.
 */

public class GistDetailActivity extends BaseBackActivity implements GistDetailContract.EmptyView {
    private GistDetailPresenter mPresenter;
    @Bind(R.id.emptyLayout)
    EmptyLayout mEmptyLayout;

    public static void show(Context context, Gist gist) {
        Intent intent = new Intent(context, GistDetailActivity.class);
        intent.putExtra("gist", gist);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gist_detail;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        final Gist gist = (Gist)
                getIntent().getSerializableExtra("gist");
        GistDetailFragment fragment = GistDetailFragment.newInstance(gist);
        mPresenter = new GistDetailPresenter(fragment, this);
        addFragment(R.id.fl_content, fragment);
        mPresenter.getGistDetail(gist.getId());
        mPresenter.getCommentCount(gist.getId());
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyLayout.getErrorState() != EmptyLayout.NETWORK_LOADING) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    mPresenter.getGistDetail(gist.getId());
                }
            }
        });
    }

    @Override
    public void showGetDetailSuccess(int strId) {
        mEmptyLayout.setErrorType(strId);
    }

    @Override
    public void showGetDetailFailure(int strId) {
        mEmptyLayout.setErrorType(strId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActionBar bar = getSupportActionBar();
            if(bar != null)
                bar.hide();
            mPresenter.changeConfig(true);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPresenter.changeConfig(false);
            ActionBar bar = getSupportActionBar();
            if(bar != null)
                bar.show();
        }
    }
}
