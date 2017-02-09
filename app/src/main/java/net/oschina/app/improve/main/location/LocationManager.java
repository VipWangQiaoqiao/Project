package net.oschina.app.improve.main.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchManager;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.Setting;

/**
 * Created by jzz
 * on 2017/2/8.
 * desc:
 */

public class LocationManager {

    private Context mContext;
    private RadarSearchManager mRadarSearchManager;
    private RadarSearchAdapter mRadarSearchAdapter;
    private LocationClient mLocationClient;

    public LocationManager(Context context) {
        this.mContext = context;
    }

    public void requestLocationPermission() {

    }

    /**
     * start auto lbs service
     */
    private void startLbs() {
        if (mRadarSearchManager == null || mLocationClient == null) {
            initLbs();
        }
        //进行定位
        mLocationClient.start();
    }

    /**
     * init lbs service
     */
    private void initLbs() {
        if (mRadarSearchManager == null) {
            mRadarSearchManager = RadarSearchManager.getInstance();
            mRadarSearchManager.addNearbyInfoListener(this.mRadarSearchAdapter = new RadarSearchAdapter() {
                @Override
                public void onGetUploadState(RadarSearchError radarSearchError) {
                    super.onGetUploadState(radarSearchError);
                    switch (radarSearchError) {
                        case RADAR_NETWORK_ERROR:
                        case RADAR_NETWORK_TIMEOUT:
                            AppContext.showToastShort(R.string.request_error_hint);
                            break;
                        case RADAR_NO_ERROR:
                            Setting.updateLocationInfo(mContext, true);
                            break;
                        case RADAR_PERMISSION_UNFINISHED:

                            break;
                    }

                }
            });
        }

        if (mLocationClient == null) {
            mLocationClient = new LocationClient(mContext);
            mLocationClient.registerLocationListener(new BDLocationAdapter() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    super.onReceiveLocation(bdLocation);
                    //ReceiveLocation(bdLocation);
                }
            });
        }

        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");

        //根据网络情况和gps进行准确定位，7s定位一次 当获取到真实有效的经纬度时，主动关闭定位功能
        option.setScanSpan(7 * 1000);

        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(false);

        //设置是否需要位置语义化结果
        option.setIsNeedLocationDescribe(false);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        //option.setIgnoreKillProcess(false);

        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);

        mLocationClient.setLocOption(option);
    }


}
