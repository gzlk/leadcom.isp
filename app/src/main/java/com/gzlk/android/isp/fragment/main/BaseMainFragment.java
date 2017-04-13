package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;

/**
 * <b>功能描述：</b>提供主页几个主要fragment相关方法的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/13 12:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/13 12:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseMainFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_INITIALIZED = "bmf_initialized";
    /**
     * fragment是否已经初始化了，为false时，需要设置顶部的padding
     */
    private boolean isInitialized = false;

    private boolean isCurrentDisplayed = false;

    /**
     * 设置是否当前所显示的内容
     */
    public void setCurrentDisplayed(boolean displayed) {
        isCurrentDisplayed = displayed;
    }

    protected boolean isCurrentDisplayed() {
        return isCurrentDisplayed;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        isInitialized = bundle.getBoolean(PARAM_INITIALIZED, false);
    }

    @Override
    protected boolean supportDefaultTitle() {
        return false;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putBoolean(PARAM_INITIALIZED, isInitialized);
    }

    /**
     * 设置内容布局的顶部padding以便正常显示
     */
    protected void tryPaddingContent() {
        tryPaddingContent(mRootView);
    }

    protected void tryPaddingContent(View view) {
        if (!isInitialized) {
            isInitialized = true;
            int statusBarHeight = BaseActivity.getStatusHeight(Activity());
            int actionBarHeight = Activity().getActionBarSize();
            int padding = actionBarHeight + statusBarHeight;
            int left = view.getPaddingLeft();
            int top = view.getPaddingTop();
            int right = view.getPaddingRight();
            int bottom = view.getPaddingBottom();
            if (top < padding) {
                view.setPadding(left, top + padding, right, bottom);
            }
        }
    }
}
