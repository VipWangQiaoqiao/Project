package net.oschina.app.improve.tweet.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.tweet.contract.TweetPublishContract;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
import net.oschina.app.improve.tweet.service.TweetPublishService;

import org.kymjs.kjframe.bitmap.BitmapCreate;
import org.kymjs.kjframe.utils.FileUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TweetPublishActivity extends BaseBackActivity {
    public static final String ACTION_TYPE = "action_type";
    public static final int ACTION_TYPE_ALBUM = 0;
    public static final int ACTION_TYPE_PHOTO = 1;
    public static final int ACTION_TYPE_RECORD = 2; // 录音
    public static final int ACTION_TYPE_TOPIC = 3; // 话题
    public static final int ACTION_TYPE_REPOST = 4; // 转发

    public static final String REPOST_IMAGE_KEY = "repost_image";
    public static final String REPOST_TEXT_KEY = "tweet_topic";

    private TweetPublishContract.View mView;

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

        mView = fragment;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
            publish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void publish() {
        String content = mView.getContent();
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) {
            Toast.makeText(this, "动弹内容不能为空~", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> paths = mView.getImagePath();

        TweetPublishService.startActionPublish(this, content, paths);
    }


}