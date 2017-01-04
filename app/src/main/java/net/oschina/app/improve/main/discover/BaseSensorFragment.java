package net.oschina.app.improve.main.discover;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.common.widget.Loading;

import java.lang.reflect.Type;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;

/**
 * 摇一摇基本逻辑实现
 */
public abstract class BaseSensorFragment<T> extends BaseFragment implements SensorEventListener, View.OnClickListener {
    public static final int UPTATE_INTERVAL_TIME = 50;

    protected SensorManager mSensor = null;
    protected Vibrator mVibrator = null;
    protected int mSpeedThreshold = 45;// 这个值越大需要越大的力气来摇晃手机

    private float mSensorLastX;
    private float mSensorLastY;
    private float mSensorLastZ;
    private long mSensorLastUpdateTime;

    protected boolean mLoading;
    protected boolean mIsRegister;
    protected int mDelayTime = 5;

    protected TextHttpResponseHandler mHandler;
    protected ResultBean<T> mBean;
    protected View mShakeView;

    @Bind(R.id.cv_shake)
    CardView mCardView;

    @Bind(R.id.tv_state)
    TextView mTvState;

    @Bind(R.id.loading)
    Loading mLoadingView;

    @Bind(R.id.tv_time)
    TextView mTxtTime;

    protected Handler mTimeHandler;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mSensor = (SensorManager) getActivity()
                .getSystemService(Context.SENSOR_SERVICE);
        mVibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        mCardView.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mHandler = new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                BaseSensorFragment.this.onRequestStart();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (mContext != null) {
                    BaseSensorFragment.this.onFailure();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (mContext == null)
                    return;
                try {
                    mBean = new GsonBuilder().create().fromJson(responseString, getType());
                    if (mBean != null && mBean.isSuccess()) {
                        BaseSensorFragment.this.onSuccess();
                    } else {
                        onFailure(statusCode, headers, responseString, new Exception());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(statusCode, headers, responseString, e);
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                onTimeProgress();
            }
        };
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTimeHandler = null;
    }

    public void onShake() {

    }


    protected void initShakeView() {

    }

    protected void onTimeProgress() {

    }

    protected void onRequestStart() {
        mTvState.setVisibility(View.VISIBLE);
        mTvState.setText("正在搜寻礼品");
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.start();
    }

    protected void onSuccess() {
        mCardView.removeAllViews();
        MediaPlayer.create(mContext, R.raw.shake).start();
        initShakeView();
        mCardView.addView(mShakeView);
        mCardView.setVisibility(View.VISIBLE);
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(320);
        animation.setFillAfter(true);
        mCardView.startAnimation(animation);

        mLoadingView.stop();
        mLoadingView.setVisibility(View.GONE);
        mTvState.setVisibility(View.GONE);
    }


    protected void onFailure() {
        mTvState.setText("很遗憾，你没有摇到礼品，请再试一次");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - mSensorLastUpdateTime;
        if (timeInterval < UPTATE_INTERVAL_TIME) {
            return;
        }
        mSensorLastUpdateTime = currentUpdateTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - mSensorLastX;
        float deltaY = y - mSensorLastY;
        float deltaZ = z - mSensorLastZ;

        mSensorLastX = x;
        mSensorLastY = y;
        mSensorLastZ = z;

        double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ) / timeInterval) * 100;
        if (speed >= mSpeedThreshold && !mLoading) {
            mLoading = true;
            mVibrator.vibrate(300);
            onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void registerSensor() {
        if (mSensor != null && !mIsRegister) {
            Sensor sensor = mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor != null) {
                mIsRegister = true;
                mSensor.registerListener(this, sensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    public void unregisterSensor() {
        if (mSensor != null && mIsRegister) {
            mIsRegister = false;
            mSensor.unregisterListener(this);
        }
    }

    protected Type getType() {
        return null;
    }
}
