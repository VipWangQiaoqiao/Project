package net.oschina.app.improve.tweet.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.tweet.adapter.TweetQueueAdapter;
import net.oschina.app.improve.tweet.service.TweetPublishCache;
import net.oschina.app.improve.tweet.service.TweetPublishModel;
import net.oschina.app.improve.tweet.service.TweetPublishService;

import java.util.ArrayList;
import java.util.List;

/**
 * 发布动弹队列实现
 */
@SuppressWarnings("WeakerAccess")
public class TweetPublishQueueFragment extends BaseFragment implements TweetQueueAdapter.Callback {

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

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new TweetQueueAdapter(this);
        recyclerView.setAdapter(mAdapter);
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
                        addData(models);
                    }
                });
                return;
            }
        }
        finish();
    }

    private void addData(final List<TweetPublishModel> models) {
        if (models.size() == 0) {
            finish();
        } else {
            mRoot.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(models);
                }
            });
        }
    }

    @Override
    public void onClickContinue(TweetPublishModel model) {

    }

    @Override
    public void onClickDelete(TweetPublishModel model) {

    }
}
