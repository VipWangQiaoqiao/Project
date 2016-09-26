package net.oschina.app.improve.tweet.contract;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.improve.utils.CollectionUtil;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;

import java.util.List;
import java.util.Set;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class TweetPublishOperator implements TweetPublishContract.Operator {
    private final static String SHARE_FILE_NAME = TweetPublishFragment.class.getName();
    private final static String SHARE_VALUES_CONTENT = "content";
    private final static String SHARE_VALUES_IMAGES = "images";
    private TweetPublishContract.View mView;
    private String mDefaultContent;

    @Override
    public void setDataView(TweetPublishContract.View view, String defaultContent) {
        mView = view;
        mDefaultContent = defaultContent;
    }

    @Override
    public void publish() {
        final Context context = mView.getContext();

        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(context);
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
        TweetPublishService.startActionPublish(context, content, paths);

        // Toast
        AppContext.showToast(R.string.tweet_publishing_toast);

        // clear the tweet data
        clearAndFinish(context);
    }

    @Override
    public void onBack() {
        saveXmlData();
        mView.finish();
    }

    @Override
    public void loadXmlData() {
        if (TextUtils.isEmpty(mDefaultContent)) {
            final Context context = mView.getContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
            String content = sharedPreferences.getString(SHARE_VALUES_CONTENT, null);
            Set<String> set = sharedPreferences.getStringSet(SHARE_VALUES_IMAGES, null);
            if (content != null) {
                mView.setContent(content);
            }
            if (set != null && set.size() > 0) {
                mView.setImages(CollectionUtil.toArray(set, String.class));

            }
        } else {
            mView.setContent(mDefaultContent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        final String content = mView.getContent();
        final String[] paths = mView.getImages();
        if (content != null)
            outState.putString(SHARE_VALUES_CONTENT, content);
        if (paths != null && paths.length > 0)
            outState.putStringArray(SHARE_VALUES_IMAGES, paths);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        String content = savedInstanceState.getString(SHARE_VALUES_CONTENT, null);
        String[] images = savedInstanceState.getStringArray(SHARE_VALUES_IMAGES);
        if (content != null) {
            mView.setContent(content);
        }
        if (images != null && images.length > 0) {
            mView.setImages(images);
        }
    }

    private void clearAndFinish(Context context) {
        if (TextUtils.isEmpty(mDefaultContent)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHARE_VALUES_CONTENT, null);
            editor.putStringSet(SHARE_VALUES_IMAGES, null);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
        mView.finish();
    }


    private void saveXmlData() {
        if (TextUtils.isEmpty(mDefaultContent)) {
            final Context context = mView.getContext();
            final String content = mView.getContent();
            final String[] paths = mView.getImages();
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHARE_VALUES_CONTENT, content);
            if (paths != null && paths.length > 0) {
                editor.putStringSet(SHARE_VALUES_IMAGES, CollectionUtil.toHashSet(paths));
            } else {
                editor.putStringSet(SHARE_VALUES_IMAGES, null);
            }
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
    }


}
