package com.leadcom.android.isp.fragment.base;

import android.support.annotation.IntDef;

/**
 * <b>功能描述：</b>提供延迟加载刷新方法的fragment<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/12 11:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/12 11:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseDelayRefreshSupportFragment extends BaseImageSelectableSupportFragment {

    private static final long STATIC_DELAY_TIME = 500;
    /**
     * 页面加载方式
     */
    protected static final int DELAY_TYPE_PAGE_LOADING = 0;
    /**
     * 手动延时方式
     */
    protected static final int DELAY_TYPE_TIME_DELAY = 1;

    /**
     * 延迟刷新类型
     */
    @IntDef({DELAY_TYPE_PAGE_LOADING, DELAY_TYPE_TIME_DELAY})
    public @interface DelayType {
    }

    public void delayRefreshLoading(long delayTime, @DelayType final int delayType) {
        displayLoading(true);
        displayNothing(false);
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onDelayRefreshComplete(delayType);
                displayLoading(false);
            }
        }, delayTime);
    }

    protected abstract void onDelayRefreshComplete(@DelayType int type);

    /**
     * 延时显示loading界面
     */
    public void delayRefreshLoading() {
        delayRefreshLoading(STATIC_DELAY_TIME);
    }

    /**
     * 延时显示loading界面
     */
    public void delayRefreshLoading(long delayTime) {
        delayRefreshLoading(delayTime, DELAY_TYPE_PAGE_LOADING);
    }
}
