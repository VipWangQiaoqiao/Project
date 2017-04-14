package net.oschina.app.improve.tweet.contract;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.bean.simple.About;
import net.oschina.app.improve.tweet.activities.TweetTopicActivity;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
import net.oschina.app.improve.tweet.service.TweetNotificationManager;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.UIHelper;
import net.oschina.common.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JuQiu
 * on 16/8/22.
 */
public class TweetPublishOperator implements TweetPublishContract.Operator {
    private final static String SHARE_FILE_NAME = TweetPublishFragment.class.getName();
    private final static String SHARE_VALUES_CONTENT = "content";
    private final static String SHARE_VALUES_IMAGES = "images";
    private final static String SHARE_VALUES_ABOUT = "about";
    private final static String DEFAULT_PRE = "default";
    private final static String SHARE_LOCAL_IMAGE = "share_image";
    private TweetPublishContract.View mView;
    private String mDefaultContent;
    private String[] mDefaultImages;
    private About.Share mAboutShare;
    private String mLocalImg;

    @Override
    public void setDataView(TweetPublishContract.View view, String defaultContent, String[] defaultImages, About.Share share, String localImg) {
        mView = view;
        mDefaultContent = defaultContent;
        mDefaultImages = defaultImages;
        mAboutShare = share;
        mLocalImg = localImg;
    }

    @Override
    public void publish() {
        final Context context = mView.getContext();
        if (context == null)
            return;

        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AccountHelper.isLogin()) {
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

        // Check con't commit to tweet
        if (About.check(mAboutShare) && mAboutShare.commitTweetId > 0 && !mView.needCommit()) {
            mAboutShare.commitTweetId = 0;
        }

        TweetNotificationManager.setup(context);

        final List<String> paths = CollectionUtil.toArrayList(mView.getImages());

        // To service publish
        content = content.replaceAll("[\n\\s]+", " ");
        TweetPublishService.startActionPublish(context, content, paths, mAboutShare);

        // Toast
        AppContext.showToastShort(R.string.tweet_publishing);

        // Save topic cache
        Pattern pattern = Pattern.compile("#.+?#");
        Matcher matcher = pattern.matcher(content);
        List<String> topics = new ArrayList<>();
        while (matcher.find()) {
            String str = matcher.group().trim()
                    .replace("#", "");
            topics.add(str);
        }
        if (topics.size() > 0) {
            TweetTopicActivity.saveCache(context, CollectionUtil.toArray(topics, String.class));
        }

        // clear the tweet data
        clearAndFinish(context);
    }

    @Override
    public void onBack() {
        saveXmlData();
        mView.finish();
    }

    @Override
    public void loadData() {
        if (isUseXmlCache()) {
            final Context context = mView.getContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
            String content = sharedPreferences.getString(SHARE_VALUES_CONTENT, null);
            Set<String> set = sharedPreferences.getStringSet(SHARE_VALUES_IMAGES, null);
            if (content != null) {
                mView.setContent(content, false);
            }
            if (set != null && set.size() > 0) {
                mView.setImages(CollectionUtil.toArray(set, String.class));
            }
        } else {
            if (!TextUtils.isEmpty(mLocalImg)) {
                mView.setImages(new String[]{mLocalImg});
                return;
            }
            if (mDefaultImages != null && mDefaultImages.length > 0)
                mView.setImages(mDefaultImages);

            boolean haveAbout = false;
            if (About.check(mAboutShare)) {
                mView.setAbout(mAboutShare, mAboutShare.commitTweetId > 0);
                haveAbout = true;
            }

            if (!TextUtils.isEmpty(mDefaultContent))
                mView.setContent(mDefaultContent, !haveAbout);
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
        // save default
        if (mDefaultContent != null) {
            outState.putString(DEFAULT_PRE + SHARE_VALUES_CONTENT, mDefaultContent);
        }
        if (mDefaultImages != null && mDefaultImages.length > 0) {
            outState.putStringArray(DEFAULT_PRE + SHARE_VALUES_IMAGES, mDefaultImages);
        }
        if (About.check(mAboutShare)) {
            outState.putSerializable(DEFAULT_PRE + SHARE_VALUES_ABOUT, mAboutShare);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        String content = savedInstanceState.getString(SHARE_VALUES_CONTENT, null);
        String[] images = savedInstanceState.getStringArray(SHARE_VALUES_IMAGES);
        if (content != null) {
            mView.setContent(content, false);
        }
        if (images != null && images.length > 0) {
            mView.setImages(images);
        }
        // Read default
        mDefaultContent = savedInstanceState.getString(DEFAULT_PRE + SHARE_VALUES_CONTENT, null);
        mDefaultImages = savedInstanceState.getStringArray(DEFAULT_PRE + SHARE_VALUES_IMAGES);
        mAboutShare = (About.Share) savedInstanceState.getSerializable(DEFAULT_PRE + SHARE_VALUES_ABOUT);
        if (About.check(mAboutShare))
            mView.setAbout(mAboutShare, mAboutShare.commitTweetId > 0);
    }

    private void clearAndFinish(Context context) {
        if (isUseXmlCache()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_FILE_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHARE_VALUES_CONTENT, null);
            editor.putStringSet(SHARE_VALUES_IMAGES, null);
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
        mView.finish();
    }


    private void saveXmlData() {
        if (isUseXmlCache()) {
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

    private boolean isUseXmlCache() {
        if (!TextUtils.isEmpty(mLocalImg))
            return false;
        return TextUtils.isEmpty(mDefaultContent)
                && (mDefaultImages == null || mDefaultImages.length == 0)
                && (!About.check(mAboutShare));
    }
}
