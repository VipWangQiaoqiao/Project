package net.oschina.app.improve.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.AppConfig;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.Setting;
import net.oschina.app.base.BaseApplication;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.bean.User;
import net.oschina.app.improve.bean.Version;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.detail.db.API;
import net.oschina.app.improve.detail.db.Behavior;
import net.oschina.app.improve.detail.db.DBManager;
import net.oschina.app.improve.main.location.BDLocationAdapter;
import net.oschina.app.improve.main.location.RadarSearchAdapter;
import net.oschina.app.improve.main.nav.NavFragment;
import net.oschina.app.improve.main.nav.NavigationButton;
import net.oschina.app.improve.main.update.CheckUpdateManager;
import net.oschina.app.improve.main.update.DownloadService;
import net.oschina.app.improve.notice.NoticeManager;
import net.oschina.app.improve.search.activities.NearbyActivity;
import net.oschina.app.improve.tweet.service.TweetNotificationManager;
import net.oschina.app.improve.utils.DialogHelper;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.interf.OnTabReselectListener;
import net.oschina.app.util.StringUtils;
import net.oschina.app.util.TDevice;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static net.oschina.app.improve.search.activities.NearbyActivity.LOCATION_PERMISSION;

public class MainActivity extends BaseActivity implements NavFragment.OnNavigationReselectListener,
        EasyPermissions.PermissionCallbacks, CheckUpdateManager.RequestPermissions {

    private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限
    public static final String ACTION_NOTICE = "ACTION_NOTICE";
    private long mBackPressedTime;

    private Version mVersion;

    @Bind(R.id.activity_main_ui)
    LinearLayout mMainUi;

    private NavFragment mNavBar;
    private List<TurnBackListener> mTurnBackListeners = new ArrayList<>();
    private RadarSearchManager mRadarSearchManager;
    private LocationClient mLocationClient;
    private RadarSearchAdapter mRadarSearchAdapter;


    public interface TurnBackListener {
        boolean onTurnBack();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_ui;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        FragmentManager manager = getSupportFragmentManager();
        mNavBar = ((NavFragment) manager.findFragmentById(R.id.fag_nav));
        mNavBar.setup(this, manager, R.id.main_container, this);

        if (AppContext.get("isFirstComing", true)) {
            View view = findViewById(R.id.layout_ripple);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ViewGroup) v.getParent()).removeView(v);
                    AppContext.set("isFirstComing", false);
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doNewIntent(getIntent(), true);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
            //如果是两天前的数据，则全部上传
            String updateTime = AppConfig.getAppConfig(this).get("upload_behavior_time");
            if (DBManager.from(getApplicationContext()).getCount(Behavior.class) >= 15 &&
                    !TextUtils.isEmpty(updateTime) &&
                    (System.currentTimeMillis() - StringUtils.toDate(updateTime).getTime() >= 172800000)) {
                final List<Behavior> behaviors = DBManager.from(getApplicationContext())
                        .get(Behavior.class);
                API.addBehaviors(new Gson().toJson(behaviors), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<String>>() {
                            }.getType();
                            ResultBean<String> bean = new Gson().fromJson(responseString, type);
                            if (bean.isSuccess()) {
                                //清楚数据，避免清空没有上传的数据
                                DBManager.from(getApplicationContext())
                                        .where("id<=?", String.valueOf(behaviors.get(behaviors.size() - 1).getId()))
                                        .delete(Behavior.class);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        doNewIntent(intent, false);
    }

    @SuppressWarnings("unused")
    private void doNewIntent(Intent intent, boolean isCreate) {
        if (intent == null || intent.getAction() == null)
            return;
        String action = intent.getAction();
        if (action.equals(ACTION_NOTICE)) {
            NavFragment bar = mNavBar;
            if (bar != null) {
                bar.select(3);
            }
        }
    }

    @Override
    public void onReselect(NavigationButton navigationButton) {
        Fragment fragment = navigationButton.getFragment();
        if (fragment != null
                && fragment instanceof OnTabReselectListener) {
            OnTabReselectListener listener = (OnTabReselectListener) fragment;
            listener.onTabReselect();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        NoticeManager.init(this);
        // in this we can checkShare update
        checkUpdate();
        checkLocation();
        TweetNotificationManager.setup(this);
    }

    private void checkLocation() {

        //首先判断appCode是否存在，如果存在是否大于当前版本的appCode，或者第一次全新安装(默认0)表示没有保存appCode
        int hasLocationAppCode = Setting.hasLocationAppCode(getApplicationContext());
        int versionCode = TDevice.getVersionCode();
        if ((hasLocationAppCode <= 0) || (hasLocationAppCode > versionCode)) {
            //如果是登陆状态，直接进行位置信息定位并上传
            if (AccountHelper.isLogin()) {
                //当app第一次被安装时，不管是覆盖安装（不管是否有定位权限）还是全新安装都必须进行定位请求
                Setting.updateLocationAppCode(getApplicationContext(), versionCode);
                requestLocationPermission();
            }
            return;
        }

        //如果有账户登陆，并且有主动上传过位置信息。那么准备请求定位
        if (AccountHelper.isLogin() && Setting.hasLocation(getApplicationContext())) {

            //1.有主动授权过，直接进行定位，否则不进行操作任何操作
            if (Setting.hasLocationPermission(getApplicationContext())) {
                requestLocationPermission();
            }
        }
    }

    @Override
    public void call(Version version) {
        this.mVersion = version;
        requestExternalStorage();
    }

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            DownloadService.startService(this, mVersion.getDownloadUrl());
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    /**
     * proxy request permission
     */
    @AfterPermissionGranted(NearbyActivity.LOCATION_PERMISSION)
    private void requestLocationPermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE)) {
            startLbs();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.need_lbs_permission_hint), LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

        for (String perm : perms) {
            if (perm.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                DialogHelper.getConfirmDialog(this, "温馨提示", "需要开启开源中国对您手机的存储权限才能下载安装，是否现在开启", "去开启", "取消", true, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    }
                }, null).show();

            } else {
                Setting.updateLocationPermission(getApplicationContext(), false);
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NoticeManager.stopListen(this);
        releaseLbs();
    }

    public void addOnTurnBackListener(TurnBackListener l) {
        this.mTurnBackListeners.add(l);
    }

    public void toggleNavTabView(boolean isShowOrHide) {
        final View view = mNavBar.getView();
        if (view == null) return;
        // hide
        view.setVisibility(View.VISIBLE);
        if (!isShowOrHide) {
            view.animate()
                    .translationY(view.getHeight())
                    .setDuration(180)
                    .setInterpolator(new LinearInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setTranslationY(view.getHeight());
                            view.setVisibility(View.GONE);
                        }
                    });
        } else {
            view.animate()
                    .translationY(0)
                    .setDuration(180)
                    .setInterpolator(new LinearInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            // fix:bug > 点击隐藏的同时，快速点击显示
                            view.setVisibility(View.VISIBLE);
                            view.setTranslationY(0);
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        for (TurnBackListener l : mTurnBackListeners) {
            if (l.onTurnBack()) return;
        }
        boolean isDoubleClick = BaseApplication.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true);
        if (isDoubleClick) {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (3 * 1000)) {
                finish();
            } else {
                mBackPressedTime = curTime;
                Toast.makeText(this, R.string.tip_double_click_exit, Toast.LENGTH_LONG).show();
            }
        } else {
            finish();
        }
    }

    private void checkUpdate() {
        if (!AppContext.get(AppConfig.KEY_CHECK_UPDATE, true)) {
            return;
        }
        CheckUpdateManager manager = new CheckUpdateManager(this, false);
        manager.setCaller(this);
        manager.checkUpdate();
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
                    //上传成功，更新用户本地定位信息标示
                    if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
                        Setting.updateLocationInfo(getApplicationContext(), true);
                    } else {
                        Setting.updateLocationInfo(getApplicationContext(), false);
                    }
                    //不管是否上传成功，都主动释放lbs资源
                    releaseLbs();
                }
            });
        }

        if (mLocationClient == null) {
            mLocationClient = new LocationClient(this);
            mLocationClient.registerLocationListener(new BDLocationAdapter() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    super.onReceiveLocation(bdLocation);
                    //处理返回的定位信息，进行用户位置信息上传
                    ReceiveLocation(bdLocation);
                }
            });
        }

        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");

        //根据网络情况和gps进行准确定位，只定位一次 当获取到真实有效的经纬度时，主动关闭定位功能
        option.setScanSpan(0);

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
     * 定位回调处理
     *
     * @param location location
     */
    private void ReceiveLocation(BDLocation location) {

        final int code = location.getLocType();
        switch (code) {
            case BDLocation.TypeCriteriaException://62
                releaseLbs();
                return;
            case BDLocation.TypeNetWorkException://63
                releaseLbs();
                return;
            case BDLocation.TypeServerError://167
                releaseLbs();
                return;
            case BDLocation.TypeNetWorkLocation://161 网络定位模式
                break;
            case BDLocation.TypeOffLineLocation://66  离线模式

                if (!TDevice.hasInternet()) {
                    SimplexToast.show(this, getString(R.string.tip_network_error));
                    releaseLbs();
                    return;
                }

                break;
        }

        if (code >= 501) {//非法key
            releaseLbs();
            return;
        }

        //定位成功，网络ok，主动上传用户位置信息
        if (TDevice.hasInternet() && location.getLatitude() != 4.9E-324 && location.getLongitude() != 4.9E-324) {

            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            Setting.updateLocationPermission(getApplicationContext(), true);

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
                    comments = URLEncoder.encode(comments, NearbyActivity.CHARSET);
                    info.comments = comments;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    SimplexToast.show(this, getString(R.string.upload_lbs_info_hint));
                }

            }

            mRadarSearchManager.setUserID(userId);
            info.pt = userLatLng;
            mRadarSearchManager.uploadInfoRequest(info);
        } else {
            //返回的位置信息异常或网络有问题，即定位失败，停止定位功能，并释放lbs资源
            releaseLbs();
        }
    }

    /**
     * release lbs source
     */
    private void releaseLbs() {
        if (mLocationClient != null && mLocationClient.isStarted())
            mLocationClient.stop();
        mLocationClient = null;
        //移除监听
        if (mRadarSearchManager != null) {
            mRadarSearchManager.removeNearbyInfoListener(mRadarSearchAdapter);
            //释放资源
            mRadarSearchManager.destroy();
            mRadarSearchManager = null;
        }
    }
}
