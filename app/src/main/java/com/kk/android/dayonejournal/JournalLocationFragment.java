package com.kk.android.dayonejournal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JournalLocationFragment extends Fragment {
    private static final String TAG = "com.kk.anfroid.location";
    public LocationClient mLocationClient;
    private TextView positionView;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    //private Journal mJournal;
    private boolean isFirstLocate = true;
    //private static UUID sUUID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.location_baidu,container,false);
        positionView = (TextView)view.findViewById(R.id.location_text_view);
        mMapView = (MapView)view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest
        .permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest
                .permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest
                .permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList
                    .size()]);
            ActivityCompat.requestPermissions(getActivity(),permissions,1);
        }else {
            requestLocation();
        }
        return view;
    }

    private void navigateTo(BDLocation location){
        if(isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);
    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent){
        Log.d("cylog","receive it" );
        UUID journalId = messageEvent.getDayUUID();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(getActivity(), "必须同意权限",
                                    Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(getActivity(), "发生错误", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
                default:
        }
    }
    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation
                    || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                navigateTo(bdLocation);
            }
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度： ").append(bdLocation.getLatitude()).append(",");
            currentPosition.append("经度： ").append(bdLocation.getLongitude()).append(",");
            currentPosition.append("国家 ").append(bdLocation.getCountry()).append(",");
            currentPosition.append("省 ").append(bdLocation.getProvince()).append(",");
            currentPosition.append("市 ").append(bdLocation.getCity()).append(",");
            currentPosition.append("区 ").append(bdLocation.getDistrict()).append(",");
            currentPosition.append("街道 ").append(bdLocation.getStreet()).append(",");
           //currentPosition.append("定位方式： ");
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                currentPosition.append("GPS");
            }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                currentPosition.append("网络");
            }
            positionView.setText(currentPosition);
        }
    }
}

