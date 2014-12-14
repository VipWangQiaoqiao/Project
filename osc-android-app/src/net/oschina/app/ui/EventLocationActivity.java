package net.oschina.app.ui;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import net.oschina.app.R;
import net.oschina.app.base.BaseActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

@SuppressLint("InflateParams")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EventLocationActivity extends BaseActivity {

	MapView mMapView = null;

	@Override
	protected boolean hasBackButton() {
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.fragment_event_location);
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void initView() {

		mMapView = (MapView) findViewById(R.id.bmapView);
		setActionBarTitle(R.string.actionbar_title_event_location);
	}

	@Override
	public void initData() {
//		BaiduMap mBaiduMap = mMapView.getMap();
//		//普通地图  
//		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);  
////		//定义Maker坐标点  
////		LatLng point = new LatLng(39.963175, 116.400244);  
////		//构建Marker图标  
////		BitmapDescriptor bitmap = BitmapDescriptorFactory  
////		    .fromResource(R.drawable.ic_launcher);  
////		//构建MarkerOption，用于在地图上添加Marker  
////		OverlayOptions option = new MarkerOptions()  
////		    .position(point)  
////		    .icon(bitmap);  
////		//在地图上添加Marker，并显示  
////		Marker marker = (Marker) (mBaiduMap.addOverlay(option));
//		
//		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
//		    public void onMarkerDrag(Marker marker) {
//		        //拖拽中
//		    }
//		    public void onMarkerDragEnd(Marker marker) {
//		        //拖拽结束
//		    }
//		    public void onMarkerDragStart(Marker marker) {
//		        //开始拖拽
//		    }
//		});
	}
}