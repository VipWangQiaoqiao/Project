package net.oschina.app.improve.base.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * Created by JuQiu
 * on 16/6/20.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected RequestManager mImageLoader;
    private boolean mIsDestroy;
    //private final String packageName4Umeng = "ImprovedBaseActivity";
    private final String packageName4Umeng = this.getClass().getName();
    private Context mContext4Umeng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (initBundle(getIntent().getExtras())) {
            setContentView(getContentView());

            initWindow();

            ButterKnife.bind(this);
            initWidget();
            initData();
        } else {
            finish();
        }

        //umeng analytics
        MobclickAgent.setDebugMode(false);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.packageName4Umeng);
        MobclickAgent.onResume(this.mContext4Umeng);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageStart(this.packageName4Umeng);
        MobclickAgent.onResume(this.mContext4Umeng);
    }

    protected abstract int getContentView();

    protected boolean initBundle(Bundle bundle) {
        return true;
    }

    protected void initWindow() {
    }

    protected void initWidget() {
    }

    protected void initData() {
    }

    public synchronized RequestManager getImageLoader() {
        if (mImageLoader == null)
            mImageLoader = Glide.with(this);
        return mImageLoader;
    }

    @Override
    protected void onDestroy() {
        mIsDestroy = true;
        super.onDestroy();
    }

    public boolean isDestroy() {
        return mIsDestroy;
    }
}
