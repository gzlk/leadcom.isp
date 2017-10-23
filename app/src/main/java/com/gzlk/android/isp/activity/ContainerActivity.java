package com.gzlk.android.isp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.SimpleDialogHelper;

import java.lang.reflect.Method;

/**
 * <b>功能描述：</b>容器Activity<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 13:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 13:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ContainerActivity extends TitleActivity {

    /**
     * 默认新建fragment静态方法名
     */
    private static final String STATIC_METHOD_NAME = "newInstance";

    private BaseFragment mFragment = null;
    private String mClass = "", mParams = "";
    /**
     * 是否需要处理BackKey事件
     */
    private boolean isBackKeySupported = false;

    private void initParams(Bundle bundle) {
        mClass = bundle.getString(REQUEST_CLASS);
        mParams = bundle.getString(REQUEST_PARAMS);
        isToolbarSupported = bundle.getBoolean(REQUEST_TOOL_BAR, true);
        // 默认不需要处理backkey
        isBackKeySupported = bundle.getBoolean(REQUEST_BACK_KEY, false);
        isInputSupported = bundle.getBoolean(REQUEST_INPUT, false);
        supportTransparentStatusBar = bundle.getBoolean(REQUEST_TRANSPARENT_STATUS_BAR, false);
        if (isBackKeySupported) {
            // 需要处理返回按键事件时，必定是需要用户输入的
            isInputSupported = true;
        }
    }

    @Override
    protected void initializeParams(Bundle bundle) {
        super.initializeParams(bundle);
        if (null != bundle) {
            initParams(bundle);
        } else {
            Intent intent = getIntent();
            if (null != intent) {
                Bundle b = intent.getBundleExtra(EXTRA_BUNDLE);
                if (null != b) {
                    initParams(b);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 微博分享时，需要这个方法进行回调
        if (null != mFragment) {
            mFragment.onNewIntent(intent);
        }
    }

    @Override
    protected boolean onBackKeyEvent(int keyCode, KeyEvent event) {
        if (null != mFragment) {
            if (mFragment.onBackKeyEvent()) {
                return true;
            }
        }
        return super.onBackKeyEvent(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null == outState) {
            outState = new Bundle();
        }
        outState.putString(REQUEST_CLASS, mClass);
        outState.putString(REQUEST_PARAMS, mParams);
        outState.putBoolean(REQUEST_TOOL_BAR, isToolbarSupported);
        outState.putBoolean(REQUEST_BACK_KEY, isBackKeySupported);
        outState.putBoolean(REQUEST_INPUT, isInputSupported);
        outState.putBoolean(REQUEST_TRANSPARENT_STATUS_BAR, supportTransparentStatusBar);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeFragment();
    }

    private void initializeFragment() {
        if (null == mFragment && null != mClass)
            mFragment = getFragment();
        if (null != mFragment) {
            setMainFrameLayout(mFragment);
        } else {
            SimpleDialogHelper.init(this).show(R.string.ui_warning_none_fragment_will_display);
        }
    }

    @SuppressWarnings("unchecked")
    private BaseFragment getFragment() {
        try {
            Class clazz = Class.forName(mClass);
            Method method = null;
            try {
                method = clazz.getMethod(STATIC_METHOD_NAME, String.class);
            } catch (Exception ignore) {
            }
            if (null != method) {
                return (BaseFragment) method.invoke(null, mParams);
            } else {
                return (BaseFragment) clazz.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
