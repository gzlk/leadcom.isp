package com.gzlk.android.isp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.helper.LogHelper;

import static com.gzlk.android.isp.fragment.base.BaseFragment.RESULT_BASE_REQUEST;

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
    /**
     * 是否需要透明化状态栏
     */
    public static final String REQUEST_TRANSPARENT_STATUS_BAR = "_request_transparent_status_bar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        app().removeActivity(Integer.toHexString(this.hashCode()));
        log(String.format("Activity '%s' is now destroy", getClass().getSimpleName()));
        super.onDestroy();
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
     * 获取ActionBar的高度
     */
    public int getActionBarSize() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return 0;
    }

    /**
     * 获取全局Application对象
     */
    public App app() {
        return (App) getApplicationContext();
    }

    /**
     * 获取dimension尺寸
     */
    public int getDimension(int res) {
        return 0 == res ? 0 : app().getResources().getDimensionPixelOffset(res);
    }

    /**
     * 获取integer值
     */
    public int getInteger(int res) {
        return 0 == res ? 0 : app().getResources().getInteger(res);
    }

    protected void log(String string) {
        LogHelper.log(getClass().getSimpleName(), string);
    }


    /**
     * 启动容器Activity(此时打开的新Activity不需要返回确认)
     *
     * @param fullClassName  fragment的类全名
     * @param params         参数列表
     * @param supportToolbar 是否支持toolbar
     * @param supportBackKey 是否要处理backKey事件
     */
    public void openActivity(String fullClassName, String params, boolean supportToolbar, boolean supportBackKey) {
        openActivity(fullClassName, params, RESULT_BASE_REQUEST, supportToolbar, supportBackKey);
    }

    /**
     * 启动容器Activity
     *
     * @param fullClassName  fragment的类全名
     * @param params         参数列表
     * @param requestCode    请求码
     * @param supportToolbar 是否支持toolbar
     * @param supportBackKey 是否要处理backKey事件
     */
    public void openActivity(String fullClassName, String params, int requestCode, boolean supportToolbar, boolean supportBackKey) {
        openActivity(fullClassName, params, requestCode, supportToolbar, supportBackKey, false);
    }

    /**
     * 启动容器Activity(此时打开的新Activity不需要返回确认)
     *
     * @param fullClassName        fragment的类全名
     * @param params               参数列表
     * @param supportToolbar       是否支持toolbar
     * @param supportBackKey       是否要处理backKey事件
     * @param transparentStatusBar 是否需要状态栏透明化
     */
    public void openActivity(String fullClassName, String params, boolean supportToolbar, boolean supportBackKey, boolean transparentStatusBar) {
        openActivity(fullClassName, params, RESULT_BASE_REQUEST, supportToolbar, supportBackKey, transparentStatusBar);
    }

    /**
     * 启动容器Activity
     *
     * @param fullClassName        fragment的类全名
     * @param params               参数列表
     * @param requestCode          请求码
     * @param supportToolbar       是否支持toolbar
     * @param supportBackKey       是否要处理backKey事件
     * @param transparentStatusBar 是否需要状态栏透明化
     */
    public void openActivity(String fullClassName, String params, int requestCode, boolean supportToolbar, boolean supportBackKey, boolean transparentStatusBar) {
        Intent intent = new Intent(this, ContainerActivity.class);
        Bundle b = new Bundle();
        b.putInt(ContainerActivity.REQUEST_CODE, requestCode);
        b.putString(ContainerActivity.REQUEST_CLASS, fullClassName);
        b.putString(ContainerActivity.REQUEST_PARAMS, params);
        b.putBoolean(ContainerActivity.REQUEST_TOOL_BAR, supportToolbar);
        b.putBoolean(ContainerActivity.REQUEST_BACK_KEY, supportBackKey);
        b.putBoolean(ContainerActivity.REQUEST_TRANSPARENT_STATUS_BAR, transparentStatusBar);
        intent.putExtra(ContainerActivity.EXTRA_BUNDLE, b);
        startActivityForResult(intent, requestCode);
    }
}
