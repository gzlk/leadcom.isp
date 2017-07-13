package com.gzlk.android.isp.fragment.map;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseLocationSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.common.Address;
import com.gzlk.android.isp.model.common.BaiduLocation;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredView;
import com.netease.nim.uikit.LocationProvider;

/**
 * <b>功能描述：</b>百度地图地址定位页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 16:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 16:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AddressMapPickerFragment extends BaseLocationSupportFragment {

    public static AddressMapPickerFragment newInstance(String params) {
        AddressMapPickerFragment ampf = new AddressMapPickerFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putBoolean(PARAM_REDUCE, Boolean.valueOf(strings[0]));
        bundle.putString(PARAM_ADDRESS, StringHelper.replaceJson(strings[1], true));
        ampf.setArguments(bundle);
        return ampf;
    }

    public static LocationProvider.Callback callback;

    private static final String PARAM_REDUCE = "ampf_reduce";
    private static final String PARAM_LOCATED = "ampf_located";
    private static final String PARAM_ADDRESS = "ampf_address";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        isReduce = bundle.getBoolean(PARAM_REDUCE, false);
        isLocated = bundle.getBoolean(PARAM_LOCATED, isLocated);
        String json = bundle.getString(PARAM_ADDRESS, "");
        if (!isEmpty(json)) {
            address = Address.fromJson(json);
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putBoolean(PARAM_REDUCE, isReduce);
        bundle.putBoolean(PARAM_LOCATED, isLocated);
        bundle.putString(PARAM_ADDRESS, Address.toJson(address));
    }

    @ViewId(R.id.ui_map_picker_address)
    private TextView addressView;
    @ViewId(R.id.ui_map_picker_map_view)
    private MapView mMapView;
    @ViewId(R.id.ui_map_picker_center_pointer)
    private LinearLayout centerPointer;
    @ViewId(R.id.ui_map_picker_relocation)
    private CorneredView relocation;

    private BaiduMap mBaiduMap;

    private boolean isLocated = false, isReduce = false;
    private Address address = new Address();

    @Override
    public int getLayout() {
        return R.layout.fragment_map_picker;
    }

    @Click({R.id.ui_map_picker_relocation})
    private void elementClick(View view) {
        tryFetchingLocation();
    }

    @Override
    public void doingInResume() {
        setCustomTitle(isReduce ? R.string.ui_nim_action_location : R.string.ui_activity_sign_map_picker_fragment_title);
        setRightText(isReduce ? 0 : R.string.ui_base_text_confirm);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (!isReduce) {
                    tryResultAddress();
                }
            }
        });
        // 重现地图时不需要中间的定位图标
        centerPointer.setVisibility(isReduce ? View.GONE : View.VISIBLE);
        // 重现地图时不需要右上角的重新定位图标
        relocation.setVisibility(isReduce ? View.GONE : View.VISIBLE);

        if (null == mBaiduMap) {
            mBaiduMap = mMapView.getMap();
            mBaiduMap.setOnMapStatusChangeListener(mapChangeListener);
        }
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        mBaiduMap.setMyLocationEnabled(true);
        if (isReduce) {
            if (!isLocated) {
                isLocated = true;
                // 重现位置信息
                reduceLocation();
            }
        } else {
            if (!isLocated) {
                tryFetchingLocation();
            }
        }
    }

    private void reduceLocation() {
        addressView.setText(address.getAddress());
        MyLocationData loc = new MyLocationData.Builder()
                .accuracy(0)// 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(0)
                .latitude(address.getLatitude())
                .longitude(address.getLongitude()).build();
        mBaiduMap.setMyLocationData(loc);
        resetMapCenterPoint(address.getLatitude(), address.getLongitude());
    }

    private void tryResultAddress() {
        if (!isLocated) {
            SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_sing_map_picker_locate_failure, R.string.ui_activity_sign_map_picker_locate_button_return, R.string.ui_activity_sign_map_picker_locate_button_relocate, new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    finish();
                    return true;
                }
            }, new DialogHelper.OnDialogCancelListener() {
                @Override
                public void onCancel() {
                    tryFetchingLocation();
                }
            });
        } else {
            if (null != callback) {
                callback.onSuccess(address.getLongitude(), address.getLatitude(), address.getAddress());
            }
            resultData(Address.toJson(address));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //mBaiduMap.setOnMapClickListener(null);
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onPause时执行mMapView.onPause()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        callback = null;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onFetchingLocationComplete(boolean success, BaiduLocation location) {
        if (success) {
            addressView.setText(location.getAddress());
            address.setAddress(location.getAddress() + location.getDescribe());
            address.setLatitude(location.getLatitude());
            address.setLongitude(location.getLongitude());
            address.setAltitude(location.getAltitude());
            MyLocationData loc = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(loc);
            resetMapCenterPoint(location.getLatitude(), location.getLongitude());
            isLocated = true;
        }
    }

    /**
     * 设置并显示地图中心点
     */
    private void resetMapCenterPoint(double latitude, double longitude) {
        if (latitude == 0 || longitude == 0) {
            return;
        }
        LatLng pos = new LatLng(latitude, longitude);
        animateMapCenterPoint(pos);
        //tryReverseGeoCode(pos);
    }

    @Override
    protected void onReverseGeoCodeComplete(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (null == reverseGeoCodeResult || null == reverseGeoCodeResult.getLocation()) {
            addressView.setText(R.string.ui_activity_sign_map_picker_location_invalid);
            return;
        }
        LatLng latLng = reverseGeoCodeResult.getLocation();
        address.setLongitude(latLng.longitude);
        address.setLatitude(latLng.latitude);
        address.setAddress(reverseGeoCodeResult.getAddress() + reverseGeoCodeResult.getSematicDescription());
        addressView.setText(address.getAddress());
    }

    /**
     * 在地图上显示新的中心点
     */
    private void animateMapCenterPoint(LatLng position) {
        //设置地图中心点以及缩放级别
        MapStatusUpdate update;
        if (!isLocated) {
            // 第一次定到位置的时候地图动画移动到当前位置
            update = MapStatusUpdateFactory.newLatLngZoom(position, 16);
        } else {
            update = MapStatusUpdateFactory.newLatLng(position);
        }
        mBaiduMap.animateMapStatus(update);
    }

    private BaiduMap.OnMapStatusChangeListener mapChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            if (!isReduce) {
                // 不是重现地址时才改变选中的地址
                tryReverseGeoCode(mapStatus.target);
            }
        }
    };
}
