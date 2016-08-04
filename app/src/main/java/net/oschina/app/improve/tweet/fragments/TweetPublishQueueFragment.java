package net.oschina.app.improve.tweet.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.tweet.adapter.TweetQueueAdapter;
import net.oschina.app.improve.tweet.service.TweetPublishCache;
import net.oschina.app.improve.tweet.service.TweetPublishModel;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.qiujuer.genius.ui.widget.Loading;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 发布动弹队列实现
 */
@SuppressWarnings("WeakerAccess")
public class TweetPublishQueueFragment extends BaseFragment implements TweetQueueAdapter.Callback, View.OnClickListener {
    @Bind(R.id.loading)
    Loading mLoading;
    @Bind(R.id.txt_title)
    TextView mTitle;
    @Bind(R.id.recycler)
    RecyclerView mRecycler;
    private TweetQueueAdapter mAdapter;

    public static TweetPublishQueueFragment newInstance(String[] ids) {
        TweetPublishQueueFragment fragment = new TweetPublishQueueFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(TweetPublishService.EXTRA_IDS, ids);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tweet_publish_queue;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new TweetQueueAdapter(this);
        mRecycler.setAdapter(mAdapter);
    }

    private void finish() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
    }

    @Override
    protected void initData() {
        super.initData();
        if (mBundle != null) {
            final String[] ids = mBundle.getStringArray(TweetPublishService.EXTRA_IDS);
            if (ids != null && ids.length > 0) {
                AppOperator.runOnThread(new Runnable() {
                    @Override
                    public void run() {
                        List<TweetPublishModel> models = new ArrayList<TweetPublishModel>();
                        for (String str : ids) {
                            TweetPublishModel model = TweetPublishCache.get(getActivity().getApplicationContext(), str);
                            if (model != null)
                                models.add(model);
                        }
                        if (models.size() > 0)
                            addData(models);
                        else
                            finish();
                    }
                });
                return;
            }
        }
        finish();
    }

    private void addData(final List<TweetPublishModel> models) {
        mRoot.post(new Runnable() {
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
        TweetPublishService.startActionContinue(getContext(), model.getId());
        if (mAdapter.getItemCount() == 0)
            finish();
    }

    @Override
    public void onClickDelete(TweetPublishModel model) {
        TweetPublishService.startActionDelete(getContext(), model.getId());
        if (mAdapter.getItemCount() == 0)
            finish();
    }

    @OnClick(R.id.icon_back)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.icon_back) {
            finish();
        }
    }
}
