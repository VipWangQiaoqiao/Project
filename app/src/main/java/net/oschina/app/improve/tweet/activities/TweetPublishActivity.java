package net.oschina.app.improve.tweet.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;

public class TweetPublishActivity extends BaseBackActivity {

    private static final int MAX_TEXT_LENGTH = 160;
    private static final int SELECT_FRIENDS_REEQUEST_CODE = 100;
    private static final String TEXT_SOFTWARE = "#请输入软件名#";

    public static final String ACTION_TYPE = "action_type";
    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    public static final int ACTION_TYPE_RECORD = 2; // 录音
    public static final int ACTION_TYPE_TOPIC = 3; // 话题
    public static final int ACTION_TYPE_REPOST = 4; // 转发

    public static final String REPOST_IMAGE_KEY = "repost_image";
    public static final String REPOST_TEXT_KEY = "tweet_topic";

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

        Fragment fragment = new TweetPublishFragment();
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.activity_tweet_publish, fragment);
        trans.commitAllowingStateLoss();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tweet_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_send) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}