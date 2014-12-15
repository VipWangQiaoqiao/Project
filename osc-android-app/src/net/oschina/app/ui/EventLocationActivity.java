package net.oschina.app.ui;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 活动地图位置显示
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @created 2014年12月15日 下午1:28:28
 * 
 */
@SuppressLint("InflateParams")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EventLocationActivity extends BaseActivity implements
		OnGetGeoCoderResultListener {

	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	BaiduMap mBaiduMap = null;
	MapView mMapView = null;

	private String mCity;

	private String mLocation;

	@Override
	protected boolean hasBackButton() {
		return true;
	}

	@Override
	protected int getActionBarTitle() {
		return R.string.actionbar_title_event_location;
	}

	protected void onCreate(Bundle savedInstanceState) {
		SDKInitializer.initialize(getApplicationContext());
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_event_location;
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			AppContext.showToast("抱歉，未能找到结果");
			return;
		}
		mBaiduMap.clear();

		final LatLng location = result.getLocation();

		final Marker marker = (Marker) mBaiduMap
				.addOverlay(new MarkerOptions()
						.position(location)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.icon_gcoding))
						.draggable(true));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);

		View view = mInflater.inflate(R.layout.event_spot_pupwindow, null, false);
		
		TextView spot = (TextView) view.findViewById(R.id.tv_spot);
		spot.setText(mLocation);
		OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
			public void onInfoWindowClick() {
				startNavi(location, location);
			}
		};
		InfoWindow mInfoWindow = new InfoWindow(
				BitmapDescriptorFactory.fromView(view), location, -80, listener);
		
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_gcoding)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		Toast.makeText(EventLocationActivity.this, result.getAddress(),
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {

	}
	
	/**
	 * 开始导航
	 * 
	 * @param view
	 */
	public void startNavi(LatLng pt1, LatLng pt2) {
		// 构建 导航参数
		NaviPara para = new NaviPara();
		para.startPoint = pt1;
		para.startName = "从这里开始";
		para.endPoint = pt2;
		para.endName = "到这里结束";

		try {
			BaiduMapNavigation.openBaiduMapNavi(para, this);
		} catch (BaiduMapAppNotSupportNaviException e) {
		}
	}

	@Override
	public void initView() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		Intent intent = getIntent();

		mCity = intent.getStringExtra("city");

		mLocation = intent.getStringExtra("location");

		// Geo搜索
		mSearch.geocode(new GeoCodeOption().city(mCity).address(mLocation));
	}

	@Override
	public void initData() {

	}
}