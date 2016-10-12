package net.oschina.app.improve.main.discover;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.view.View;

import net.oschina.app.improve.base.fragments.BaseFragment;

/**
 * Created by haibin
 * on 2016/10/12.
 */

public abstract class BaseSensorFragment extends BaseFragment implements SensorEventListener {
    protected SensorManager mSensor = null;
    protected Vibrator mVibrator = null;
    public static final int SPEED_SHRESHOLD = 45;// 这个值越大需要越大的力气来摇晃手机
    public static final int UPTATE_INTERVAL_TIME = 50;
    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdateTime;

    protected boolean mLoading;

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mSensor = (SensorManager) getActivity()
                .getSystemService(Context.SENSOR_SERVICE);
        mVibrator = (Vibrator) getActivity().getSystemService(Service.VIBRATOR_SERVICE);
    }

    public void onShake() {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - lastUpdateTime;
        if (timeInterval < UPTATE_INTERVAL_TIME) {
            return;
        }
        lastUpdateTime = currentUpdateTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        lastX = x;
        lastY = y;
        lastZ = z;

        double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ) / timeInterval) * 100;
        if (speed >= SPEED_SHRESHOLD && !mLoading) {
            mLoading = true;
            mVibrator.vibrate(300);
            onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSensor != null) {
            Sensor sensor = mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor != null) {
                mSensor.registerListener(this, sensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSensor != null) {
            mSensor.unregisterListener(this);
        }
    }
}
