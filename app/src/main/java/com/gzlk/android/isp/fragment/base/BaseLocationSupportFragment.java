package com.gzlk.android.isp.fragment.base;

import com.gzlk.android.isp.helper.BaiduHelper;
import com.gzlk.android.isp.model.BaiduLocation;

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
        fetchingLocateWithBaiduApi(interval, stoppable);
    }

    /**
     * 初始化百度定位
     */
    private void fetchingLocateWithBaiduApi(int interval, boolean stoppable) {
        displayLoading(true);
        // 先停止已经开始的定位过程
        BaiduHelper.Instance().stop();
        BaiduHelper.Instance().stopWhenLocated(stoppable)
                .setScanInterval(interval).addOnLocatedListener(locatedListener).start();
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                displayLoading(false);
            }
        }, 10000);
    }

    private BaiduHelper.OnLocatedListener locatedListener = new BaiduHelper.OnLocatedListener() {
        @Override
        public void onLocated(boolean success, BaiduLocation location) {
            if (!success) {
                BaiduHelper.Instance().stop();
            }
            displayLoading(false);
            onFetchingLocationComplete(success, location);
        }
    };

    /**
     * 百度地图定位返回，子类需要重载该方法进行定位结果获取
     */
    protected void onFetchingLocationComplete(boolean success, BaiduLocation location) {

    }
}
