package net.oschina.app.improve.tweet.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TweetPublishActivity extends BaseBackActivity implements TweetPublishContract.Operator {
    private final static String SHARE_FILE_NAME = TweetPublishActivity.class.getName();
    private final static String SHARE_VALUES_CONTENT = "content";
    private final static String SHARE_VALUES_IMAGES = "images";

    private TweetPublishContract.View mView;

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
        trans.commitAllowingStateLoss();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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

        final String content = mView.getContent();
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) {
            AppContext.showToastShort(R.string.tip_content_empty);
            return;
        }

        if (content.length() > TweetPublishFragment.MAX_TEXT_LENGTH) {
            AppContext.showToastShort(R.string.tip_content_too_long);
            return;
        }

        final List<String> paths = mView.getImages();

        TweetPublishService.startActionPublish(this, content, paths);
        finish();
    }

    @Override
    public void onBack() {
        onBackPressed();
    }


    @Override
    public void setDataView(TweetPublishContract.View view) {
        mView = view;
    }

    @Override
    protected void onDestroy() {
        saveXmlData();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final String content = mView.getContent();
        final List<String> paths = mView.getImages();
        if (content != null)
            outState.putString(SHARE_VALUES_CONTENT, content);
        if (paths != null && paths.size() > 0)
            outState.putStringArrayList(SHARE_VALUES_IMAGES, (ArrayList<String>) paths);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String content = savedInstanceState.getString(SHARE_VALUES_CONTENT, null);
        ArrayList<String> images = savedInstanceState.getStringArrayList(SHARE_VALUES_IMAGES);
        if (content != null) {
            mView.setContent(content);
        }
        if (images != null && images.size() > 0) {
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
            mView.setImages(new ArrayList<>(set));
        }
    }

    private void saveXmlData() {
        final String content = mView.getContent();
        final List<String> paths = mView.getImages();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARE_VALUES_CONTENT, content);
        if (paths != null && paths.size() > 0) {
            Set<String> set = new HashSet<>(paths);
            editor.putStringSet(SHARE_VALUES_IMAGES, set);
        } else {
            editor.putStringSet(SHARE_VALUES_IMAGES, null);
        }
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }


}