package net.oschina.app.improve.tweet.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Size;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.tweet.contract.TweetPublishContract;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
import net.oschina.app.improve.tweet.service.TweetPublishService;

public class TweetPublishActivity extends BaseBackActivity implements TweetPublishContract.Host {
    private TweetPublishContract.View mView;
    private int[] mViewLocation;
    private int[] mViewSize;

    public static void show(Context context) {
        show(context, null);
    }

    public static void show(Context context, View view) {
        int[] location = new int[]{0, 0};
        int[] size = new int[]{0, 0};

        if (view != null) {
            view.getLocationOnScreen(location);
            size[0] = view.getWidth();
            size[1] = view.getHeight();
        }

        show(context, location, size);
    }

    public static void show(Context context, @Size(2) int[] viewLocationOnScreen, @Size(2) int[] viewSize) {
        Intent intent = new Intent(context, TweetPublishActivity.class);

        if (viewLocationOnScreen != null) {
            intent.putExtra("location", viewLocationOnScreen);
        }
        if (viewSize != null) {
            intent.putExtra("size", viewSize);
        }

        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        // hide the software
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return R.layout.activity_tweet_publish;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        if (bundle != null) {
            mViewLocation = bundle.getIntArray("location");
            mViewSize = bundle.getIntArray("size");
        }
        if (mViewLocation == null)
            mViewLocation = new int[]{0, 0};
        if (mViewSize == null)
            mViewSize = new int[]{0, 0};

        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TweetPublishFragment fragment = new TweetPublishFragment();
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.activity_tweet_publish, fragment);
        trans.commit();
        mView = fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        // before the fragment show
        registerPublishStateReceiver();
    }

    @Override
    protected void onDestroy() {
        unRegisterPublishStateReceiver();
        super.onDestroy();
    }

    private void registerPublishStateReceiver() {
        if (mPublishStateReceiver != null)
            return;
        IntentFilter intentFilter = new IntentFilter(TweetPublishService.ACTION_RECEIVER_SEARCH_FAILED);
        BroadcastReceiver receiver = new SearchReceiver();
        registerReceiver(receiver, intentFilter);
        mPublishStateReceiver = receiver;

        // start search
        TweetPublishService.startActionSearchFailed(this);
    }

    private void unRegisterPublishStateReceiver() {
        final BroadcastReceiver receiver = mPublishStateReceiver;
        mPublishStateReceiver = null;
        if (receiver != null)
            unregisterReceiver(receiver);
    }

    private BroadcastReceiver mPublishStateReceiver;

    @Override
    public int[] getStartLocation() {
        return mViewLocation;
    }

    @Override
    public int[] getStartSize() {
        return mViewSize;
    }

    private class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TweetPublishService.ACTION_RECEIVER_SEARCH_FAILED.equals(intent.getAction())) {
                String[] ids = intent.getStringArrayExtra(TweetPublishService.EXTRA_IDS);
                if (ids == null || ids.length == 0)
                    return;
                TweetPublishQueueActivity.show(TweetPublishActivity.this, ids);
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mView != null) {
            mView.getOperator().onBack();
        }
    }
}