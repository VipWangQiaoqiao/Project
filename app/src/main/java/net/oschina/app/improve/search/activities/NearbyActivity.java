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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private static final int LOCATION_PERMISSION = 0x0100;//定位权限
    public static final String CHARSET = "UTF-8";

    @Bind(R.id.recycler)
    RecyclerView mRecycler;

    @Bind(R.id.layout_recycler_refresh)
    RecyclerRefreshLayout mRecyclerRefresh;

    @Bind(R.id.lay_emptyLayout)
    EmptyLayout mEmptyLayout;
    private NearbyUserAdapter mAdapter;

    private int mNextPageIndex = 0;
    private LatLng mUserLatLng;

    private LocationClient mLocationClient = null;
    private RadarSearchManager mRadarSearchManager = null;

    private LocationManager mLocationManager;

    private boolean mIsFirstLocation = true;
    private AlertDialog.Builder confirmDialog;
    private AlertDialog alertDialog;
    private BottomDialog mSelectorDialog;

    /**
     * show activity
     *
     * @param context context
     */
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
        initLbs();
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
        if (!mIsFirstLocation && isEnabledLocation()) {
            if (mLocationClient == null) {
                startLbs();
            } else {
                mLocationClient.requestLocation();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseLbs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK || resultCode == RESULT_CANCELED)
                && requestCode == LOCATION_PERMISSION) {
            startLbs();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear_opt:
                //清除用户信息
                mRadarSearchManager.clearUserInfo();
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

    /**
     * 附近的人的监听
     *
     * @param result Radar Nearby Result
     * @param error  Radar Search Error
     */
    @Override
    public void onGetNearbyInfoList(RadarNearbyResult result, RadarSearchError error) {
        updateView(result);
    }

    /**
     * 上传用户信息的监听
     *
     * @param error Radar Search Error
     */
    @Override
    public void onGetUploadState(RadarSearchError error) {
        switch (error) {
            case RADAR_NETWORK_ERROR:
            case RADAR_NETWORK_TIMEOUT:
                if (mNextPageIndex == 0) {
                    showError(EmptyLayout.NETWORK_ERROR);
                    mEmptyLayout.setNoDataContent(getString(R.string.network_timeout_hint));
                } else {
                    AppContext.showToastShort(R.string.request_error_hint);
                }
                SimplexToast.show(this, getString(R.string.upload_lbs_info_hint));
                break;
            case RADAR_NO_ERROR:
                if (mIsFirstLocation) {
                    Setting.updateLocationInfo(getApplicationContext(), true);
                    onRefreshing();
                }
                break;
            case RADAR_PERMISSION_UNFINISHED:
                ShowSettingDialog();
                break;
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
            SimplexToast.show(this, getString(R.string.clear_bodies_failed_hint));
            return;
        }
        Setting.updateLocationInfo(getApplicationContext(), false);
        supportFinishAfterTransition();
    }

    /**
     * lbs callback
     *
     * @param location location
     */
    @Override
    public void onReceiveLocation(BDLocation location) {
        ReceiveLocation(location);
    }

    @Override
    public void onRefreshing() {
        requestData(0);
    }

    @Override
    public void onLoadMore() {
        requestData(mNextPageIndex);
    }

    @Override
    public void onItemClick(int position, long itemId) {
        NearbyResult result = mAdapter.getItem(position);
        if (result == null) return;
        OtherUserHomeActivity.show(this, result.getUser());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ShowSettingDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * proxy request permission
     */
    @AfterPermissionGranted(LOCATION_PERMISSION)
    private void requestLocationPermission() {

        if (isEnabledLocation()) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE)) {
                startLbs();
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.need_lbs_permission_hint), LOCATION_PERMISSION,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE);
            }
        }
    }

    /**
     * menu action selector Dialog
     *
     * @return dialog
     */
    private Dialog getSelectorDialog() {
        if (mSelectorDialog == null) {
            mSelectorDialog = new BottomDialog(this, true);
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.view_nearby_operator, null, false);
            view.findViewById(R.id.tv_clear_opt).setOnClickListener(this);
            view.findViewById(R.id.tv_cancel_opt).setOnClickListener(this);
            mSelectorDialog.setContentView(view);
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.setBackgroundResource(R.color.transparent);
            }
        }
        return mSelectorDialog;
    }

    /**
     * update data view
     *
     * @param result radarNearByResult
     */
    private void updateView(RadarNearbyResult result) {

        mRecyclerRefresh.onComplete();
        if (result != null) {
            //pageNum==0，表示初始化数据，有可能是刷新，也有可能是第一次加载
            List<RadarNearbyInfo> infoList = result.infoList;
            int pageIndex = result.pageIndex;

            if (infoList != null) {
                int loadInfoSize = infoList.size();

                List<NearbyResult> items = mAdapter.getItems();

                int tempSize = items.size();

                if (pageIndex == 0) {
                    //发现已有数据，直接更新对应的数据
                    if (tempSize > 0) {
                        for (RadarNearbyInfo info : infoList) {
                            User user = null;
                            try {
                                String comments = URLDecoder.decode(info.comments, CHARSET);
                                user = AppOperator.createGson().fromJson(comments, User.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (user == null || (user.getId() == 0 && TextUtils.isEmpty(user.getName())))
                                continue;

                            int index = containsFriend(user);
                            if (index == -1) {
                                NearbyResult.Nearby nearby = new NearbyResult.Nearby();
                                nearby.setDistance(info.distance);
                                nearby.setMobileName(info.mobileName);
                                nearby.setMobileOS(info.mobileOS);
                                items.add(new NearbyResult(user, nearby));
                            }
                        }
                        if (tempSize < items.size()) {
                            notifySortData(loadInfoSize, pageIndex, items);
                        }
                    } else {
                        //没有缓存数据，直接添加
                        for (RadarNearbyInfo info : infoList) {
                            User user = null;
                            try {
                                String comments = URLDecoder.decode(info.comments, CHARSET);
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
                            items.add(new NearbyResult(user, nearby));
                        }

                        //根据数据的距离从近到远进行排序
                        notifySortData(loadInfoSize, pageIndex, items);
                    }

                } else {
                    //当pageNum>0时，证明是翻页，不管时候有缓存，直接添加
                    for (RadarNearbyInfo info : infoList) {
                        User user = null;
                        try {
                            String comments = URLDecoder.decode(info.comments, CHARSET);
                            user = AppOperator.createGson().fromJson(comments, User.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (user == null || (user.getId() == 0 && TextUtils.isEmpty(user.getName())))
                            continue;

                        int index = containsFriend(user);

                        if (index == -1) {
                            NearbyResult.Nearby nearby = new NearbyResult.Nearby();
                            nearby.setDistance(info.distance);
                            nearby.setMobileName(info.mobileName);
                            nearby.setMobileOS(info.mobileOS);
                            items.add(new NearbyResult(user, nearby));
                        }
                    }

                    if (tempSize < items.size()) {
                        //根据数据的距离从近到远进行排序
                        notifySortData(loadInfoSize, pageIndex, items);
                    }
                }
            } else {
                //没有数据返回时,不管pageIndex是多少，保证nextPageIndex不变与缓存不变
                notifyNoData();
            }
        } else {
            //2.请求结果为null，不管pageIndex如何，保持缓存不变
            notifyNoData();
        }

    }

    /**
     * notify no data
     */
    private void notifyNoData() {
        int count = mAdapter.getCount();
        if (count <= 0) {
            //没有缓存直接进行提示
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true);
        }
        hideLoading();
    }

    /**
     * notify sort data
     *
     * @param loadInfoSize response info size
     * @param pageIndex    request page index
     * @param items        cache data
     */
    private void notifySortData(int loadInfoSize, int pageIndex, List<NearbyResult> items) {
        //根据数据的距离从近到远进行排序
        Collections.sort(items);
        //刷新数据，初始化有效数据ui
        mAdapter.notifyDataSetChanged();
        mAdapter.setState(loadInfoSize < 20 ? BaseRecyclerAdapter.STATE_NO_MORE : BaseRecyclerAdapter.STATE_LOAD_MORE, loadInfoSize >= 20);
        //隐藏emptyView
        hideLoading();
        mNextPageIndex = (pageIndex + 1);
    }

    /**
     * check is cache
     *
     * @param user load_user
     * @return isCache?index:-1
     */
    private int containsFriend(User user) {
        int index = -1;
        List<NearbyResult> items = this.mAdapter.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getUser().getId() == user.getId()) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * show setting dialog
     */
    private void ShowSettingDialog() {

        if (confirmDialog == null) {
            confirmDialog = DialogHelper.getConfirmDialog(this, getString(R.string.location_get_failed_hint),
                    getString(R.string.no_permission_hint),
                    getString(R.string.cancel), getString(R.string.actionbar_title_setting), true, null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, LOCATION_PERMISSION);
                        }
                    });
        }

        if (alertDialog == null) {
            alertDialog = confirmDialog.create();
        }

        if (alertDialog != null) {
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    /**
     * 定位回调处理
     *
     * @param location location
     */
    private void ReceiveLocation(BDLocation location) {
        final int code = location.getLocType();
        switch (code) {
            case BDLocation.TypeCriteriaException://62
                showError(EmptyLayout.NODATA);
                SimplexToast.show(this, getString(R.string.no_location_hint));
                mLocationClient.requestLocation();
                mRecyclerRefresh.setOnLoading(false);
                return;
            case BDLocation.TypeNetWorkException://63
                if (mNextPageIndex == 0) {
                    showError(EmptyLayout.NETWORK_ERROR);
                }
                mRecyclerRefresh.setOnLoading(false);
                SimplexToast.show(this, getString(R.string.network_exception_hint));
                return;
            case BDLocation.TypeServerError://167
                if (isEnabledLocation()) {
                    ShowSettingDialog();
                }
                hideLoading();
                showError(EmptyLayout.NODATA);
                mRecyclerRefresh.setOnLoading(false);
                SimplexToast.show(this, getString(R.string.server_no_have_permission_hint));
                return;
            case BDLocation.TypeNetWorkLocation://161

                if (!TDevice.hasInternet() && mNextPageIndex == 0 && mAdapter.getCount() <= 0) {
                    showError(EmptyLayout.NETWORK_ERROR);
                }
                break;
            case BDLocation.TypeOffLineLocation://66  离线模式

                if (!TDevice.hasInternet()) {
                    showError(EmptyLayout.NETWORK_ERROR);
                    SimplexToast.show(this, getString(R.string.tip_network_error));
                    return;
                }

                break;
        }

        if (code >= 501) {
            showError(EmptyLayout.NODATA);
            SimplexToast.show(this, getString(R.string.key_is_invalid_hint));
            return;
        }

        if (TDevice.hasInternet() && location.getLatitude() != 4.9E-324 && location.getLongitude() != 4.9E-324) {

            boolean started = mLocationClient.isStarted();

            if (started) {
                mLocationClient.stop();
            }

            mUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());

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
                    comments = URLEncoder.encode(comments, CHARSET);
                    info.comments = comments;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    SimplexToast.show(this, getString(R.string.upload_lbs_info_hint));
                }
            }

            mRadarSearchManager.setUserID(userId);
            info.pt = mUserLatLng;
            mRadarSearchManager.uploadInfoRequest(info);
        } else {
            if (!TDevice.hasInternet() && mNextPageIndex == 0 && mAdapter.getCount() <= 0) {
                showError(EmptyLayout.NETWORK_ERROR);
            }
        }
    }

    /**
     * 判断系统定位功能是否打开，如果未打开直接跳转去打开
     *
     * @return true/false
     */
    private boolean isEnabledLocation() {

        LocationManager locationManager = this.mLocationManager;

        if (mLocationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            this.mLocationManager = locationManager;
        }

        if (locationManager != null) {

            //gps  基于gps定位
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //network  基于wifi和基站定位
            boolean netEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            //passive  基于被动的精度不高，或者不怎么变化的位置服务，比如其他应用或者定位服务位置更新时的定位
            //boolean passiveEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            if (gpsEnabled || netEnabled) {
                return true;
            } else {

                DialogHelper.getConfirmDialog(this, getString(R.string.location_get_failed_hint),
                        getString(R.string.no_permission_hint), getString(R.string.cancel), getString(R.string.actionbar_title_setting),
                        true, null, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, LOCATION_PERMISSION);
                            }
                        }).create().show();

                return false;
            }

        } else {
            showError(EmptyLayout.NODATA);
            AppContext.showToastShort(R.string.near_body_gps_error_hint);
            finish();
            return false;
        }
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
            mRadarSearchManager.addNearbyInfoListener(this);
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

    /**
     * release lbs source
     */
    private void releaseLbs() {
        if (mLocationClient != null && mLocationClient.isStarted())
            mLocationClient.stop();
        //移除监听
        if (mRadarSearchManager != null) {
            mRadarSearchManager.removeNearbyInfoListener(this);
            //释放资源
            mRadarSearchManager.destroy();
            mRadarSearchManager = null;
        }
    }

    /**
     * request data
     *
     * @param pageIndex pageIndex
     */
    private void requestData(int pageIndex) {

        if (TDevice.hasInternet()) {

            if (pageIndex == 0 && mAdapter.getCount() <= 0) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }

            if (mUserLatLng == null || (mUserLatLng.latitude == 4.9E-324 && mUserLatLng.longitude == 4.9E-324)) {
                mLocationClient.requestLocation();
                return;
            }

            //构造请求参数，其中centerPt是自己的位置坐标
            RadarNearbySearchOption option = new RadarNearbySearchOption()
                    .centerPt(mUserLatLng).pageNum(pageIndex).radius(35000).pageCapacity(20);
            //发起查询请求
            mRadarSearchManager.nearbyInfoRequest(option);
            mIsFirstLocation = false;

        } else {
            if (pageIndex == 0) {
                if (mAdapter.getCount() > 0) {
                    mAdapter.setState(BaseRecyclerAdapter.STATE_INVALID_NETWORK, false);
                    AppContext.showToastShort(R.string.error_view_network_error_click_to_refresh);
                    mRecyclerRefresh.onComplete();
                } else {
                    showError(EmptyLayout.NETWORK_ERROR);
                    AppContext.showToastShort(R.string.error_view_network_error_click_to_refresh);
                }
            } else {
                mRecyclerRefresh.onComplete();
                AppContext.showToastShort(R.string.error_view_network_error_click_to_refresh);
            }
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

}
