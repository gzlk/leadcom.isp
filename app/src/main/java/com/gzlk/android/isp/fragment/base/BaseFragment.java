package com.gzlk.android.isp.fragment.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.activity.ContainerActivity;
import com.gzlk.android.isp.activity.LoginActivity;
import com.gzlk.android.isp.activity.MainActivity;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.main.MainFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.hlk.hlklib.etc.Utility;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.param.HttpParamModel;
import com.litesuits.http.request.param.HttpRichParamModel;

import java.util.Locale;

/**
 * <b>功能描述：</b>提供一些基本方法的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/04 20:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/04 20:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseFragment extends BasePermissionHandleSupportFragment {

    /**
     * 空的json数组
     */
    public static final String EMPTY_ARRAY = "[]";
    /**
     * fragment的根UI
     */
    protected View mRootView;

    private Handler mHandler = new Handler();

    public Handler Handler() {
        return mHandler;
    }

    /**
     * 将字符串以半角逗号分隔成字符数组
     */
    protected static String[] splitParameters(String params) {
        if (params.charAt(0) == '[') {
            // json 对象
            return Json.gson().fromJson(params, new TypeToken<String[]>() {
            }.getType());
        } else {
            // 普通字符串
            return params.split(",", -1);
        }
    }

    /**
     * 格式化字符串
     */
    public String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }

    /**
     * 判断字符串是否为空，"null"也当作空
     */
    public boolean empty(String string) {
        return StringHelper.isEmpty(string);
    }

    /**
     * 获取颜色
     */
    public int getColor(int res) {
        return ContextCompat.getColor(Activity(), res);
    }

    /**
     * 获取dimention尺寸
     */
    public int getDimension(int res) {
        return Activity().getDimension(res);
    }

    /**
     * 获取integer值
     */
    public int getInteger(int res) {
        return Activity().getInteger(res);
    }

    /**
     * 获取字体大小，单位sp/dp
     */
    public int getFontDimension(int res) {
        return Utility.ConvertPx(getDimension(res));
    }

    /**
     * BackKey事件
     */
    public boolean onBackKeyEvent() {
        return false;
    }

    /**
     * 关闭Activity页面
     */
    public void finish() {
        finish(false);
    }

    /**
     * 返回上一页Activity并指定返回成功，以便上一页通过onActivityResult捕获消息
     */
    public void resultSucceededActivity() {
        resultData(null);
    }

    /**
     * 返回数据时的字段名称
     */
    public static final String RESULT_STRING = "_result_string_";

    /**
     * 返回上一页Activity并指定要返回的数据内容，以便上一页通过onActivityResult捕获消息
     */
    public void resultData(String data) {
        if (StringHelper.isEmpty(data)) {
            Activity().setResult(Activity.RESULT_OK);
        } else {
            Intent intent = new Intent();
            intent.putExtra(RESULT_STRING, data);
            Activity().setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    /**
     * 关闭Activity
     *
     * @param toMainUI true=直接返回主页面并且固定显示页，false=普通Activity关闭（受backState堆栈的顺序影响）
     */
    public void finish(boolean toMainUI) {
        // 这里固定为2，也即首页中
        finish(toMainUI ? 2 : -1);
    }

    /**
     * 直接返回到主页面，并且指定主页面要显示的页面index
     *
     * @param mainPageDisplayIndex 指定主页面中ViewPager要显示的index。小于0时跟普通Activity的backKey事件一样（并不一定能够返回到主页面）
     */
    public void finish(int mainPageDisplayIndex) {
        Utils.hidingInputBoard(mRootView);
        Activity().finish();
        if (mainPageDisplayIndex >= 0) {
            // 打开主UI
            Intent intent = new Intent(Activity(), MainActivity.class);

            Bundle bundle = new Bundle();
            // 这里默认返回到主页的时候都显示“找”页面
            bundle.putInt(MainFragment.PARAM_SELECTED, mainPageDisplayIndex);
            intent.putExtras(bundle);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * 回到登录页面
     */
    public void finishToSignIn() {
        // 先关闭本页面
        Activity().finish();

        Intent intent = new Intent(Activity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 不需要新打开的Activity返回结果
     */
    public static final int RESULT_NONE = -1;
    /**
     * 需求新打开的Activity返回结果
     */
    public static final int RESULT_BASE_REQUEST = 10000;
    /**
     * 需求照相机
     */
    public static final int REQUEST_CAMERA = RESULT_BASE_REQUEST + 1;
    /**
     * 需求相册
     */
    public static final int REQUEST_GALLERY = RESULT_BASE_REQUEST + 2;
    /**
     * 第三方相册预览
     */
    public static final int REQUEST_PREVIEW = RESULT_BASE_REQUEST + 3;
    /**
     * 需求剪切照片
     */
    public static final int REQUEST_CROP = RESULT_BASE_REQUEST + 4;

    /**
     * 启动容器Activity(此时打开的新Activity不需要返回确认)
     *
     * @param fullClassName  fragment的类全名
     * @param params         参数列表
     * @param supportToolbar 是否支持toolbar
     * @param supportBackKey 是否要处理backKey事件
     */
    public void openActivity(String fullClassName, String params, boolean supportToolbar, boolean supportBackKey) {
        openActivity(fullClassName, params, RESULT_NONE, supportToolbar, supportBackKey);
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
        openActivity(fullClassName, params, RESULT_NONE, supportToolbar, supportBackKey, transparentStatusBar);
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
        Intent intent = new Intent(Activity(), ContainerActivity.class);
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

    private LiteHttp http() {
        LiteHttp liteHttp = LiteHttp.build(Activity()).create();
        // 10秒网络超时
        liteHttp.getConfig().setConnectTimeout(10000);
        return liteHttp;
    }

    /**
     * 执行网络请求
     */
    protected void httpRequest(AbstractRequest request) {
        http().executeAsync(request);
    }

    /**
     * 执行网络请求
     */
    protected void httpRequest(HttpRichParamModel mode) {
        http().executeAsync(mode);
    }
}
