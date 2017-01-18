package net.oschina.app.improve.search.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.Setting;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.base.activities.BaseBackActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.bean.NearbyResult;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.search.adapters.NearbyUserAdapter;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.BottomDialog;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.app.util.TDevice;
import net.oschina.app.util.TLog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 寻找附近的人
 * Created by thanatosx
 * on 2016/12/22.
 * Updated bt fei
 * on 2017/01/13
 */

public class NearbyActivity extends BaseBackActivity implements RadarSearchListener, BDLocationListener,
        RecyclerRefreshLayout.SuperRefreshLayoutListener, BaseRecyclerAdapter.OnItemClickListener,
        View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "NearbyActivity";

    @Bind(R.id.recycler)
    RecyclerView mRecycler;
    @Bind(R.id.layout_recycler_refresh)
    RecyclerRefreshLayout mRecyclerRefresh;

    @Bind(R.id.lay_emptyLayout)
    EmptyLayout mEmptyLayout;

    private int mPageNum = 0;
    private LatLng mUserLatLng;
    private BottomDialog mSelectorDialog;
    private BaseRecyclerAdapter<NearbyResult> mAdapter;
    private LocationClient mLocationClient;
    private RadarSearchManager mManager = null;

    private boolean isFirstLocation = true;

    private static final int LOCATION_PERMISSION = 0x01;//定位权限
    private LocationManager mLocationManager;

    public static void show(Context context) {
        Intent intent = new Intent(context, NearbyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_nearby;
    }

    @Override
    protected void initWidget() {
        mEmptyLayout.setLoadingLocalFriend(true);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmptyLayout emptyLayout = mEmptyLayout;
                if (emptyLayout != null && emptyLayout.getErrorState() != EmptyLayout.HIDE_LAYOUT) {
                    onRefreshing();
                }
            }
        });

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerRefresh.setSuperRefreshLayoutListener(this);
        mRecyclerRefresh.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        mRecycler.setAdapter(mAdapter = new NearbyUserAdapter(this));
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        requestLocationPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nearby_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_more:
                getSelectorDialog().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ----------->");
        if (isEnabledLocation()) {
            startLocationClient();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
        Log.e(TAG, "onDestroy: ---->");
    }


    /**
     * 附近的人的监听
     *
     * @param result Radar Nearby Result
     * @param error  Radar Search Error
     */
    @Override
    public void onGetNearbyInfoList(RadarNearbyResult result, RadarSearchError error) {
        updateView(result, error);
    }

    /**
     * 上传用户信息的监听
     *
     * @param error Radar Search Error
     */
    @Override
    public void onGetUploadState(RadarSearchError error) {
        if (error != RadarSearchError.RADAR_NO_ERROR) {
            showError(EmptyLayout.NETWORK_ERROR);
            SimplexToast.show(this, "上传用户信息失败");
            return;
        }
        if (isFirstLocation) {
            Setting.updateLocationInfo(getApplicationContext(), true);
            onRefreshing();
        }
    }

    /**
     * 清除用户信息的监听
     *
     * @param error Radar Search Error
     */
    @Override
    public void onGetClearInfoState(RadarSearchError error) {
        if (error != RadarSearchError.RADAR_NO_ERROR) {
            TLog.i("oschina", "On Clear Radar Search Info State: ERROR!!");
            SimplexToast.show(this, "清除失败");
            return;
        }
        Setting.updateLocationInfo(getApplicationContext(), false);
        supportFinishAfterTransition();
    }

    /**
     * 定位回调
     *
     * @param location location
     */
    @Override
    public void onReceiveLocation(BDLocation location) {
        ReceiveLocation(location);
    }

    @Override
    public void onRefreshing() {
        mPageNum = 0;
        Log.e(TAG, "onRefreshing: ------->" + mPageNum);
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        requestData(mPageNum);
    }

    @Override
    public void onLoadMore() {
        ++mPageNum;
        Log.e(TAG, "onLoadMore: ----->" + mPageNum);
        requestData(mPageNum);
    }


    @Override
    public void onItemClick(int position, long itemId) {
        NearbyResult result = mAdapter.getItem(position);
        if (result == null) return;
        OtherUserHomeActivity.show(this, result.getUser());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear_opt:
                //清除用户信息
                mManager.clearUserInfo();
                Setting.updateLocationInfo(getApplicationContext(), false);
                if (mSelectorDialog.isShowing())
                    mSelectorDialog.cancel();
                break;
            case R.id.tv_cancel_opt:
                if (mSelectorDialog.isShowing())
                    mSelectorDialog.cancel();
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.e(TAG, "onPermissionsGranted:  权限申请成功---->");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        Log.e(TAG, "onPermissionsDenied: --------> 授权被拒绝");

        ShowDialog();

        boolean denied = EasyPermissions.checkDeniedPermissionsNeverAskAgain(this, "我要获取定位权限", R.string.btn_ok, R.string.cancel, perms);

        Log.e(TAG, "onPermissionsDenied: ----------------------->" + denied);
    }

    private void updateView(RadarNearbyResult result, RadarSearchError error) {
        mRecyclerRefresh.onComplete();

        Log.e(TAG, "onGetNearbyInfoList: ------->获取附近的人列表-------   " + (result.infoList == null ? null : result.infoList.size()) + "  totolNum=" + result.totalNum + " pageNum=" + result.pageNum + " pageIndex=" + result.pageIndex);

        if (mPageNum == 0) {
            mAdapter.clear();
        }

        List<RadarNearbyInfo> infoList = result.infoList;

        int size;

        if (infoList == null || error != RadarSearchError.RADAR_NO_ERROR) {
            //当第一次请求的时候，没有数据的话，直接noData
            if (mPageNum == 0) {
                showError(EmptyLayout.NODATA);
                mEmptyLayout.setNoDataContent("没有获取到附近的人");
                AppContext.showToastShort("没有获取到附近的人...");
            } else {
                //当不是第一次请求数据时，但是没有数据返回不做处理。
                AppContext.showToastShort("没有获取到更多附近的人...");
            }
        } else {
            //不是第一次请求，有数据情况，所以进行数据的添加

            List<NearbyResult> results = mAdapter.getItems();

            for (RadarNearbyInfo info : infoList) {

                User user = null;
                try {
                    String comments = URLDecoder.decode(info.comments, "UTF-8");
                    TLog.i("oschina", "Nearby Info List: " + comments);
                    user = AppOperator.createGson().fromJson(comments, User.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (user == null || (user.getId() == 0 && TextUtils.isEmpty(user.getName())))
                    continue;

                NearbyResult.Nearby nearby = new NearbyResult.Nearby();
                nearby.setDistance(info.distance);
                nearby.setMobileName(info.mobileName);
                nearby.setMobileOS(info.mobileOS);

                results.add(new NearbyResult(user, nearby));
                TLog.i("oschina", String.format("comments: %s, distance: %s, mobile name: %s, mobile OS: %s, user id: %s",
                        info.comments, info.distance, info.mobileName, info.mobileOS, info.userID));
            }

            //根据数据的距离从近到远进行排序
            Collections.sort(results);
            //刷新数据，初始化有效数据ui
            mAdapter.notifyDataSetChanged();
            size = infoList.size();
            mAdapter.setState(size < 20 ? BaseRecyclerAdapter.STATE_NO_MORE : BaseRecyclerAdapter.STATE_LOAD_MORE, size >= 20);
            //隐藏emptyView
            hideLoading();
        }
    }

    private void ShowDialog() {
        DialogHelper.getConfirmDialog(this, "温馨提示", "定位权限已被禁用，需要开启定位权限", "去开启", "取消", false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
            }
        }, null).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e(TAG, "onRequestPermissionsResult: -----> 授权代理");
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(LOCATION_PERMISSION)
    private void requestLocationPermission() {
        if (isEnabledLocation()) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.e(TAG, "requestLocationPermission: ------》授权成功。。。。。");
                startLocationClient();
            } else {
                Log.e(TAG, "requestLocationPermission: -----2-->未授权，所以申请定位授权");
                EasyPermissions.requestPermissions(this, "hello", LOCATION_PERMISSION,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
    }

    private void ReceiveLocation(BDLocation location) {
        final int code = location.getLocType();
        Log.e(TAG, "onReceiveLocation: ------>" + code);
        switch (code) {
            case 62:
                showError(EmptyLayout.NODATA);
                SimplexToast.show(this, "无法定位");
                mLocationClient.requestLocation();
                mRecyclerRefresh.setOnLoading(false);
                return;
            case 63:
                showError(EmptyLayout.NETWORK_ERROR);
                mRecyclerRefresh.setOnLoading(false);
                SimplexToast.show(this, "网络异常");
                return;
            case 167:

                if (isEnabledLocation()) {
                    ShowDialog();
                }

                showError(EmptyLayout.NODATA);
                Log.e(TAG, "onReceiveLocation: ------->未授权，所以申请定位授权");
                mRecyclerRefresh.setOnLoading(false);
                SimplexToast.show(this, "服务器定位失败，请检查权限");
                return;
        }
        if (code >= 501) {
            showError(EmptyLayout.NODATA);
            SimplexToast.show(this, "非法或无效的APP KEY");
            return;
        }

        mUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        Log.e(TAG, "onReceiveLocation: ------定位回调----->latitude=" + location.getLatitude() + "  longitude=" + location.getLongitude());

        if (!TDevice.hasInternet()) {
            showError(EmptyLayout.NETWORK_ERROR);
            return;
        }

        TLog.i("oschina", String.format("定位成功，latitude: %s, longitude: %s, location describe: %s, user id: %s",
                location.getLatitude(), location.getLongitude(), location.getLocationDescribe(), AccountHelper.getUserId()));

        //周边雷达设置用户身份标识，id为空默认是设备标识
        String userId = null;

        //上传位置
        RadarUploadInfo info = new RadarUploadInfo();

        if (AccountHelper.isLogin()) {
            userId = String.valueOf(AccountHelper.getUserId());

            User user = AccountHelper.getUser();
            try {
                String company = "";
                if (user.getMore() != null) {
                    company = user.getMore().getCompany();
                }
                company = TextUtils.isEmpty(company) ? "" : company;
                String comments = String.format(
                        "{" +
                                "\"id\":\"%s\"," +
                                "\"name\":\"%s\"," +
                                "\"portrait\":\"%s\"," +
                                "\"gender\":\"%s\"," +
                                "\"more\":{" +
                                "\"company\":\"%s\"}" +
                                "}"
                        , user.getId(), user.getName(), user.getPortrait(), user.getGender(), company);
                comments = comments.replaceAll("[\\s\n]+", "");
                comments = URLEncoder.encode(comments, "UTF-8");
                TLog.i("oschina", comments);
                info.comments = comments;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

                SimplexToast.show(this, "上传用户信息失败");
            }
        }

        mManager.setUserID(userId);
        info.pt = mUserLatLng;
        mManager.uploadInfoRequest(info);
    }

    private boolean isEnabledLocation() {

        LocationManager locationManager = this.mLocationManager;
        if (mLocationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            this.mLocationManager = locationManager;
        }

        if (locationManager != null) {

            boolean GpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean NetEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (GpsEnabled || NetEnabled) {
                Log.e(TAG, "requestLocationPermission: -----已开启gps，可以进行定位---然后进行权限判断");
                return true;
            } else {

                Log.e(TAG, "requestLocationPermission: 未开启gps，准备开启gps定位功能");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                setResult(RESULT_OK);
                startActivityForResult(intent, LOCATION_PERMISSION);

                return false;
            }

        } else {
            showError(EmptyLayout.NODATA);
            AppContext.showToastShort("当前手机gps不可用！！！");
            finish();
            Log.e(TAG, "requestLocationPermission:----------> 获取gps功能失败");
            return false;
        }
    }

    private void startLocationClient() {
        if (mManager == null) {
            mManager = RadarSearchManager.getInstance();
            mManager.addNearbyInfoListener(this);
        }
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this);
            mLocationClient.registerLocationListener(this);
        }

        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");

        //仅定位一次  ms
        option.setScanSpan(10 * 1000);

        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        //设置是否需要位置语义化结果
        option.setIsNeedLocationDescribe(true);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);

        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);

        //进行定位
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private void requestData(int pageNum) {

        if (TDevice.hasInternet()) {
            if (mUserLatLng == null) {
                mLocationClient.requestLocation();
                Log.e(TAG, "requestData: ---------->经纬度为null");
            } else {
                if (mUserLatLng.latitude == 4.9E-324 && mUserLatLng.longitude == 4.9E-324) {
                    mLocationClient.requestLocation();
                    Log.e(TAG, "requestData: ------>经纬度有问题，需要重新定位");
                }
            }
            //构造请求参数，其中centerPt是自己的位置坐标
            RadarNearbySearchOption option = new RadarNearbySearchOption()
                    .centerPt(mUserLatLng).pageNum(pageNum).radius(35000).pageCapacity(20);
            //发起查询请求
            mManager.nearbyInfoRequest(option);
            isFirstLocation = false;
            Log.e(TAG, "requestData: ---------->开始发起附近人查询请求");
            if (mUserLatLng != null)
                Log.e(TAG, "requestData: ------->latitude=" + mUserLatLng.latitude + "   longitude=" + mUserLatLng.longitude);
        } else {
            showError(EmptyLayout.NETWORK_ERROR);
        }

    }

    /**
     * hide empty view's loading
     */
    private void hideLoading() {
        final EmptyLayout emptyLayout = mEmptyLayout;
        if (emptyLayout == null)
            return;

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_to_hide);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        emptyLayout.startAnimation(animation);
    }

    /**
     * show empty view's error type
     *
     * @param type type
     */
    private void showError(int type) {
        EmptyLayout layout = mEmptyLayout;
        if (layout != null) {
            layout.setErrorType(type);
        }
    }

    private Dialog getSelectorDialog() {
        if (mSelectorDialog == null) {
            mSelectorDialog = new BottomDialog(this, true);
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.view_nearby_operator, null, false);
            view.findViewById(R.id.tv_clear_opt).setOnClickListener(this);
            view.findViewById(R.id.tv_cancel_opt).setOnClickListener(this);
            mSelectorDialog.setContentView(view);
        }
        return mSelectorDialog;
    }

    private void release() {
        if (mLocationClient.isStarted())
            mLocationClient.stop();
        //移除监听
        mManager.removeNearbyInfoListener(this);
        //释放资源
        mManager.destroy();
        mManager = null;
    }

}
