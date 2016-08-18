package net.oschina.app.improve.user.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.oschina.app.R;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.user.fragments.UserTweetFragment;

/**
 * Created by fei on 2016/8/18.
 * desc:
 */

public class UserTweetActivity extends BaseBackActivity {


    private long uid;

    /**
     * show activity
     *
     * @param context context
     * @param uid     uid
     */
    public static void show(Context context, long uid) {
        Intent intent = new Intent(context, UserTweetActivity.class);
        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        uid = bundle.getLong("uid", 0);
        if (uid > 0) {
            return true;
        }
        return super.initBundle(bundle);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_user_tweet;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        UserTweetFragment userTweetFragment = (UserTweetFragment) UserTweetFragment.instantiate(uid);
        getSupportFragmentManager().beginTransaction().add(R.id.user_tweet_container, userTweetFragment).commit();
    }
}
