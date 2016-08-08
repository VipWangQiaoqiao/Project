package net.oschina.app.improve.tweet.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;
import android.view.WindowManager;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.tweet.contract.TweetPublishContract;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
import net.oschina.app.improve.tweet.fragments.TweetPublishQueueFragment;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.utils.CollectionUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.util.List;
import java.util.Set;

public class TweetPublishActivity extends BaseBackActivity implements TweetPublishContract.Operator {
    private final static String SHARE_FILE_NAME = TweetPublishActivity.class.getName();
    private final static String SHARE_VALUES_CONTENT = "content";
    private final static String SHARE_VALUES_IMAGES = "images";

    private TweetPublishContract.View mView;

    private boolean mSaveOnDestroy = true;

    public static void show(Context context) {
        Intent intent = new Intent(context, TweetPublishActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_tweet_publish;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        return super.initBundle(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        TweetPublishFragment fragment = new TweetPublishFragment();
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.activity_tweet_publish, fragment);
        trans.commitNow();
    }

    @Override
    public void publish() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(this);
            return;
        }

        String content = mView.getContent();
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) {
            AppContext.showToastShort(R.string.tip_content_empty);
            return;
        }

        if (content.length() > TweetPublishFragment.MAX_TEXT_LENGTH) {
            AppContext.showToastShort(R.string.tip_content_too_long);
            return;
        }

        final List<String> paths = CollectionUtil.toArrayList(mView.getImages());

        // To service publish
        content = content.replaceAll("[\n\\s]+", " ");
        TweetPublishService.startActionPublish(this, content, paths);

        // Toast
        AppContext.showToast(R.string.tweet_publishing_toast);

        // clear the tweet data
        clearAndFinish();
    }

    @Override
    public void onBack() {
        onSupportNavigateUp();
    }


    @Override
    public void setDataView(TweetPublishContract.View view) {
        mView = view;
        // before the fragment show
        registerPublishStateReceiver();
    }

    @Override
    protected void onDestroy() {
        if (mSaveOnDestroy) {
            saveXmlData();
        }
        unRegisterPublishStateReceiver();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final String content = mView.getContent();
        final String[] paths = mView.getImages();
        if (content != null)
            outState.putString(SHARE_VALUES_CONTENT, content);
        if (paths != null && paths.length > 0)
            outState.putStringArray(SHARE_VALUES_IMAGES, paths);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String content = savedInstanceState.getString(SHARE_VALUES_CONTENT, null);
        String[] images = savedInstanceState.getStringArray(SHARE_VALUES_IMAGES);
        if (content != null) {
            mView.setContent(content);
        }
        if (images != null && images.length > 0) {
            mView.setImages(images);
        }
    }

    @Override
    public void loadXmlData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
        String content = sharedPreferences.getString(SHARE_VALUES_CONTENT, null);
        Set<String> set = sharedPreferences.getStringSet(SHARE_VALUES_IMAGES, null);
        if (content != null) {
            mView.setContent(content);
        }
        if (set != null && set.size() > 0) {
            mView.setImages(CollectionUtil.toArray(set, String.class));

        }
        // hide the software
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void saveXmlData() {
        final String content = mView.getContent();
        final String[] paths = mView.getImages();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARE_VALUES_CONTENT, content);
        if (paths != null && paths.length > 0) {
            editor.putStringSet(SHARE_VALUES_IMAGES, CollectionUtil.toHashSet(paths));
        } else {
            editor.putStringSet(SHARE_VALUES_IMAGES, null);
        }
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    private void clearAndFinish() {
        mSaveOnDestroy = false;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARE_VALUES_CONTENT, null);
        editor.putStringSet(SHARE_VALUES_IMAGES, null);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        finish();
    }

    private void registerPublishStateReceiver() {
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

    private class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TweetPublishService.ACTION_RECEIVER_SEARCH_FAILED.equals(intent.getAction())) {
                String[] ids = intent.getStringArrayExtra(TweetPublishService.EXTRA_IDS);
                if (ids == null || ids.length == 0)
                    return;
                TweetPublishQueueFragment fragment = TweetPublishQueueFragment.newInstance(ids);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_tweet_publish, fragment)
                        .addToBackStack(TweetPublishQueueFragment.class.toString())
                        .commit();
            }
        }
    }
}