package net.oschina.app.improve.tweet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.tweet.adapter.TweetQueueAdapter;
import net.oschina.app.improve.tweet.service.TweetPublishCache;
import net.oschina.app.improve.tweet.service.TweetPublishModel;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.common.widget.Loading;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class TweetPublishQueueActivity extends BaseBackActivity implements TweetQueueAdapter.Callback, View.OnClickListener {
    @Bind(R.id.loading)
    Loading mLoading;
    @Bind(R.id.txt_title)
    TextView mTitle;
    @Bind(R.id.recycler)
    RecyclerView mRecycler;
    private TweetQueueAdapter mAdapter;

    public static void show(Context context, String[] ids) {
        Intent intent = new Intent(context, TweetPublishQueueActivity.class);
        intent.putExtra(TweetPublishService.EXTRA_IDS, ids);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_tweet_publish_queue;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TweetQueueAdapter(this);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            final String[] ids = bundle.getStringArray(TweetPublishService.EXTRA_IDS);
            if (ids != null && ids.length > 0) {
                AppOperator.runOnThread(new Runnable() {
                    @Override
                    public void run() {
                        Context context = getApplicationContext();
                        List<TweetPublishModel> models = new ArrayList<>();
                        for (String str : ids) {
                            TweetPublishModel model = TweetPublishCache.get(context, str);
                            if (model != null)
                                models.add(model);
                        }
                        if (models.size() > 0)
                            addData(models);
                        else
                            finish();
                    }
                });
                return true;
            }
        }
        return false;
    }

    private void addData(final List<TweetPublishModel> models) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.add(models);
                mLoading.setVisibility(View.GONE);
                mTitle.setVisibility(View.VISIBLE);
                mRecycler.setVisibility(View.VISIBLE);

                mTitle.setText(String.format("『%s』Todo", models.size()));
            }
        });
    }

    @Override
    public void onClickContinue(TweetPublishModel model) {
        TweetPublishService.startActionContinue(this, model.getId());
        if (mAdapter.getItemCount() == 0)
            finish();
    }

    @Override
    public void onClickDelete(TweetPublishModel model) {
        TweetPublishService.startActionDelete(this, model.getId());
        if (mAdapter.getItemCount() == 0)
            finish();
    }

    @OnClick(R.id.icon_back)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.icon_back) {
            onSupportNavigateUp();
        }
    }
}
