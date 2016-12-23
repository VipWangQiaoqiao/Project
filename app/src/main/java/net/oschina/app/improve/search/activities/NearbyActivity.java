package net.oschina.app.improve.search.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

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

import net.oschina.app.R;
import net.oschina.app.improve.account.AccountHelper;
import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.base.adapter.BaseRecyclerAdapter;
import net.oschina.app.improve.widget.RecyclerRefreshLayout;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.TLog;

import java.util.List;

import butterknife.Bind;

/**
 * 寻找附近的人
 * Created by thanatosx on 2016/12/22.
 */

public class NearbyActivity extends BaseActivity implements RadarSearchListener, BDLocationListener, RecyclerRefreshLayout.SuperRefreshLayoutListener, BaseRecyclerAdapter.OnItemClickListener {

    @Bind(R.id.recycler) RecyclerView mRecycler;
    @Bind(R.id.layout_recycler_refresh) RecyclerRefreshLayout mRecyclerRefresh;

    private int mPageNum = 0;
    private LatLng mUserLatLng;
    private BaseRecyclerAdapter mAdapter;
    private LocationClient mLocationClient;
    private RadarSearchManager mManager = RadarSearchManager.getInstance();

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
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerRefresh.setSuperRefreshLayoutListener(this);
        mRecyclerRefresh.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        mRecycler.setAdapter(mAdapter = new BaseRecyclerAdapter(this, BaseRecyclerAdapter.ONLY_FOOTER) {
            @Override
            protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
                return null;
            }

            @Override
            protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Object item, int position) {

            }
        });
        mAdapter.setOnItemClickListener(this);

    }

    @Override
    protected void initData() {
        mManager.addNearbyInfoListener(this);

        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(this);


        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);

        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");

        //仅定位一次
        option.setScanSpan(0);

        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        //设置是否需要位置语义化结果
        option.setIsNeedLocationDescribe(true);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);

        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);

        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        //移除监听
        mManager.removeNearbyInfoListener(this);
        //清除用户信息
        mManager.clearUserInfo();
        //释放资源
        mManager.destroy();
        mManager = null;
    }

    /**
     * 附近的人的监听
     *
     * @param result Radar Nearby Result
     * @param error Radar Search Error
     */
    @Override
    public void onGetNearbyInfoList(RadarNearbyResult result, RadarSearchError error) {
        if (error != RadarSearchError.RADAR_NO_ERROR) {
            SimplexToast.show(this, "没有获取到附近的人");
            mAdapter.clear();
            mPageNum = --mPageNum > 0 ? mPageNum : 0;
            return;
        }
        TLog.i("oschina", "Get Nearby Info List: Successful");
        List<RadarNearbyInfo> infos =  result.infoList;
        for (RadarNearbyInfo info : infos) {
            TLog.i("oschina", String.format("comments: %s, distance: %s, mobile name: %s, mobile OS: %s, user id: %s",
                    info.comments, info.distance, info.mobileName, info.mobileOS, info.userID));
        }
    }

    /**
     * 上传用户信息的监听
     * @param error Radar Search Error
     */
    @Override
    public void onGetUploadState(RadarSearchError error) {
        if (error != RadarSearchError.RADAR_NO_ERROR) {
            SimplexToast.show(this, "上传用户信息失败");
            return;
        }
        TLog.i("oschina", "On Upload User Info: Successful");
        onRefreshing();
    }

    /**
     * 清除用户信息的监听
     * @param error Radar Search Error
     */
    @Override
    public void onGetClearInfoState(RadarSearchError error) {
        if (error != RadarSearchError.RADAR_NO_ERROR) {
            TLog.i("oschina", "On Clear Radar Search Info State: ERROR!!");
            return;
        }
        TLog.i("oschina", "On Clear Radar Search Info State: Successful");
    }

    /**
     * 定位回调
     *
     * @param location
     */
    @Override
    public void onReceiveLocation(BDLocation location) {
        final int code = location.getLocType();
        switch (code) {
            case 62:
                SimplexToast.show(this, "无法定位");
                return;
            case 63:
                SimplexToast.show(this, "网络异常");
                return;
            case 167:
                SimplexToast.show(this, "服务器定位失败，请检查权限");
                return;
        }
        if (code >= 501) {
            SimplexToast.show(this, "非法或无效的APP KEY");
            return;
        }
        TLog.i("oschina", String.format("定位成功，latitude: %s, longitude: %s, location describe: %s, user id: %s",
                location.getLatitude(), location.getLongitude(), location.getLocationDescribe(), AccountHelper.getUserId()));

        //周边雷达设置用户身份标识，id为空默认是设备标识
        mManager.setUserID(String.valueOf(AccountHelper.getUserId()));
        //上传位置
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = AccountHelper.getUser().getName();
        info.pt = mUserLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mManager.uploadInfoRequest(info);
    }

    @Override
    public void onRefreshing() {
        requestData(mPageNum = 0);
    }

    @Override
    public void onLoadMore() {
        requestData(++mPageNum);
    }

    private void requestData(int pageNum) {
        //构造请求参数，其中centerPt是自己的位置坐标
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(mUserLatLng).pageNum(pageNum).radius(5000).pageCapacity(20);
        //发起查询请求
        mManager.nearbyInfoRequest(option);
    }

    @Override
    public void onItemClick(int position, long itemId) {

    }
}
