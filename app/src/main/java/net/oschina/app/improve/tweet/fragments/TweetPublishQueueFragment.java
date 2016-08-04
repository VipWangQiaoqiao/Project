package net.oschina.app.improve.tweet.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.tweet.adapter.TweetQueueAdapter;
import net.oschina.app.improve.tweet.service.TweetPublishCache;
import net.oschina.app.improve.tweet.service.TweetPublishModel;
import net.oschina.app.improve.tweet.service.TweetPublishService;

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

    @Override
    protected void initData() {
        super.initData();
        if (mBundle == null)
            return;
        String[] ids = mBundle.getStringArray(TweetPublishService.EXTRA_IDS);
        if (ids != null && ids.length > 0) {
            TweetPublishModel model = TweetPublishCache.get(getActivity().getApplicationContext(), ids[0]);
            mAdapter.add(model);
        }
    }

    @Override
    public void onClickContinue(TweetPublishModel model) {

    }

    @Override
    public void onClickDelete(TweetPublishModel model) {

    }
}
