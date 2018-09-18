package com.leadcom.android.isp.fragment.base;

import android.os.Bundle;
import android.view.View;

import com.leadcom.android.isp.activity.BaseActivity;

/**
 * <b>功能描述：</b>提供Activity透明化方法的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/14 14:26 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/14 14:26 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseTransparentSupportFragment extends BaseLocationSupportFragment {

    private static final String PARAM_INITIALIZED = "bmf_initialized";
    protected static final String PARAM_QUERY_ID = "btsf_param_query";
    protected static final String PARAM_SEARCHED = "btsf_param_searching_text";
    protected static final String PARAM_JSON = "btsf_param_json";
    protected static final String PARAM_SELECTABLE = "btsf_param_selectable";
    /**
     * fragment是否已经初始化了，为false时，需要设置顶部的padding
     */
    private boolean isInitialized = false;
    /**
     * 基本queryId
     */
    protected String mQueryId;
    /**
     * 临时保存的json字符串[]
     */
    protected String mJsonString;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        isInitialized = bundle.getBoolean(PARAM_INITIALIZED, false);
        try {
            mQueryId = bundle.getString(PARAM_QUERY_ID, "");
            mJsonString = bundle.getString(PARAM_JSON, "");
        } catch (Exception ignore) {
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putBoolean(PARAM_INITIALIZED, isInitialized);
        try {
            bundle.putString(PARAM_QUERY_ID, mQueryId);
            bundle.putString(PARAM_JSON, mJsonString);
        } catch (Exception ignore) {
        }
    }

    /**
     * 标记fragment是否是在ViewPager里正显示的页面
     */
    private boolean isDisplayedInViewPager = false;

    /**
     * 设置是否当前所显示的内容
     */
    public void setViewPagerDisplayedCurrent(boolean displayed) {
        if (isDisplayedInViewPager != displayed) {
            isDisplayedInViewPager = displayed;
            onViewPagerDisplayedChanged(displayed);
        }
    }

    /**
     * 当前ViewPager显示的是否是本fragment
     */
    public boolean isViewPagerDisplayedCurrent() {
        return isDisplayedInViewPager;
    }

    /**
     * 当前页面在ViewPager中显示或隐藏事件
     */
    protected void onViewPagerDisplayedChanged(boolean visible) {

    }

    /**
     * 设置根内容布局的顶部padding以便正常显示
     */
    protected void tryPaddingContent(boolean includeActionBar) {
        tryPaddingContent(mRootView, includeActionBar);
    }

    /**
     * 设置指定布局的顶部padding以便正常显示
     */
    public void tryPaddingContent(View view, boolean includeActionBar) {
        if (!isInitialized) {
            isInitialized = true;
            int padding = BaseActivity.getStatusHeight(Activity());
            if (includeActionBar) {
                int actionBarHeight = Activity().getActionBarSize();
                padding += actionBarHeight;
            }
            int left = view.getPaddingLeft();
            int top = view.getPaddingTop();
            int right = view.getPaddingRight();
            int bottom = view.getPaddingBottom();
            if (top < padding) {
                view.setPadding(left, top + padding, right, bottom);
            }
        }
    }

    public void transparentStatusBar() {
        Activity().transparentStatusBar();
    }
}
