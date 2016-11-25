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
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.tweet.contract.TweetPublishContract;
import net.oschina.app.improve.tweet.fragments.TweetPublishFragment;
import net.oschina.app.improve.tweet.service.TweetPublishService;
import net.oschina.app.util.UIHelper;

import java.util.ArrayList;

public class TweetPublishActivity extends BaseBackActivity {
    private static final String TAG = "TweetPublishActivity";
    private TweetPublishContract.View mView;

    public static void show(Context context) {
        show(context, null);
    }

    public static void show(Context context, View view) {
        show(context, view, null);
    }

    public static void show(Context context, View view, String defaultContent) {
        int[] location = new int[]{0, 0};
        int[] size = new int[]{0, 0};

        if (view != null) {
            view.getLocationOnScreen(location);
            size[0] = view.getWidth();
            size[1] = view.getHeight();
        }

        show(context, location, size, defaultContent);
    }


    public static void show(Context context, @Size(2) int[] viewLocationOnScreen, @Size(2) int[] viewSize, String defaultContent) {
        // Check login before show
        if (!AccountHelper.isLogin()) {
            UIHelper.showLoginActivity(context);
            return;
        }

        Intent intent = new Intent(context, TweetPublishActivity.class);

        if (viewLocationOnScreen != null) {
            intent.putExtra("location", viewLocationOnScreen);
        }
        if (viewSize != null) {
            intent.putExtra("size", viewSize);
        }
        if (defaultContent != null) {
            intent.putExtra("defaultContent", defaultContent);
        }

        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        // hide the software
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return R.layout.activity_tweet_publish;
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "unchecked"})
    @Override
    protected void initWidget() {
        super.initWidget();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();

        String type = intent.getType();
        Bundle bundle = intent.getExtras();
        ArrayList<String> uris = new ArrayList<>();
        if ("text/plain".equals(type)) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            bundle.putString("defaultContent", text);
        } else if ("image/*".equals(type)) {

//            Parcelable obj = intent.getParcelableExtra(Intent.EXTRA_STREAM);
//            if (obj instanceof Uri) {
//                Uri uri = (Uri) obj;
//
//                Log.e(TAG, "initWidget: ----->" + uri.toString() + " " + uri.getEncodedUserInfo());
//
//                Uri tempUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                String[] progection = {MediaStore.Images.Media.DATA};
//                String selection = MediaStore.Images.Media._ID + "=?";
//                String[] selectionArgs = {};
//
//                Cursor cursor = getContentResolver().query(tempUri, progection, selection, selectionArgs, null);
//                try {
//                    while (cursor != null && cursor.moveToNext()) {
//
//                        String dataPath = cursor.getString(1);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (cursor != null && !cursor.isClosed())
//                        cursor.close();
//                }
//
//
//                // uris.add()
//            } else {
//                ArrayList<Uri> list = (ArrayList<Uri>) obj;
//            }

            //ArrayList<Uri> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        }

        // if (uris.size() > 0) {
        //   bundle.putString("defaultContent", "请添加描述");
        // bundle.putStringArrayList("defaultImage", uris);
        // }
        TweetPublishFragment fragment = new TweetPublishFragment();

        // init the args bounds
        fragment.setArguments(bundle);

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