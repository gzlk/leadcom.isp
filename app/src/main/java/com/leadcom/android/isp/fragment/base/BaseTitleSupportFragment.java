package com.leadcom.android.isp.fragment.base;

import android.app.Activity;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.TitleActivity;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;

/**
 * <b>功能描述：</b>提供Activity页面标题栏相关属性读写的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 13:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 13:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseTitleSupportFragment extends BaseFragment {

    /**
     * 设置默认的标题栏事件处理
     */
    public void setDefaultTitleClickEvent() {
        Activity().setResult(Activity.RESULT_CANCELED);
        if (Activity() != null) {
            setLeftText(R.string.ui_base_text_back);
            setLeftTitleClickListener(new OnTitleButtonClickListener() {

                @Override
                public void onClick() {
                    if (!onBackKeyEvent()) {
                        finish(false);
                    }
                }
            });
        }
    }

    /**
     * 设置顶部的标题栏是否可见
     */
    public void enableToolbar(boolean enable) {
        ((TitleActivity) Activity()).showToolbar(enable);
    }

    /**
     * 设置标题栏左侧图标
     */
    public void setLeftIcon(int resId) {
        ((TitleActivity) Activity()).setLeftIcon(resId);
    }

    /**
     * 设置标题栏左侧图标
     */
    public void setLeftIcon(String text) {
        ((TitleActivity) Activity()).setLeftIcon(text);
    }

    /**
     * 设置标题栏左边文字
     */
    public void setLeftText(int resId) {
        ((TitleActivity) Activity()).setLeftText(resId);
    }

    /**
     * 设置标题栏左边文字
     */
    public void setLeftText(String text) {
        ((TitleActivity) Activity()).setLeftText(text);
    }

    /**
     * 设置标题栏左边文字大小
     */
    public void setLeftTextSize(float size) {
        ((TitleActivity) Activity()).setLeftTextSize(size);
    }

    /**
     * 设置中间标题文字
     */
    public void setCustomTitle(int resId) {
        ((TitleActivity) Activity()).setCustomTitle(resId);
    }

    /**
     * 设置中间标题文字
     */
    public void setCustomTitle(String title) {
        ((TitleActivity) Activity()).setCustomTitle(title);
    }

    /**
     * 设置小标题文字
     */
    public void setSubTitle(int resId) {
        ((TitleActivity) Activity()).setSubTitle(resId);
    }

    /**
     * 设置小标题文字
     */
    public void setSubTitle(String title) {
        ((TitleActivity) Activity()).setSubTitle(title);
    }

    /**
     * 设置标题栏右边文字
     */
    public void setRightText(int resId) {
        ((TitleActivity) Activity()).setRightText(resId);
    }

    /**
     * 设置标题栏右边文字
     */
    public void setRightText(String text) {
        ((TitleActivity) Activity()).setRightText(text);
    }

    /**
     * 设置标题栏右边文字大小
     */
    public void setRightTextSize(float size) {
        ((TitleActivity) Activity()).setRightTextSize(size);
    }

    /**
     * 设置标题栏右侧图标
     */
    public void setRightIcon(int resId) {
        ((TitleActivity) Activity()).setRightIcon(resId);
    }

    /**
     * 设置标题栏右侧图标
     */
    public void setRightIcon(String text) {
        ((TitleActivity) Activity()).setRightIcon(text);
    }

    /**
     * 设置左边标题栏点击事件回调
     */
    public void setLeftTitleClickListener(OnTitleButtonClickListener l) {
        ((TitleActivity) Activity()).setLeftTitleClickListener(l);
    }

    /**
     * 设置右边标题栏点击事件回调
     */
    public void setRightTitleClickListener(OnTitleButtonClickListener l) {
        ((TitleActivity) Activity()).setRightTitleClickListener(l);
    }
}
