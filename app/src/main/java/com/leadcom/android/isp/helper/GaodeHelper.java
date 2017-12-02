package com.leadcom.android.isp.helper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.amap.api.maps2d.model.LatLng;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.model.common.HLKLocation;

import java.lang.ref.SoftReference;

/**
 * <b>功能描述：</b>高德地图定位Helper<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/16 22:23 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/16 22:23 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GaodeHelper {

    private static final String TAG = "GaodeHelper";
    // 默认定位次数为10次后停止定位
    private static final int LOCATE_TIMER = 10;
    private static GaodeHelper instance;

    /**
     * 计算两个点之间的直线距离
     */
    public static double getDistance(LatLng point1, LatLng point2) {
        DPoint p1 = new DPoint(point1.latitude, point1.longitude);
        DPoint p2 = new DPoint(point2.latitude, point2.longitude);
        return CoordinateConverter.calculateLineDistance(p1, p2);
    }

    /**
     * 全局实例
     */
    public static GaodeHelper instance() {
        if (null == instance) {
            instance = new GaodeHelper();
        }
        return instance;
    }

    private GaodeHelper() {
        mLocationClient = new AMapLocationClient(App.app());
        initializeOptions();
    }

    private AMapLocationClient mLocationClient = null;
    private AMapLocationListener mLocationListener = new MyLocationListener();

    /**
     * 设置定位参数
     */
    private void initializeOptions() {
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        // 设置定位模式为AMapLocationMode.Battery_Saving
        // 低功耗模式：不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位）。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        // 获取一次定位结果：
        // 该方法默认为false。
        mLocationOption.setOnceLocation(true);
        // 获取最近3s内精度最高的一次定位结果：
        // 设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        // 设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(scanSpan);

        // 设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        // 设置是否强制刷新WIFI，默认为true，强制刷新。
        mLocationOption.setWifiScan(false);

        // 设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);

        // 单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);

        // 关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);

        // 给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    private class MyLocationListener implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (showLocation) {
                LogHelper.log(TAG, null == aMapLocation ? "null location" : aMapLocation.toString());
            }
            boolean located = null != aMapLocation && aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS;
            if (!located) {
                if (null != aMapLocation) {
                    LogHelper.log(TAG, "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
                locateTimes--;
            }
            // 定位失败超时后停止
            if (locateTimes <= 0) {
                LogHelper.log(TAG, "Gaode helper stopped, locate times: " + locateTimes + ", located: " + located);
                // 这里不需要handle了，否则会陷入死循环
                //handleMessage(location, false);
                stop();
            }

            if (located) {
                handleMessage(aMapLocation, true);

                if (stopWhenLocated) {
                    // 定位成功后停止
                    stop();
                }
            }
        }

        private void handleMessage(AMapLocation location, boolean located) {
            if (null != mHandler) {
                // 通知handler进行处理
                Message msg = mHandler.obtainMessage(LOCATE_TIMER);
                Bundle bundle;
                if (null == msg.getData()) {
                    bundle = new Bundle();
                } else {
                    bundle = msg.getData();
                }
                bundle.putBoolean(InnerHandler.FLAG, located);
                bundle.putParcelable(InnerHandler.DATA, new HLKLocation(location));
                mHandler.sendMessage(msg);
            }
        }
    }

    private static InnerHandler mHandler = null;

    private static class InnerHandler extends Handler {
        private static final String FLAG = "flag";
        private static final String DATA = "data";
        private SoftReference<OnLocatedListener> listener;

        InnerHandler(OnLocatedListener l) {
            listener = new SoftReference<>(l);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOCATE_TIMER:
                    OnLocatedListener l = listener.get();
                    if (null != l) {
                        Bundle bundle = msg.getData();
                        l.onLocated(bundle.getBoolean(FLAG, false), (HLKLocation) bundle.getParcelable(DATA));
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private int locateTimes;

    /**
     * 开始定位
     */
    public void start() {
        if (null == mHandler) {
            mHandler = new InnerHandler(mOnLocatedListener);
        }

        locateTimes = LOCATE_TIMER;
        if (null != mLocationClient) {
            mLocationClient.setLocationListener(mLocationListener);
            mLocationClient.startLocation();
        }
    }

    private void performStop() {
        try {
            if (null != mLocationClient) {
                mLocationClient.stopLocation();
                mLocationClient.unRegisterLocationListener(mLocationListener);
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    /**
     * 停止定位
     */
    public void stop() {
        performStop();
        mHandler = null;
        // 停止之后释放listener
        mOnLocatedListener = null;
    }

    /**
     * 定位时间间隔：1秒
     */
    public static final int SI_1 = 1000;
    /**
     * 定位时间间隔：3秒
     */
    public static final int SI_3 = 3000;
    /**
     * 定位时间间隔：5秒
     */
    public static final int SI_5 = 5000;

    private int scanSpan = SI_3;

    /**
     * 设置连续定位时间间隔
     */
    public GaodeHelper setScanSpan(int span) {
        scanSpan = span;
        if (scanSpan <= SI_1) {
            scanSpan = SI_1;
        }
        initializeOptions();
        return this;
    }

    private boolean showLocation = false;

    /**
     * 是否显示定位debug信息
     */
    public GaodeHelper showDebug(boolean shown) {
        showLocation = shown;
        return this;
    }

    private boolean stopWhenLocated = true;

    /**
     * 设置是否在定位成功之后自动停止获取定位
     */
    public GaodeHelper stopWhenLocated(boolean stopWhenLocated) {
        this.stopWhenLocated = stopWhenLocated;
        return this;
    }

    private OnLocatedListener mOnLocatedListener;

    /**
     * 设置定位成功之后的回调处理
     */
    public GaodeHelper addOnLocatedListener(OnLocatedListener l) {
        mOnLocatedListener = l;
        return this;
    }
}
