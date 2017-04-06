package com.gzlk.android.isp.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gzlk.android.isp.application.App;

/**
 * <b>功能描述：</b>Activity 基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/04 19:22 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/04 19:22 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_BUNDLE = "_bundle_";
    /**
     * 请求的CODE码
     */
    public static final String REQUEST_CODE = "_request_code_";
    /**
     * 需要打开的fragment的class名称
     */
    public static final String REQUEST_CLASS = "_request_class_";
    /**
     * 请求的参数列表
     */
    public static final String REQUEST_PARAMS = "_request_params_";
    /**
     * 是否支持toolbar显示
     */
    public static final String REQUEST_TOOL_BAR = "_request_tool_bar_";
    /**
     * 是否需要处理返回按键事件
     */
    public static final String REQUEST_BACK_KEY = "_request_back_key_";
    /**
     * 是否需要手动输入
     */
    public static final String REQUEST_INPUT = "_request_input_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 设置状态栏透明并且界面延伸到状态栏后面，api>=19才有效果
     */
    public void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= 19) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置View的padding高度
     */
    public void setRootViewPadding(View view, boolean needTopPadding) {
        if (Build.VERSION.SDK_INT >= 19) {
            int padding = needTopPadding ? getStatusHeight(this) : 0;
            view.setPadding(0, padding, 0, 0);
        }
    }

    /**
     * 状态栏高度算法
     *
     * @param activity Activity
     * @return 返回当前页面的状态栏高度
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 获取全局Application对象
     */
    public App app() {
        return (App) getApplicationContext();
    }
}
