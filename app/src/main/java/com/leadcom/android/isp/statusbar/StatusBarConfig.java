package com.leadcom.android.isp.statusbar;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.DrawableRes;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/02 21:25 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class StatusBarConfig {

    public static int statusDrawable;
    public static int toolbarBackgroundColor;
    public static int toolbarBackgroundDrawable;
    public static int backDrawable;
    public static boolean isStatusBarLight;

    public static void setStatusBarDrawable(@DrawableRes int statusDraw) {
        statusDrawable = statusDraw;
    }

    public static boolean isStatusBar() {
        return statusDrawable > 0;
    }

    public static void setToolbarDrawable(int toolbarBackgroundDrawable) {
        StatusBarConfig.toolbarBackgroundDrawable = toolbarBackgroundDrawable;
    }

    public static void setBackDrawable(int backDrawable) {
        StatusBarConfig.backDrawable = backDrawable;
    }

    public static void setIsStatusBarLight(boolean isStatusBarLight) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            StatusBarConfig.statusDrawable = Color.parseColor("#33ffffff");
        }
        StatusBarConfig.isStatusBarLight = isStatusBarLight;
    }
}
