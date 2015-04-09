package com.yueme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.GeoPoint;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapController;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.yueme.util.ToastUtil;

public class LocationActivity extends MapActivity implements TencentLocationListener{
	private MapView mapView;
	private MapController locationController;
	private TencentLocationManager locationManager;
	private boolean located;
	private String locationInfo;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_location);
		mapView = (MapView) findViewById(R.id.mapview);
		locationController = mapView.getController();
		locationController.setZoom(10^20);
		TextView tv_send = (TextView) findViewById(R.id.tv_send);
		
		String extraLocationInfo = getIntent().getExtras().getString("locationInfo");
		if(!extraLocationInfo.equals("sendLocation")){
			tv_send.setVisibility(View.GONE);
			
			String infos[] = extraLocationInfo.split("-");
			Log.d("location", infos[0]+","+infos[1]+","+infos[2]);
			double latitude = Double.valueOf(infos[1]);
			double longitude = Double.valueOf(infos[2]);
			moveToLocation(latitude, longitude, infos[0]);
		}else{
			locationManager = TencentLocationManager.getInstance(LocationActivity.this);
			TencentLocationRequest request  =TencentLocationRequest.create();
			locationManager.requestLocationUpdates(request, this);
			
			tv_send.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d("send", "send");
					if(located) {
						Intent intent = new Intent();
						intent.putExtra("locationInfo", locationInfo);
						setResult(RESULT_OK, intent);
						finish();
					}else{
						ToastUtil.showToast("定位中...", LocationActivity.this);
					}
				}
			});
		}
		
		
		
	}

	
	public void back(View v){
		finish();
	}
	@Override
	public void onLocationChanged(TencentLocation arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		if(arg1==TencentLocation.ERROR_OK){
			
			located = true;
			locationInfo = arg0.getAddress()+"-"+arg0.getLatitude()+"-"+arg0.getLongitude();
			Log.d("main activity", "accuracy: "+arg0.getAccuracy());
			moveToLocation(arg0.getLatitude(), arg0.getLongitude(), arg0.getAddress());
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public void onStatusUpdate(String arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	
	private void moveToLocation(double latitude, double longitude, String addr){
		GeoPoint geoPoint = new GeoPoint((int)(latitude*1e6), (int)(longitude*1e6));
		locationController.setCenter(geoPoint);
		//locationController.animateTo(latLng, 100, null);
		LatLng latLng = new LatLng(latitude, longitude);
		Marker marker = mapView.addMarker(new MarkerOptions()
		.position(latLng)
		.title(addr)
		.anchor(0.5f, 0.5f)
		.icon(BitmapDescriptorFactory
				.defaultMarker())
				.draggable(true));
		marker.showInfoWindow();
	}

}
