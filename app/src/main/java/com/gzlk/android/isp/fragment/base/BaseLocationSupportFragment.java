package com.gzlk.android.isp.fragment.base;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.gzlk.android.isp.helper.GaodeHelper;
import com.gzlk.android.isp.helper.OnLocatedListener;
import com.gzlk.android.isp.model.common.HLKLocation;

/**
 * <b>功能描述：</b>提供定位相关api的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/23 19:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/23 19:43 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseLocationSupportFragment extends BaseNothingLoadingSupportFragment {

    /**
     * 启动百度地图进行定位，定位成功之后立即自动停止
     */
    protected void tryFetchingLocation() {
        tryFetchingLocation(0, true);
    }

    /**
     * 启动百度地图进行定位，定位成功之后立即自动停止
     */
    protected void tryFetchingLocation(int interval, boolean stoppable) {
        displayNothing(false);
        fetchingLocateWithGaodeApi(interval, stoppable);
    }

    protected void stopFetchingLocation() {
        GaodeHelper.instance().stop();
    }

    /**
     * 初始化百度定位
     */
    private void fetchingLocateWithGaodeApi(int interval, boolean stoppable) {
        // 先停止已经开始的定位过程
        GaodeHelper.instance().stop();
        GaodeHelper.instance().showDebug(true)
                .stopWhenLocated(stoppable)
                .setScanSpan(interval)
                .addOnLocatedListener(locatedListener)
                .start();
    }

    private OnLocatedListener locatedListener = new OnLocatedListener() {
        @Override
        public void onLocated(boolean success, HLKLocation location) {
            if (!success) {
                GaodeHelper.instance().stop();
            }
            onFetchingLocationComplete(success, location);
        }
    };

    /**
     * 百度地图定位返回，子类需要重载该方法进行定位结果获取
     */
    protected void onFetchingLocationComplete(boolean success, HLKLocation location) {

    }


    //***********************************反转地址服务
    // 地址反转服务
    protected GeocodeSearch mGeoCoder;

    /**
     * 反转编码地理位置
     */
    protected void tryReverseGeoCode(LatLonPoint location) {
        if (null == mGeoCoder) {
            // 创建GeoCoder实例对象
            mGeoCoder = new GeocodeSearch(Activity());
            // 设置查询结果监听者
            mGeoCoder.setOnGeocodeSearchListener(onGeocodeSearchListener);
        }
        // 发起反地理编码请求(经纬度->地址信息)
        RegeocodeQuery query = new RegeocodeQuery(location, 200, GeocodeSearch.AMAP);
        mGeoCoder.getFromLocationAsyn(query);
    }

    private GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener = new GeocodeSearch.OnGeocodeSearchListener() {

        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            onReverseGeoCodeComplete(regeocodeResult);
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

        }
    };

    /**
     * 反转地址编码的回调
     */
    protected void onReverseGeoCodeComplete(RegeocodeResult regeocodeResult) {
    }

}
