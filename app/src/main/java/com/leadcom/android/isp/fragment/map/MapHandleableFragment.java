package com.leadcom.android.isp.fragment.map;

import android.Manifest;
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
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseLocationSupportFragment;
import com.leadcom.android.isp.helper.GaodeHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.common.Address;
import com.leadcom.android.isp.model.common.HLKLocation;
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

    protected static boolean hasPermission = false;
    private static final String PARAM_LOCATED = "ampf_located";
    protected static final String PARAM_ADDRESS = "ampf_address";
    private static final float dftZoomLevel = 18f;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        hasPermission = false;
        checkPermission();
        super.onActivityCreated(savedInstanceState);
    }

    // 尝试获取定位权限
    private void checkPermission() {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            String text = StringHelper.getString(R.string.ui_text_permission_location_request);
            String denied = StringHelper.getString(R.string.ui_text_permission_location_denied);
            tryGrantPermission(Manifest.permission.ACCESS_FINE_LOCATION, GRANT_LOCATION, text, denied);
        } else {
            hasPermission = true;
        }
    }

    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        if (requestCode == GRANT_LOCATION) {
            hasPermission = true;
        }
        super.permissionGranted(permissions, requestCode);
    }

    @Override
    public void permissionGrantFailed(int requestCode) {
        super.permissionGrantFailed(requestCode);
        if (requestCode == GRANT_CONTACTS) {
            setNothingText(getString(R.string.ui_text_permission_location_denied));
            displayNothing(true);
        }
    }

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
            mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onMapLoadedComplete();
                        }
                    }, duration());
                }
            });
        }
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    protected void onMapLoadedComplete() {
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putBoolean(PARAM_LOCATED, isLocated);
        bundle.putString(PARAM_ADDRESS, Address.toJson(address));
    }

    @Override
    protected void destroyView() {
        mAMap = null;
        mMapView.onDestroy();
    }

    protected void reduceLocation() {
        Handler().post(new Runnable() {
            @Override
            public void run() {
                LatLng pos = new LatLng(address.getLatitude(), address.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(pos, dftZoomLevel);
                mAMap.animateCamera(update, duration(), new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        //mAMap.animateCamera(CameraUpdateFactory.zoomTo(dftZoomLevel), duration(), null);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    /**
     * 定位类型
     */
    protected int getLocationType() {
        // 定位一次，且将地图移动到中心点
        return MyLocationStyle.LOCATION_TYPE_LOCATE;
    }

    /**
     * 开始地图定位
     */
    protected void startLocation() {
        MyLocationStyle style = new MyLocationStyle();
        style.myLocationType(getLocationType());
        style.strokeColor(getColor(R.color.colorPrimary));
        style.radiusFillColor(getColor(R.color.transparent_30_blue));
        style.strokeWidth(getDimension(R.dimen.ui_static_dp_1));
        style.interval(GaodeHelper.SI_5);
        mAMap.setOnMyLocationChangeListener(onMyLocationChangeListener);
        mAMap.setOnCameraChangeListener(onCameraChangeListener);
        mAMap.setMyLocationStyle(style);
        // 定位按钮
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 指南针
        mAMap.getUiSettings().setCompassEnabled(true);
        // 比例尺
        mAMap.getUiSettings().setScaleControlsEnabled(true);
        // 放大缩小控制
        //mAMap.getUiSettings().setZoomControlsEnabled(true);
        mAMap.setMyLocationEnabled(true);
    }

    /**
     * 停止定位
     */
    protected void enableMyLocation(boolean enable) {
        mAMap.setMyLocationEnabled(enable);
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
            if (!isLocated) {
                // 未定位成功之前，设置放大级数
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                final CameraUpdate update = CameraUpdateFactory.newCameraPosition(new CameraPosition(pos, dftZoomLevel, 0, 0));
                Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mAMap.animateCamera(update);
                    }
                });
            }
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
