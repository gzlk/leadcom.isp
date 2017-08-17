package com.gzlk.android.isp.fragment.map;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseLocationSupportFragment;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.model.common.Address;
import com.gzlk.android.isp.model.common.HLKLocation;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredView;

/**
 * <b>功能描述：</b>地图控制页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/17 09:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/17 09:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class MapHandleableFragment extends BaseLocationSupportFragment {

    private static final String PARAM_LOCATED = "ampf_located";
    protected static final String PARAM_ADDRESS = "ampf_address";
    private static final int dftZoomLevel = 16;

    @ViewId(R.id.ui_map_picker_center_pointer)
    public LinearLayout centerPointer;
    @ViewId(R.id.ui_map_picker_relocation)
    public CorneredView relocation;
    @ViewId(R.id.ui_map_picker_map_view)
    public MapView mMapView;
    protected AMap mAMap;

    /**
     * 是否定位成功
     */
    protected boolean isLocated = false;
    protected Address address = new Address();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        isLocated = bundle.getBoolean(PARAM_LOCATED, false);
        String json = bundle.getString(PARAM_ADDRESS, "");
        if (!isEmpty(json)) {
            address = Address.fromJson(json);
        }
    }

    @Override
    public void doingInResume() {
        if (null == mAMap) {
            mAMap = mMapView.getMap();
        }
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putBoolean(PARAM_LOCATED, isLocated);
        bundle.putString(PARAM_ADDRESS, Address.toJson(address));
    }

    @Override
    protected void destroyView() {

    }

    protected void reduceLocation() {
        LatLng pos = new LatLng(address.getLatitude(), address.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(new CameraPosition(pos, dftZoomLevel, 0, 0));
        mAMap.animateCamera(update, duration(), null);
    }

    /**
     * 开始地图定位
     */
    protected void startLocation() {
        MyLocationStyle style = new MyLocationStyle();
        // 定位一次，且将地图移动到中心点
        style.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        style.strokeColor(getColor(R.color.colorPrimary));
        style.radiusFillColor(getColor(R.color.transparent_30_blue));
        style.strokeWidth(getDimension(R.dimen.ui_static_dp_1));
        mAMap.setOnMyLocationChangeListener(onMyLocationChangeListener);
        mAMap.setOnCameraChangeListener(onCameraChangeListener);
        mAMap.setMyLocationStyle(style);
        // 定位按钮
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 指南针
        mAMap.getUiSettings().setCompassEnabled(true);
        // 16级放大
        mAMap.getUiSettings().setZoomPosition(dftZoomLevel);
        mAMap.setMyLocationEnabled(true);
    }

    /**
     * 设置是否可以拖动地图
     */
    protected void setMapScrollEnable(boolean enable) {
        mAMap.getUiSettings().setScrollGesturesEnabled(enable);
    }

    private AMap.OnMyLocationChangeListener onMyLocationChangeListener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            isLocated = true;
            address.setLatitude(location.getLatitude());
            address.setLongitude(location.getLongitude());
            address.setAltitude(location.getAltitude());
            tryReverseGeoCode(new LatLonPoint(location.getLatitude(), location.getLongitude()));
        }
    };

    private AMap.OnCameraChangeListener onCameraChangeListener = new AMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            isLocated = true;
            address.setLatitude(cameraPosition.target.latitude);
            address.setLongitude(cameraPosition.target.longitude);
            tryReverseGeoCode(new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude));
        }
    };

    @Override
    protected void onFetchingLocationComplete(boolean success, HLKLocation location) {
        if (success) {
            isLocated = true;
            address.setLatitude(location.getLatitude());
            address.setLongitude(location.getLongitude());
            reduceLocation();
            startLocation();
        } else {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_map_picker_locate_failed);
        }
    }

    @Override
    protected void onReverseGeoCodeComplete(RegeocodeResult regeocodeResult) {
        if (null != regeocodeResult && null != regeocodeResult.getRegeocodeAddress()) {
            address.setAddress(regeocodeResult.getRegeocodeAddress().getFormatAddress());
            onReverseGeoCodeComplete(regeocodeResult.getRegeocodeAddress().getFormatAddress());
        } else {
            onReverseGeoCodeComplete("");
        }
    }

    protected void onReverseGeoCodeComplete(String address) {

    }
}
