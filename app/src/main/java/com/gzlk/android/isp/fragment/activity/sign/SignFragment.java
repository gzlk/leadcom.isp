package com.gzlk.android.isp.fragment.activity.sign;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.activity.AppSignRecordRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseLocationSupportFragment;
import com.gzlk.android.isp.helper.BaiduHelper;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.activity.AppSignRecord;
import com.gzlk.android.isp.model.activity.AppSigning;
import com.gzlk.android.isp.model.common.BaiduLocation;
import com.gzlk.android.isp.nim.callback.SignCallback;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>签到页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/19 23:05 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/19 23:05 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignFragment extends BaseLocationSupportFragment {

    public static SignFragment newInstance(String params) {
        SignFragment sf = new SignFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 群聊的tid
        bundle.putString(PARAM_TID, strings[0]);
        // 签到应用app的id
        bundle.putString(PARAM_SIGN_ID, strings[1]);
        // 签到内容
        bundle.putString(PARAM_RECORD, strings[2]);
        sf.setArguments(bundle);
        return sf;
    }

    public static SignCallback callback;

    private static final String PARAM_TID = "sf_sign_tid";
    private static final String PARAM_SIGN_ID = "sf_sign_id";
    private static final String PARAM_RECORD = "sf_sign_record";
    private static final String PARAM_LOCATED = "sf_located";

    private String mTID, mSignId;
    private AppSignRecord record;
    private AppSigning signing;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        isLocated = bundle.getBoolean(PARAM_LOCATED, false);
        mTID = bundle.getString(PARAM_TID, "");
        mSignId = bundle.getString(PARAM_SIGN_ID, "");
        String json = bundle.getString(PARAM_RECORD, "");
        if (!isEmpty(json)) {
            record = AppSignRecord.fromJson(json);
        }
        createRecord();
    }

    private void createRecord() {
        if (null == record) {
            record = new AppSignRecord();
            record.setCreatorName(Cache.cache().userName);
            record.setSetupId(mSignId);
        }
    }

    private void getLocalSignRecord() {
        record = AppSignRecord.getMyRecord(mSignId);
        createRecord();
        // 未签到时按钮可用
        mSignButton.setEnabled(isEmpty(record.getId()));
        mSignedAddress.setText(record.getDesc());
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putBoolean(PARAM_LOCATED, isLocated);
        bundle.putString(PARAM_TID, mTID);
        bundle.putString(PARAM_SIGN_ID, mSignId);
        bundle.putString(PARAM_RECORD, AppSignRecord.toJson(record));
    }

    @ViewId(R.id.ui_map_picker_map_view)
    private MapView mMapView;
    @ViewId(R.id.ui_activity_sign_timer)
    private TextView mTimerView;
    @ViewId(R.id.ui_activity_sign_address)
    private TextView mAddress;
    @ViewId(R.id.ui_activity_signed_address)
    private TextView mSignedAddress;
    @ViewId(R.id.ui_activity_sign_button)
    private CorneredButton mSignButton;
    @ViewId(R.id.ui_activity_sign_end)
    private TextView mEndTime;

    private BaiduMap mBaiduMap;
    private boolean isLocated = false;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_sign;
    }

    @Override
    public void doingInResume() {
        if (isEmpty(mSignId)) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_invalid_sign_setup);
            mSignButton.setEnabled(false);
        } else {
            signing = new Dao<>(AppSigning.class).query(mSignId);
        }
        getLocalSignRecord();
        setCustomTitle(R.string.ui_nim_action_sign);

        if (isEmpty(record.getId())) {
            // 未签到时显示刷新按钮
            setRightIcon(R.string.ui_icon_refresh);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    if (!isEmpty(record.getId())) {
                        tryFetchingLocation(BaiduHelper.SI_3, false);
                    }
                }
            });
        }

        mEndTime.setText(getString(R.string.ui_activity_sign_end_time, (null == signing ? "" : signing.getEndTime())));
        if (null == mBaiduMap) {
            mBaiduMap = mMapView.getMap();
            //mBaiduMap.setOnMapStatusChangeListener(mapChangeListener);
        }
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        mBaiduMap.setMyLocationEnabled(true);
        // 不是复现签到位置时，开始定位
        if (isEmpty(record.getId()) && !isLocated) {
            // 3s一次定位返回
            tryFetchingLocation(BaiduHelper.SI_3, false);
        }
        reduceSignPoint();
    }

    private void reduceSignPoint() {
        if (!isEmpty(record.getId())) {
            MyLocationData loc = new MyLocationData.Builder()
                    .accuracy(0.0F)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(0.0F)
                    .latitude(Double.valueOf(record.getLat()))
                    .longitude(Double.valueOf(record.getLon())).build();
            mBaiduMap.setMyLocationData(loc);
            resetMapCenterPoint(Double.valueOf(record.getLat()), Double.valueOf(record.getLon()));
        }
    }

    @Click({R.id.ui_activity_sign_button})
    private void elementClick(View view) {
        if (view.getId() == R.id.ui_activity_sign_button) {
            stopFetchingLocation();
            if (!isLocated) {
                ToastHelper.make().showMsg(R.string.ui_activity_sign_map_picker_location_invalid);
            } else if (signing.couldSignable(record.getTime())) {
                // 将当前定位的位置当作签到位置
                warningSignPosition();
            }
        }
    }

    private void warningSignPosition() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_sign_warning, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                sign();
                return true;
            }
        }, null);
    }

    private void sign() {
        setLoadingText(R.string.ui_activity_sign_signing);
        displayLoading(true);
        AppSignRecordRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppSignRecord>() {
            @Override
            public void onResponse(AppSignRecord record, boolean success, String message) {
                super.onResponse(record, success, message);
                if (success) {
                    if (null != callback) {
                        callback.onSuccess();
                    }
                    finish();
                }
            }
        }).add(record);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopFetchingLocation();
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
            createRecord();
            record.setTime(location.getTime());
            record.setLon(String.valueOf(location.getLongitude()));
            record.setLat(String.valueOf(location.getLatitude()));
            record.setAddress(location.getAddress() + location.getDescribe());
            record.setAlt(String.valueOf(location.getAltitude()));
            mTimerView.setText(formatDateTime(location.getTime()));
            mAddress.setText(record.getAddress());
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

}
